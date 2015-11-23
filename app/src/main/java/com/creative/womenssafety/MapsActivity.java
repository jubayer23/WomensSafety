package com.creative.womenssafety;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ikhtiar on 11/9/2015.
 */
public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private double lattitude, langitude;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onNewIntent(getIntent());
        init();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey("lattitude") && extras.containsKey("langitude")) {
            lattitude = extras.getDouble("lattitude");
            langitude = extras.getDouble("langitude");
            setUpMapIfNeeded();
        } else {
            lattitude = 0;
            langitude = 0;
        }
    }

    private void init()
    {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        GPSTracker gps = new GPSTracker(this);
        LatLng position = new LatLng(lattitude, langitude);


        setUpVictimInfo();

        sendRequestToServer(AppConstant.DirectionApiUrl(gps.getLatitude(), gps.getLongitude(), lattitude, langitude));
        mMap.addMarker(new MarkerOptions().position(position).title("VICTIM"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(gps.getLatitude(), gps.getLongitude())).title("ME"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));


    }
    private void setUpVictimInfo() {
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker arg0) {
                View v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                TextView tv = (TextView) v.findViewById(R.id.victimInfo);
                tv.setText(getLocationDetails(lattitude, langitude));
                return v;
            }

            public View getInfoContents(Marker arg0) {

                return null;

            }
        });
    }
    private String getLocationDetails(double lat,double lang) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String add="        VICTIM        ";

        List<Address> addresses  = null;
        try {
            addresses = geocoder.getFromLocation(lat, lang, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(addresses!=null) {
            String address="";
            for(int i=0;i<addresses.get(0).getMaxAddressLineIndex();i++){
                address += addresses.get(0).getAddressLine(i);
                if(addresses.get(0).getAddressLine(i+1)!=null && !addresses.get(0).getAddressLine(i+1).isEmpty())
                    address +="\n";
            }

            String knownName = addresses.get(0).getFeatureName();
            String premises = addresses.get(0).getPremises();
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();
            Bundle extras = addresses.get(0).getExtras();
            String subadminarea = addresses.get(0).getSubAdminArea();
            String SubLocality  = addresses.get(0).getSubLocality();
            String SubThoroughfare = addresses.get(0).getSubThoroughfare();
            String Thoroughfare = addresses.get(0).getThoroughfare();


            if(address!=null && !address.isEmpty())add+="\n\nAddressLine : "+address;
            if(knownName!=null && !knownName.isEmpty())add+="\nFeatureName : "+knownName;
            if(premises!=null && !premises.isEmpty())add+="\nPremises : "+premises;
            if(zip!=null && !zip.isEmpty())add+="\nZip : "+zip;
            if(state!=null && !state.isEmpty())add+="\nState : "+state;
            if(city!=null && !city.isEmpty())add+="\nCity : "+city;
            if(country!=null && !country.isEmpty())add+="\nCountry : "+country;
            if(extras!=null && !extras.isEmpty())add+="\nExtras : "+extras;
            if(subadminarea!=null && !subadminarea.isEmpty())add+="\nSubAdminArea : "+subadminarea;
            if(SubLocality !=null && !SubLocality.isEmpty())add+="\nSubLocality : "+SubLocality;
            if(SubThoroughfare !=null && !SubThoroughfare.isEmpty())add+="\nSubThoroughfare : "+SubThoroughfare;
            if(Thoroughfare !=null && !Thoroughfare.isEmpty())add+="\nThoroughfare : "+Thoroughfare;
        }
        return add;
    }



    public void sendRequestToServer(String url) {
        progressDialog = new ProgressDialog(MapsActivity.this);
        progressDialog.setMessage("Fetching route, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        if(response!=null){
                            drawPath(response);
                        }


                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();


            }
        });
        // req.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS,
        //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(req);
    }

    public void drawPath(String  result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = mMap.addPolyline(new PolylineOptions()
                            .addAll(list)
                            .width(12)
                            .color(Color.parseColor("#05b1fb"))//Google maps blue color
                            .geodesic(true)
            );
        }
        catch (JSONException e) {

        }
    }
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }
}