package com.creative.womenssafety.userInfoView;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.womenssafety.R;
import com.creative.womenssafety.adapter.PoliceListAdapter;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.model.Police;
import com.creative.womenssafety.sharedprefs.SaveManager;
import com.creative.womenssafety.utils.GPSTracker;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by comsol on 20-Dec-15.
 */
public class HospitalInfo extends AppCompatActivity {

    private ProgressBar progressBar;

    private ListView listView;

    private GPSTracker gps;
    private SaveManager saveManager;

    private String region;

    private List<Police> polices;

    Gson gson;

    private PoliceListAdapter policeListAdapter;

    public static String police_OR_hospital = "hospital";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policeinfo);

        police_OR_hospital = "hospital";

        init();

        showOrHideProgressBar();

        gps = new GPSTracker(this);

        saveManager = new SaveManager(this);


        try {
            region = getLocationDetails(saveManager.getLat(),saveManager.getLng());
        } catch (Exception e) {
            region = "sylhet";
        }


        sendRequestToServer(AppConstant.getUrlForHospitalInfo(region));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Police police = polices.get(position);


                if (!police.getNumber().equals("")) {
                    Uri number = Uri.parse("tel:" + police.getNumber());
                    Intent dial = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(dial);
                }


            }
        });


    }


    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        listView = (ListView) findViewById(R.id.listView_police);

        polices = new ArrayList<Police>();
        polices.clear();

        gson = new Gson();
    }

    public void sendRequestToServer(String sentUrl) {

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);

                                Police police = gson.fromJson(tempObject.toString(), Police.class);


                                polices.add(police);

                            }

                            showOrHideProgressBar();

                            if (policeListAdapter == null) {
                                policeListAdapter = new PoliceListAdapter(
                                        HospitalInfo.this, polices);
                                listView.setAdapter(policeListAdapter);

                            } else {
                                policeListAdapter.addMore();
                            }

                           // Toast.makeText(getApplicationContext(), "SEND",
                            //        Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                showOrHideProgressBar();


            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }

    private String getLocationDetails(double lat, double lang) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String add = "";

        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String city = "sylhet";
        if (addresses != null) {
            city = addresses.get(0).getLocality();

            if (city == null || city.isEmpty()) city = "sylhet";
            else city = city.toLowerCase();
        }

        return city;
    }


    private void showOrHideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        } else
            progressBar.setVisibility(View.VISIBLE);
    }
}
