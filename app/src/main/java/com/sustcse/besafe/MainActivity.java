package com.sustcse.besafe;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sustcse.besafe.HowToUse.HowToUse;
import com.sustcse.besafe.UserSettingView.ManageSmsList;
import com.sustcse.besafe.UserSettingView.UserSettingActivity;
import com.sustcse.besafe.alertbanner.AlertDialogForAnything;
import com.sustcse.besafe.appdata.AppConstant;
import com.sustcse.besafe.appdata.AppController;
import com.sustcse.besafe.drawer.Drawer_list_adapter;
import com.sustcse.besafe.model.History;
import com.sustcse.besafe.service.LockScreenService;
import com.sustcse.besafe.sharedprefs.SaveManager;
import com.sustcse.besafe.userInfoView.AboutUs;
import com.sustcse.besafe.userInfoView.HistoryList;
import com.sustcse.besafe.userInfoView.HospitalInfo;
import com.sustcse.besafe.userInfoView.PoliceInfo;
import com.sustcse.besafe.userview.UserLoginActivity;
import com.sustcse.besafe.utils.CheckDeviceConfig;
import com.sustcse.besafe.utils.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.sustcse.besafe.utils.LastLocationOnly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<LocationSettingsResult> {


    private CheckDeviceConfig checkDeviceConfig;


    private SaveManager saveData;

    private String gcmRegId;

    private Button btn_helpMe;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    private ExpandableListView drawer_list;
    private Drawer_list_adapter drawer_adapter_custom;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    private Toolbar toolbar;

    private LastLocationOnly gps;

    public static final String DRAWER_LIST_HISTORY = "History";
    public static final String DRAWER_LIST_MANAGE_SMS = "Manage Sms List";
    public static final String DRAWER_LIST_TUTORIAL = "How To Use";
    public static final String DRAWER_LIST_LOGOUT = "Logout";
    public static final String DRAWER_LIST_SETTING = "Setting";
    public static final String DRAWER_LIST_INFORMATION = "Information";
    public static final String DRAWER_LIST_HOSPITAL = "Hospital";
    public static final String DRAWER_LIST_POLICE = "Police";
    public static final String DRAWER_LIST_HEATMAP = "Heat Map";
    public static final String DRAWER_LIST_ABOUTUS = "About Us";

    private ProgressBar progressBar;
    Gson gson;

    private static boolean FLAG_ACTIVITY_RESUME = true;


    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest locationRequest;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static boolean app_comeFrom_background = false;

    //private  checkDeviceConfig

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FLAG_ACTIVITY_RESUME = false;


        progressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        checkDeviceConfig = new CheckDeviceConfig(getApplicationContext());
        saveData = new SaveManager(this);
        gson = new Gson();

        if (!checkDeviceConfig.isGoogelPlayInstalled()) {
            //Internet Connection is not present
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Google Play Services",
                    "No google play services!!!Please Install google play services", false);

            return;
            //stop executing code by return
        }


        // start service for observing intents
        startService(new Intent(this, LockScreenService.class));


        init();

        if (checkDeviceConfig.isConnectingToInternet()) {
            //showing progressBar
            showOrHideProgressBar();
            sendRequestToServerForHistoryFetch(AppConstant.getUrlForHistoryList(saveData.getUserId(), saveData.getLat(), saveData.getLng(), saveData.getUserNotificationRange(), 1));
        } else {
            //Internet Connection is not present
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);
        locationRequest.setFastestInterval(5 * 1000);


    }

    @Override
    protected void onResume() {
        super.onResume();


        // creating GPS Class object
        gps = new LastLocationOnly(this);


        if (!checkDeviceConfig.isConnectingToInternet()) {
            //Internet Connection is not present
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            //stop executing code by return
            return;
        }
        if (app_comeFrom_background && !gps.canGetLocation()) {
            app_comeFrom_background = false;
            // showGPSDisabledAlertToUser();
            if (mGoogleApiClient.isConnected()) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(locationRequest);
                builder.setAlwaysShow(true);
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(
                                mGoogleApiClient,
                                builder.build()
                        );

                result.setResultCallback(this);
            }


            return;
        }
        if (!checkDeviceConfig.isGoogelPlayInstalled()) {
            //Internet Connection is not present
            AlertDialogForAnything.showAlertDialogWhenComplte(MainActivity.this, "Google Play Services",
                    "No google play services!!!Please Install google play services", false);

            return;
            //stop executing code by return
        }

        if (gps.canGetLocation() && checkDeviceConfig.isConnectingToInternet()) {
            saveData.setLat(String.valueOf(gps.getLatitude()));
            saveData.setLng(String.valueOf(gps.getLongitude()));
        }



    }


    private void init() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btn_helpMe = (Button) findViewById(R.id.btnHelpMe);
        btn_helpMe.setOnClickListener(this);


        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon_inactive);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon_inactive);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav_icon_active);
                if (checkDeviceConfig.isConnectingToInternet()) {
                    //showing progressBar
                    showOrHideProgressBar();
                    sendRequestToServerForHistoryFetch(AppConstant.getUrlForHistoryList(saveData.getUserId(), saveData.getLat(), saveData.getLng(), saveData.getUserNotificationRange(), 1));
                }
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        setDrawer();
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data

        listDataHeader.add(DRAWER_LIST_HISTORY);
        listDataHeader.add(DRAWER_LIST_MANAGE_SMS);
        listDataHeader.add(DRAWER_LIST_INFORMATION);
        listDataHeader.add(DRAWER_LIST_HEATMAP);
        listDataHeader.add(DRAWER_LIST_TUTORIAL);
        listDataHeader.add(DRAWER_LIST_SETTING);
        listDataHeader.add(DRAWER_LIST_ABOUTUS);
        listDataHeader.add(DRAWER_LIST_LOGOUT);

        // Adding child data
        List<String> info = new ArrayList<String>();
        info.add(DRAWER_LIST_HOSPITAL);
        info.add(DRAWER_LIST_POLICE);


        listDataChild.put(listDataHeader.get(2), info); // Header, Child data
