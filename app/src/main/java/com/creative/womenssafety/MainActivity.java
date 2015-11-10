package com.creative.womenssafety;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.receiver.ScreenOnOffReceiver;
import com.creative.womenssafety.sharedprefs.SaveManager;
import com.creative.womenssafety.userview.LoginOrSingupActivity;
import com.creative.womenssafety.userview.UserRegistrationActivity;
import com.creative.womenssafety.utils.CheckDeviceConfig;
import com.creative.womenssafety.utils.GPSTracker;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private CheckDeviceConfig checkDeviceConfig;


    ScreenOnOffReceiver mScreenStateReceiver;

    private SaveManager saveManager;

    private String gcmRegId;

    private Button btn_helpMe;

    GPSTracker gps;

    //private  checkDeviceConfig

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkDeviceConfig = new CheckDeviceConfig(this);

        saveManager = new SaveManager(this);

        gps = new GPSTracker(this);

        registerReceiverForHomeButtonAction();


        init();


    }


    private void registerReceiverForHomeButtonAction() {

        mScreenStateReceiver = new ScreenOnOffReceiver();

        registerReceiver(mScreenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(mScreenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }


    private void init() {

        btn_helpMe = (Button) findViewById(R.id.btnHelpMe);
        btn_helpMe.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {


        int id = v.getId();

        if (id == R.id.btnHelpMe) {
            if (checkDeviceConfig.isGoogelPlayInstalled()) {

                // Read saved registration id from shared preferences.
                gcmRegId = saveManager.getUserGcmRegId();

                String lat = String.valueOf(gps.getLatitude());

                String lng = String.valueOf(gps.getLongitude());

                sendRequestToServer(AppConstant.getUrlForHelpSend(gcmRegId, lat, lng));


            }
        }
    }


    public void sendRequestToServer(String sentUrl) {

        StringRequest req = new StringRequest(Request.Method.GET, sentUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(getApplicationContext(), response,
                                Toast.LENGTH_LONG).show();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });

        AppController.getInstance().addToRequestQueue(req);

    }

    public void LogOut(View view) {
        saveManager.setUserGcmRegId("0");
        saveManager.setIsLoggedIn(false);
        Intent home = new Intent(MainActivity.this, LoginOrSingupActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
    }
}

