package com.sustcse.besafe.userInfoView;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen);
        init();
    }

    private void init() {
        gson = new Gson();
        seenListView = (ListView) findViewById(R.id.listView_seen);
        prepareList();
        seenListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, seenList);
        seenListView.setAdapter(seenListAdapter);
        seenListView.setEnabled(false);
    }

    private void prepareList() {
        seenList = new ArrayList<>();
//        String device_id = "00000000-70d7-384d-0607-ee8105dec69f";
        String device_id = new SaveManager(this).getDeviceId();
        sendRequestToServerForLastSeenFetch(AppConstant.getLastSeenUrl(device_id));

    }

    private void sendRequestToServerForLastSeenFetch(String sentUrl) {

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        response = response.trim();

                        try {
                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);

                                if (tempObject.has("name")) {
                                    seenList.add(tempObject.getString("name"));
                                    seenListAdapter.notifyDataSetChanged();
                                }
                            }
                            if (seenListAdapter.getCount() > 0) {
                                Toast.makeText(LastSeen.this, "These people have seen your last alert message", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LastSeen.this, "Your last alert message was not seen by anyone", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });


        AppController.getInstance().addToRequestQueue(req);

    }
}
