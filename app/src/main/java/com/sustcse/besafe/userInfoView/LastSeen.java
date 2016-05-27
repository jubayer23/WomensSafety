package com.sustcse.besafe.userInfoView;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sustcse.besafe.R;
import com.sustcse.besafe.appdata.AppConstant;
import com.sustcse.besafe.appdata.AppController;
import com.sustcse.besafe.sharedprefs.SaveManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LastSeen extends AppCompatActivity {

    private ListView seenListView;
    private ArrayAdapter seenListAdapter;
    private ArrayList<String> seenList;
    private Gson gson;
    private ProgressDialog pDialog;
    private TextView tv_error_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen);

        init();

        prepareList();

        seenListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, seenList);
        seenListView.setAdapter(seenListAdapter);
        seenListView.setEnabled(false);
    }

    private void init() {
        pDialog = new ProgressDialog(LastSeen.this);
        pDialog.setMessage("Loading.... Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);

        tv_error_message = (TextView)findViewById(R.id.tv_error_message);
        tv_error_message.setVisibility(View.INVISIBLE);


        gson = new Gson();
        seenListView = (ListView) findViewById(R.id.listView_seen);



    }

    private void prepareList() {
        seenList = new ArrayList<>();
//        String device_id = "00000000-70d7-384d-0607-ee8105dec69f";
        String device_id = new SaveManager(this).getDeviceId();
        sendRequestToServerForLastSeenFetch(AppConstant.getLastSeenUrl(device_id));

    }

    private void sendRequestToServerForLastSeenFetch(String sentUrl) {
        pDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.dismiss();

                        response = response.trim();

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);

                                if (tempObject.has("name")) {
                                    seenList.add(tempObject.getString("name"));

                                }
                            }
                            seenListAdapter.notifyDataSetChanged();
                            if (seenListAdapter.getCount()<= 0) {
                                seenListView.setVisibility(View.INVISIBLE);
                                tv_error_message.setVisibility(View.VISIBLE);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            pDialog.dismiss();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                pDialog.dismiss();
            }
        });


        AppController.getInstance().addToRequestQueue(req);

    }

    public void finishActivity(View view)
    {
        finish();
    }
}
