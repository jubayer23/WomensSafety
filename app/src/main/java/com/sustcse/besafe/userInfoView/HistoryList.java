package com.sustcse.besafe.userInfoView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sustcse.besafe.MapActivity;
import com.sustcse.besafe.R;
import com.sustcse.besafe.adapter.HistoryListAdapter;
import com.sustcse.besafe.appdata.AppConstant;
import com.sustcse.besafe.appdata.AppController;
import com.sustcse.besafe.model.History;
import com.sustcse.besafe.sharedprefs.SaveManager;
import com.sustcse.besafe.utils.GPSTracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by comsol on 26-Dec-15.
 */
public class HistoryList extends AppCompatActivity implements AbsListView.OnScrollListener {


    private ProgressBar progressBar, progressBar_listviewBottom;

    private ListView listView;

    private GPSTracker gps;
    private SaveManager saveManager;

    private Gson gson;

    private List<History> historyList;

    private HistoryListAdapter historyListAdapter;

    private static boolean endOfHistory = false;

    private static int historyPageNum = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historylist);

        getSupportActionBar().setHomeButtonEnabled(true);

        init();

        endOfHistory = false;


        if (historyListAdapter == null) {
            historyListAdapter = new HistoryListAdapter(
                    HistoryList.this, AppConstant.histories);
            listView.setAdapter(historyListAdapter);

        } else {
            historyListAdapter.addMore();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                AppConstant.histories.get(position).setSeen("true");

                History history = AppConstant.histories.get(position);

                String lat = history.getLat();

                String lng = history.getLng();

                String event_id = history.getEvent_id();

                Intent intent = new Intent(HistoryList.this, MapActivity.class);

                intent.putExtra("lattitude", Double.parseDouble(lat));
                intent.putExtra("langitude", Double.parseDouble(lng));
                intent.putExtra("event_id", Integer.parseInt(event_id));

                startActivity(intent);


            }
        });

        listView.setOnScrollListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyListAdapter.addMore();
    }

    private void init() {
        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        progressBar_listviewBottom = (ProgressBar) findViewById(R.id.loadingProgressBar_listview);

        listView = (ListView) findViewById(R.id.listView_history);

        historyList = new ArrayList<History>();
        historyList.clear();

        gson = new Gson();

        gps = new GPSTracker(this);

        saveManager = new SaveManager(this);
    }

    private void showOrHideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        } else
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if (listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 &&
                listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight()  && !endOfHistory && (progressBar_listviewBottom.getVisibility() != View.VISIBLE)) {
            showBottomProgressBar(true);
            sendRequestToServerForHistoryFetch(AppConstant.getUrlForHistoryList(saveManager.getUserId(), saveManager.getLat(), saveManager.getLng(), saveManager.getUserNotificationRange(),++historyPageNum));
            //It is scrolled all the way down here
            //Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    private void showBottomProgressBar(boolean flag) {
        if (flag) progressBar_listviewBottom.setVisibility(View.VISIBLE);
        else
            progressBar_listviewBottom.setVisibility(View.GONE);
    }

    public void sendRequestToServerForHistoryFetch(String sentUrl) {

       // Log.d("DEBUG_history_url",sentUrl);

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();

                        if(response.equalsIgnoreCase("[]"))
                        {
                            endOfHistory = true;
                            Toast.makeText(HistoryList.this, "No Data To Load...", Toast.LENGTH_SHORT).show();
                        }

                        try {
                            JSONArray jsonArray = new JSONArray(response);


                            //AppConstant.histories.clear();
                           // AppConstant.NUM_OF_UNSEEN_HISTORY = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);

                                History history = gson.fromJson(tempObject.toString(), History.class);
                                if (history.getSeen().equalsIgnoreCase("false"))
                                    AppConstant.NUM_OF_UNSEEN_HISTORY++;

                                AppConstant.histories.add(history);

                            }

                            if (historyListAdapter == null) {
                                historyListAdapter = new HistoryListAdapter(
                                        HistoryList.this, AppConstant.histories);
                                listView.setAdapter(historyListAdapter);

                            } else {
                                historyListAdapter.addMore();
                            }

                            //Collections.reverse(AppConstant.histories);





                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showBottomProgressBar(false);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showBottomProgressBar(false);
            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }

}