//        listDataChild.put(listDataHeader.get(1), others);
    }

    protected void setDrawer() {
        prepareListData();
        drawer_list = (ExpandableListView) findViewById(R.id.left_drawer);
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.drawer_list_header, drawer_list, false);


        TextView header2 = (TextView) header.findViewById(R.id.list_header);
        TextView header3 = (TextView) header.findViewById(R.id.list_header_2);


        header2.setText(saveData.getUserName());
        header3.setText(saveData.getUserEmail());


        drawer_adapter_custom = new Drawer_list_adapter(this, listDataHeader, listDataChild);
        drawer_list.addHeaderView(header, null, false);
        drawer_list.setAdapter(drawer_adapter_custom);

        drawer_list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {


                if (listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).contains(DRAWER_LIST_POLICE)) {

                    Intent intent = new Intent(MainActivity.this, PoliceInfo.class);
                    startActivity(intent);

                }
                if (listDataChild.get(
                        listDataHeader.get(groupPosition)).get(
                        childPosition).contains(DRAWER_LIST_HOSPITAL)) {

                    Intent intent = new Intent(MainActivity.this, HospitalInfo.class);
                    startActivity(intent);

                }


                return false;
            }
        });
        drawer_list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {

                if (listDataHeader.get(i).contains(DRAWER_LIST_MANAGE_SMS)) {
                    Intent intent = new Intent(MainActivity.this, ManageSmsList.class);
                    startActivity(intent);
                }
                if (listDataHeader.get(i).contains(DRAWER_LIST_LOGOUT)) {
                    saveData.setIsLoggedIn(false);
                    Intent home = new Intent(MainActivity.this, UserLoginActivity.class);
                    startActivity(home);
                    finish();
                }
                if (listDataHeader.get(i).contains(DRAWER_LIST_HISTORY)) {
                    Intent intent = new Intent(MainActivity.this, HistoryList.class);
                    startActivity(intent);
                }
                if (listDataHeader.get(i).contains(DRAWER_LIST_SETTING)) {
                    Intent intent = new Intent(MainActivity.this, UserSettingActivity.class);
                    startActivity(intent);
                }
                if (listDataHeader.get(i).contains(DRAWER_LIST_HEATMAP)) {
                    Intent intent = new Intent(MainActivity.this, HeatMapActivity.class);
                    startActivity(intent);
                }
                if (listDataHeader.get(i).contains(DRAWER_LIST_TUTORIAL)) {
                    Intent intent = new Intent(MainActivity.this, HowToUse.class);
                    startActivity(intent);
                }if (listDataHeader.get(i).contains(DRAWER_LIST_ABOUTUS)) {
                    Intent intent = new Intent(MainActivity.this, AboutUs.class);
                    startActivity(intent);
                }


                //Toast.makeText(getApplicationContext(), "" + listDataHeader.get(i), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    @Override
    public void onClick(View v) {


        int id = v.getId();

        if (FLAG_ACTIVITY_RESUME) {
            if (id == R.id.btnHelpMe) {

                FLAG_ACTIVITY_RESUME = false;

                if (checkDeviceConfig.isGoogelPlayInstalled() && checkDeviceConfig.isConnectingToInternet()) {


                    // Read saved registration id from shared preferences.
                    gcmRegId = saveData.getUserGcmRegId();

                    gps = new LastLocationOnly(this);

                    String lat;
                    String lng;
                    if (gps.canGetLocation()) {
                        lat = String.valueOf(gps.getLatitude());

                        lng = String.valueOf(gps.getLongitude());
                    } else {
                        lat = String.valueOf(saveData.getLat());

                        lng = String.valueOf(saveData.getLng());
                    }



                    this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
                    this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));


                    sendRequestToServer(AppConstant.getUrlForHelpSend(gcmRegId, lat, lng, saveData.getUserNotificationRange(), saveData.getNotificationMsg(), saveData.getDeviceId()));
                    sendSMStoFriendList();

                } else {

                    sendSMStoFriendList();


                }
            }
        }


    }


    public void sendRequestToServer(String sentUrl) {
        Log.d("DEBUG_url", sentUrl);
        progressBar.setVisibility(View.VISIBLE);
        btn_helpMe.setEnabled(false);

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        FLAG_ACTIVITY_RESUME = true;

                        progressBar.setVisibility(View.INVISIBLE);

                        btn_helpMe.setBackgroundResource(R.drawable.rounded_button_green);
                        btn_helpMe.setTextColor(getResources().getColor(R.color.white));
                        btn_helpMe.setText("SENT");

                        // change to original after 5 secs.
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                btn_helpMe.setEnabled(true);
                                btn_helpMe.setBackgroundResource(R.drawable.rounded_button);
                                btn_helpMe.setText("HELP");
                            }
                        }, 1000);

                        Toast.makeText(getApplicationContext(), "SEND",
                                Toast.LENGTH_LONG).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        AppController.getInstance().addToRequestQueue(req);

    }

    public void sendRequestToServerForHistoryFetch(String sentUrl) {

        Log.d("DEBUG_history_url", sentUrl);

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //Log.d("DEBUG",response);

                        try {
                            JSONArray jsonArray = new JSONArray(response);


                            AppConstant.histories.clear();
                            AppConstant.NUM_OF_UNSEEN_HISTORY = 0;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempObject = jsonArray.getJSONObject(i);

                                History history = gson.fromJson(tempObject.toString(), History.class);
                                if (history.getSeen().equalsIgnoreCase("false"))
                                    AppConstant.NUM_OF_UNSEEN_HISTORY++;

                                AppConstant.histories.add(history);

                            }

                            //Collections.reverse(AppConstant.histories);

                            drawer_adapter_custom.notifyDataSetChanged();

                            showOrHideProgressBar();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        FLAG_ACTIVITY_RESUME = true;

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showOrHideProgressBar();
            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }

    public void sendSMStoFriendList() {


        gps = new LastLocationOnly(this);

        String location = "undefined";

        if(gps.canGetLocation())
        {
                location = getLocationDetails(gps.getLatitude(),gps.getLongitude());
        }

        Log.d("DEBUG_loc",location);


        ArrayList<String> phoneList = saveData.getPhoneNumberArray();

        for (String phoneNo : phoneList) {
            //String phoneNo = textPhoneNo.getText().toString();
            //Log.d("DEBUG_phone", phoneNo);
            // Log.d("DEBUG_phone2", "no");
            String sms = saveData.getNotificationMsg() + "\n" + saveData.getUserName() + "\n\nLocation:\n" + location;
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNo, null, sms, null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent!",
                        Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS faild, please try again later!",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


        FLAG_ACTIVITY_RESUME = true;
    }


    public void sMs(View view) {

        Intent home = new Intent(MainActivity.this, ManageSmsList.class);
        startActivity(home);
        //finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        app_comeFrom_background = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(mScreenStateReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void showOrHideProgressBar() {
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.INVISIBLE);
        } else
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConnected(Bundle bundle) {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        builder.build()
                );

        result.setResultCallback(this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(LocationSettingsResult locationSettingsResult) {

        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //failed to show
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {

            if (resultCode == RESULT_OK) {
                gps = new LastLocationOnly(this);

                if (gps.canGetLocation() && checkDeviceConfig.isConnectingToInternet()) {
                    saveData.setLat(String.valueOf(gps.getLatitude()));
                    saveData.setLng(String.valueOf(gps.getLongitude()));
                }


                Toast.makeText(getApplicationContext(), "GPS enabled", Toast.LENGTH_LONG).show();
            } else {

                Toast.makeText(getApplicationContext(), "GPS is not enabled", Toast.LENGTH_LONG).show();
            }

        }
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
}

