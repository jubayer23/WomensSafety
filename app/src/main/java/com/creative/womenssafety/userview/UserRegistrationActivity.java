package com.creative.womenssafety.userview;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.regex.Pattern;

public class UserRegistrationActivity extends AppCompatActivity {

    EditText usernamaEd, emailEd, passEd;

    Button signUp;

    String userName, email, password;


    ProgressDialog progressDialog;

    CheckDeviceConfig cd;


    SaveManager saveManager;

    private String gcmRegId;

    GoogleCloudMessaging gcm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cd.isConnectingToInternet()) {

                    userName = usernamaEd.getText().toString().trim();
                    email = emailEd.getText().toString().trim();
                    password = passEd.getText().toString().trim();


                    boolean checkWarn = showWarningDialog();
                    if (checkWarn) {
                        progressDialog.show();
                        if (saveManager.getUserGcmRegId().equals("0")) {
                            new GCMRegistrationTask().execute();
                        } else {
                            singnUP(saveManager.getUserGcmRegId());
                        }
                    }
                } else {
                    cd.showAlertDialogToNetworkConnection(UserRegistrationActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }
            }
        });


    }


    private void init() {

        cd = new CheckDeviceConfig(this);

        saveManager = new SaveManager(this);

        gcm = GoogleCloudMessaging
                .getInstance(getApplicationContext());


        signUp = (Button) findViewById(R.id.signSignupB);


        usernamaEd = (EditText) findViewById(R.id.userNameSignUpEd);

        emailEd = (EditText) findViewById(R.id.emailSignEd);


        passEd = (EditText) findViewById(R.id.passSignEd);


        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Sign Up...");


    }


    public boolean showWarningDialog() {


        if (usernamaEd.getText().toString().trim().isEmpty()) {
            AlertDialogForAnything.showAlertDialogWhenComplte(UserRegistrationActivity.this, "EMPTY FIELD", "UserName Field is Empty", false);

        } else if (emailEd.getText().toString().trim().isEmpty()) {
            AlertDialogForAnything.showAlertDialogWhenComplte(UserRegistrationActivity.this, "EMPTY FIELD", "Email Field is Empty", false);

        } else if (passEd.getText().toString().trim().isEmpty()) {

            AlertDialogForAnything.showAlertDialogWhenComplte(UserRegistrationActivity.this, "EMPTY FIELD", "Password Field is Empty", false);
        }  else if (!validEmail(emailEd.getText().toString().trim())) {
            AlertDialogForAnything.showAlertDialogWhenComplte(UserRegistrationActivity.this, "INVALID", "Email InValid", false);

        } else {
            return true;
        }
        return false;
    }

    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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


                singnUP(result);


            }
        }

    }

    private void singnUP(String gcmRegId) {
        // TODO Auto-generated method stub


        String url_reg = AppConstant.getUserRegUrl(gcmRegId, userName, email, password, DeviceInfoUtils.getDeviceID(UserRegistrationActivity.this));

        Log.d("DEBUG_regUrl", url_reg);
        this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        sendRequestToServer(url_reg);

    }


    public void sendRequestToServer(String url_all_products) {
        String url = url_all_products;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        Log.d("DEBUG_onresponse", response);
                        try {
                            parseJsonFeed(new JSONObject(response));
                        } catch (JSONException e) {

                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        }


                        // }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
                // Log.d("DEBUG_onError","error");

            }
        });
        // req.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_SOCKET_TIMEOUT_MS,
        //        DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(req);
    }


    private void parseJsonFeed(JSONObject response) {
        try {


            int status = response.getInt("success");

            if(status == 1)saveManager.setUserId(response.getString("user_id"));
            //Log.d("DEBUG_regStatus", String.valueOf(status));


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

            saveManager.setUserEmail(email);
            saveManager.setUserName(userName);

            Intent intent = new Intent(UserRegistrationActivity.this, MainActivity.class);

            startActivity(intent);

            UserLoginActivity.userLoginActivity.finish();

            finish();
        } else {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Register Failed", "You Are Device Already Registered. Please Login with exiting Id", false);
        }

    }


}
