package com.creative.womenssafety.userview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.creative.womenssafety.MainActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.alertbanner.AlertDialogForAnything;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.appdata.AppController;
import com.creative.womenssafety.sharedprefs.SaveManager;
import com.creative.womenssafety.utils.CheckDeviceConfig;
import com.creative.womenssafety.utils.DeviceInfoUtils;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserLoginActivity extends AppCompatActivity {

    public static Activity userLoginActivity;

    String userName;
    String password;

    Button loginB;
    EditText userNameEd, passwordEd;
    TextView registerUser;
    //SqliteDb theDb;
    ProgressDialog progressDialog;


    CheckDeviceConfig cd;


    SaveManager saveManager;


    private String gcmRegId;

    GoogleCloudMessaging gcm;

    private static final String KEY_SUCCESS = "success";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_user_login);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userLoginActivity = this;

        init();

        if (saveManager.getIsLoggedIn()) {
            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);

            startActivity(intent);

            finish();
        }


        loginB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cd.isConnectingToInternet()) {
                    userName = userNameEd.getText().toString();
                    password = passwordEd.getText().toString();

                    if (showWarningDialog()) {
                        progressDialog.show();

                        if (saveManager.getUserGcmRegId().equals("0")) {
                            new GCMRegistrationTask().execute();
                        } else {
                            LoginCheck(saveManager.getUserGcmRegId());
                        }


                    }

                } else {
                    cd.showAlertDialogToNetworkConnection(UserLoginActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }

            }
        });


        registerUser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent signUpIntent = new Intent(UserLoginActivity.this, UserRegistrationActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

    private void init() {
        // gson = new Gson();
        //theDb = AppController.getsqliteDbInstance();
        cd = new CheckDeviceConfig(this);

        saveManager = new SaveManager(this);

        gcm = GoogleCloudMessaging
                .getInstance(getApplicationContext());


        loginB = (Button) findViewById(R.id.loginInnerB);


        userNameEd = (EditText) findViewById(R.id.username_email_loginED);

        passwordEd = (EditText) findViewById(R.id.pasword_ed);

        registerUser = (TextView) findViewById(R.id.btn_signup);

        progressDialog = new ProgressDialog(UserLoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Login...");

    }

    public boolean showWarningDialog() {

        boolean valid = true;

        if (userNameEd.getText().toString().isEmpty()) {
            userNameEd.setError("Enter Username");
            valid = false;
        } else {
            userNameEd.setError(null);
        }

        if (passwordEd.getText().toString().isEmpty()) {
            passwordEd.setError("Enter Password");
            valid = false;
        } else {
            passwordEd.setError(null);
        }

        if (!(userNameEd.getText().toString().isEmpty() && passwordEd.getText().toString().isEmpty())) {
            if (userNameEd.getText().toString().isEmpty() && !passwordEd.getText().toString().isEmpty()) {
                userNameEd.requestFocus();
            }
            if (!userNameEd.getText().toString().isEmpty() && passwordEd.getText().toString().isEmpty()) {
                passwordEd.requestFocus();
            }
        }


        return valid;
    }

    private class GCMRegistrationTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (gcm == null && cd.isGoogelPlayInstalled()) {
                gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            }
            try {
                gcmRegId = gcm.register(AppConstant.GCM_SENDER_ID);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return gcmRegId;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Toast.makeText(getApplicationContext(), "registered with GCM",
                        Toast.LENGTH_LONG).show();
                // regIdView.setText(result);
                saveManager.setUserGcmRegId(result);
                //Log.d("GCM_REG_ID", result);


                LoginCheck(result);


            }
        }

    }


    private void LoginCheck(String gcmRegId) {
        // TODO Auto-generated method stub


        String url_login = AppConstant.getLoginUrl(userName, password, DeviceInfoUtils.getDeviceID(UserLoginActivity.this), gcmRegId);

        Log.d("DEBUG_loginUrl", url_login);

        this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        sendRequestToServer(url_login);

    }


    public void sendRequestToServer(String url_all_products) {
        String url = url_all_products;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            parseJsonFeed(new JSONObject(response));
                        } catch (JSONException e) {

                            if (progressDialog.isShowing()) progressDialog.dismiss();

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


    private void parseJsonFeed(JSONObject response) {
        try {


            int status = response.getInt(KEY_SUCCESS);
           // Log.d("DEBUG_loginStatus", String.valueOf(status));

            if (status == 1) {
                saveManager.setUserId(response.getString(KEY_USER_ID));
                saveManager.setUserName(response.getString(KEY_USER_NAME));
                saveManager.setUserEmail(response.getString(KEY_USER_EMAIL));

            }


            if (progressDialog.isShowing()) progressDialog.dismiss();


            gotoFrontPage(status);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {

        } catch (Exception e) {

        }
        if (progressDialog.isShowing()) progressDialog.dismiss();

    }


    public void gotoFrontPage(int success) {
        if (success == 1) {

            saveManager.setIsLoggedIn(true);

            Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);

            startActivity(intent);


            finish();
        } else {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Login Failed", "InValid Login Information", false);
        }

    }
}
