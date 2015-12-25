package com.creative.womenssafety;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.creative.womenssafety.R;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.utils.GPSTracker;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private double lattitude, langitude;
    private ProgressDialog progressDialog;
    final int MY_LOCATION = 1;
    final int VICTIM_LOCATION = 0;
    private static final int PLACE_PICKER_REQUEST = 1;
    private Marker m1, m2;
    private GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey("lattitude") && extras.containsKey("langitude")) {
            lattitude = extras.getDouble("lattitude");
            langitude = extras.getDouble("langitude");
            try {
                setUpMapIfNeeded();
            } catch (Exception e) {
            }
        } else {
            lattitude = 0;
            langitude = 0;
        }
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
        gps = new GPSTracker(this);
        LatLng position = new LatLng(lattitude, langitude);

        setUpMarker(lattitude, langitude, gps.getLatitude(), gps.getLongitude());
        mMap.setOnMarkerClickListener(this);
        sendRequestToServer(AppConstant.DirectionApiUrl(gps.getLatitude(), gps.getLongitude(), lattitude, langitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));


    }

    private void setUpMarker(double desLat, final double desLang, double srcLat, double srcLang) {
        m1 = mMap.addMarker(new MarkerOptions().position(new LatLng(desLat, desLang)));
        m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_victim));
        m1.setTitle("VICTIM");


        m2 = mMap.addMarker(new MarkerOptions().position(new LatLng(srcLat, srcLang)));
        m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me));
        m2.setTitle("ME");
    }

    private int getDistance(double desLat, double desLang, double srcLat, double srcLang) {
        Location l1 = new Location("src");
        l1.setLatitude(srcLat);
        l1.setLongitude(srcLang);

        Location l2 = new Location("des");
        l2.setLatitude(desLat);
        l2.setLongitude(desLang);

        return Math.round(l1.distanceTo(l2));
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
        if (addresses != null) {
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String SubLocality = addresses.get(0).getSubLocality();
            String Thoroughfare = addresses.get(0).getThoroughfare();
            String country = addresses.get(0).getCountryName();

            if (Thoroughfare != null && !Thoroughfare.isEmpty())
                add += Thoroughfare + ",";
            if (SubLocality != null && !SubLocality.isEmpty())
                add += "\n(Around " + SubLocality + "),";
            if (state != null && !state.isEmpty()) add += "\n" + state + ",";
            if (city != null && !city.isEmpty()) add += "\n" + city;
            else if (country != null && !country.isEmpty()) add += "\n" + country;
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

                        if (response != null) {
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

    public void drawPath(String result) {

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
        } catch (JSONException e) {

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

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(m1)) {
            openPicker(lattitude, langitude);
        } else if (marker.equals(m2)) {
            openPicker(gps.getLatitude(), gps.getLongitude());
        }
        return false;
    }

    private void openPicker(double lattitude, double langitude) {
        PlacePicker.IntentBuilder intentBuilder;
        Intent intent;

        try {
            intentBuilder = new PlacePicker.IntentBuilder();
            intentBuilder.setLatLngBounds(new LatLngBounds(new LatLng(lattitude - .002, langitude - .002),
                    new LatLng(lattitude + .002, langitude + .002)));

            intent = intentBuilder.build(getApplicationContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == PLACE_PICKER_REQUEST
                && resultCode == Activity.RESULT_OK) {
            final Place place = PlacePicker.getPlace(data, this);
            openPicker(place.getLatLng().latitude, place.getLatLng().longitude);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            setUpMapIfNeeded();
        } catch (Exception e) {
        }
    }

}