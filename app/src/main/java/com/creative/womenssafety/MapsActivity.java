package com.creative.womenssafety;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.creative.womenssafety.R;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.utils.GPSTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
public class MapsActivity extends FragmentActivity implements GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private double lattitude, langitude;
    private ProgressDialog progressDialog;
    final int MY_LOCATION = 1;
    final int VICTIM_LOCATION = 0;

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

    private void init() {

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

        setUpMarker(lattitude, langitude, gps.getLatitude(), gps.getLongitude());
        sendRequestToServer(AppConstant.DirectionApiUrl(gps.getLatitude(), gps.getLongitude(), lattitude, langitude));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));


    }

    private void setUpMarker(double desLat, final double desLang, double srcLat, double srcLang) {

        final Marker m1 = mMap.addMarker(new MarkerOptions().position(new LatLng(desLat, desLang)));
        m1.setSnippet(getLocationDetails(desLat, desLang));
        m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_victim));
        m1.setTitle("VICTIM");


        Marker m2 = mMap.addMarker(new MarkerOptions().position(new LatLng(srcLat, srcLang)));
        m2.setSnippet(getLocationDetails(srcLat, srcLang));
        m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me));
        m2.setTitle("ME");

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker marker) {
                return null;
            }

            public View getInfoContents(Marker marker) {
                LatLng position = new LatLng(lattitude, langitude);
                View v;
                TextView tv_title, tv_info;

                if (marker.equals(m1)) {
                    v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                    tv_title = (TextView) v.findViewById(R.id.victimtitle);
                    tv_info = (TextView) v.findViewById(R.id.victimInfo);
                    tv_title.setText(marker.getTitle());
                    tv_info.setText(marker.getSnippet());
                    return v;
                } else {
                    v = getLayoutInflater().inflate(R.layout.custom_infowindow_me, null);
                    tv_title = (TextView) v.findViewById(R.id.metitle);
                    tv_info = (TextView) v.findViewById(R.id.meInfo);
                    tv_title.setText(marker.getTitle());
                    tv_info.setText(marker.getSnippet());
                    return v;
                }
            }
        });

        mMap.setOnInfoWindowClickListener(this);
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
    public void onInfoWindowClick(final Marker marker) {


        // TODO Auto-generated method stub
        String title = marker.getTitle();

        if (!title.equalsIgnoreCase("ME")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MapsActivity.this);
            alert.setTitle(title);

            alert.setMessage("Do you Want see Direction");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    Uri gmmIntentUri = Uri.parse("google.navigation:q="
                            + String.valueOf(marker.getPosition().latitude) + ","
                            + String.valueOf(marker.getPosition().longitude));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);


                }
            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });

            alert.show();

        }


    }
}