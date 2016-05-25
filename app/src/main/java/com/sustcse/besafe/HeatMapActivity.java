package com.sustcse.besafe;

import android.app.ProgressDialog;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sustcse.besafe.Map.BaseMapActivity;
import com.sustcse.besafe.appdata.AppConstant;
import com.sustcse.besafe.appdata.AppController;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Paul on 9/7/15.
 */
public class HeatMapActivity extends BaseMapActivity {

    private HeatmapTileProvider mProvider;

    int freq;

    private ProgressDialog progressDialog;

    private LatLng cameraLocation;

    private ArrayList<LatLng> locations;

    @Override
    protected void initMapSettings() {
        sendRequestToServer(AppConstant.getUrlForHeatMap());
       // if(locations == null) return;

    }

    protected void initCamera() {
        if(cameraLocation == null) return;

        CameraPosition position = CameraPosition.builder()
                .target( cameraLocation )
                .zoom( getInitialMapZoomLevel() )
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), null);
    }



    public void sendRequestToServer(String sentUrl) {

        if(progressDialog ==  null){
            progressDialog = new ProgressDialog(HeatMapActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                       // FLAG_ACTIVITY_RESUME = true;


                        try {

                            //Log.d("HEATMAP","YES");
                            parseJsonFeed(new JSONArray(response));

                            mProvider = new HeatmapTileProvider.Builder().data( locations ).build();
                            mProvider.setRadius( HeatmapTileProvider.DEFAULT_RADIUS );
                            mGoogleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));



                            if (progressDialog.isShowing()) progressDialog.dismiss();

                            initCamera();

                        } catch (JSONException e) {

                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        }


                        //Toast.makeText(getApplicationContext(), "SEND",
                         //       Toast.LENGTH_LONG).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();

            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }


    private void parseJsonFeed(JSONArray response) {

       locations = new ArrayList<LatLng>();


        try {



           freq = 0;
            for (int i = 0; i < response.length(); i++) {

               // Log.d("HEATMAP","YES");

                JSONObject tempObject = response.getJSONObject(i);

                Double lat = Double.parseDouble(tempObject.getString("lat"));
                Double lng = Double.parseDouble(tempObject.getString("lng"));
                int freq_j = Integer.parseInt(tempObject.getString("freq"));

                if(freq_j > freq)
                {
                    freq = freq_j;
                    cameraLocation = new LatLng(lat, lng);
                }

                locations.add(new LatLng(lat, lng));

                //FoodItem foodItem = gson.fromJson(tempObject.toString(), FoodItem.class);

                //foodItemList.add(foodItem);

            }






        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {

        } catch (Exception e) {

        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
