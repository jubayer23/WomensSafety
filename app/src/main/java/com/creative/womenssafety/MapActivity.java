package com.creative.womenssafety;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.womenssafety.Map.BaseMapActivity;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
 * Created by comsol on 17-Jan-16.
 */
public class MapActivity extends BaseMapActivity implements GoogleMap.OnInfoWindowClickListener {

    private double lattitude, langitude;
    private int event_id;
    private ProgressDialog progressDialog;
    private static final int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey("lattitude") && extras.containsKey("langitude")) {
            lattitude = extras.getDouble("lattitude");
            langitude = extras.getDouble("langitude");
            event_id = extras.getInt("event_id");
            Log.d("DEBUG_inInternt","Yes");
            // setUpMapIfNeeded();
        } else {
            Log.d("DEBUG_inInternt","No");
            lattitude = 24.913596;
            langitude = 91.90391;
            event_id = 757;
        }
    }

    @Override
    protected void initMapSettings() {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);


        LatLng position = new LatLng(lattitude, langitude);

        if (gps.canGetLocation()) {

            setUpMarker(lattitude, langitude, gps.getLatitude(), gps.getLongitude());

            sendRequestToServer(AppConstant.DirectionApiUrl(gps.getLatitude(), gps.getLongitude(), lattitude, langitude));
        } else {
            setUpMarkerOnlyVictim(lattitude, langitude);

            sendRequestToServerForSetHistoryIsSeen(AppConstant.getUrlForSetHistorySeenUnseen(saveManager.getUserId(), String.valueOf(event_id)));
        }



        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    }

    private void setUpMarker(double desLat, final double desLang, double srcLat, double srcLang) {

        final Marker m1 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(desLat, desLang)));
        m1.setSnippet(getLocationDetails(desLat, desLang));
        m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_victim));
        m1.setTitle("VICTIM");


        Marker m2 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(srcLat, srcLang)));
        m2.setSnippet(getLocationDetails(srcLat, srcLang));
        m2.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_me));
        m2.setTitle("ME");

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

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

        mGoogleMap.setOnInfoWindowClickListener(this);
        //mGoogleMap.setOnMarkerClickListener(this);
    }

    private void setUpMarkerOnlyVictim(double desLat, final double desLang) {

        final Marker m1 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(desLat, desLang)));
        m1.setSnippet(getLocationDetails(desLat, desLang));
        m1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_victim));
        m1.setTitle("VICTIM");


        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            public View getInfoWindow(Marker marker) {
                return null;
            }

            public View getInfoContents(Marker marker) {
                LatLng position = new LatLng(lattitude, langitude);
                View v;
                TextView tv_title, tv_info;


                v = getLayoutInflater().inflate(R.layout.custom_infowindow, null);
                tv_title = (TextView) v.findViewById(R.id.victimtitle);
                tv_info = (TextView) v.findViewById(R.id.victimInfo);
                tv_title.setText(marker.getTitle());
                tv_info.setText(marker.getSnippet());
                return v;

            }
        });

        mGoogleMap.setOnInfoWindowClickListener(this);
        //mGoogleMap.setOnMarkerClickListener(this);
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

            try {
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
            } catch (Exception e) {

            }


        }

        return add;
    }


    public void sendRequestToServer(String url) {
        progressDialog = new ProgressDialog(MapActivity.this);
        progressDialog.setMessage("Fetching route, Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.show();

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (response != null) {
                            drawPath(response);

                            sendRequestToServerForSetHistoryIsSeen(AppConstant.getUrlForSetHistorySeenUnseen(saveManager.getUserId(), String.valueOf(event_id)));
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

    public void sendRequestToServerForSetHistoryIsSeen(String url) {

        if(progressDialog ==  null){
            progressDialog = new ProgressDialog(MapActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

                        if (response != null) {
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
            Polyline line = mGoogleMap.addPolyline(new PolylineOptions()
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
            showSettingDialog(marker);
        }

    }

    private void showSettingDialog(final Marker marker) {

        final Dialog dialog_start = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog_start.setCancelable(true);
        dialog_start.setContentView(R.layout.dialog_infowindow_click);

        Button btn_path = (Button) dialog_start.findViewById(R.id.dialog_show_path);
        Button btn_picker = (Button) dialog_start.findViewById(R.id.dialog_show_picker);


        btn_path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri gmmIntentUri = Uri.parse("google.navigation:q="
                        + String.valueOf(marker.getPosition().latitude) + ","
                        + String.valueOf(marker.getPosition().longitude));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

                dialog_start.dismiss();

            }
        });

        btn_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPicker(lattitude, langitude);
            }
        });




        dialog_start.show();
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
}
