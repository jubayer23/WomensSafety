package com.creative.womenssafety.userInfoView;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.creative.womenssafety.MapsActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.adapter.HistoryListAdapter;
import com.creative.womenssafety.adapter.PoliceListAdapter;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.model.History;
import com.creative.womenssafety.model.Police;
import com.creative.womenssafety.sharedprefs.SaveManager;
import com.creative.womenssafety.utils.GPSTracker;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by comsol on 26-Dec-15.
 */
public class HistoryList extends AppCompatActivity {


    private ProgressBar progressBar;

    private ListView listView;

    private GPSTracker gps;
    private SaveManager saveManager;

    private Gson gson;

    private List<History> historyList;

    private HistoryListAdapter historyListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historylist);

        getSupportActionBar().setHomeButtonEnabled(true);

        init();


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

                Intent intent = new Intent(HistoryList.this, MapsActivity.class);

                intent.putExtra("lattitude",Double.parseDouble(lat));
                intent.putExtra("langitude",Double.parseDouble(lng));
                intent.putExtra("event_id",Integer.parseInt(event_id));

                startActivity(intent);



            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        historyListAdapter.addMore();
    }

    private void init() {
        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
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
}
