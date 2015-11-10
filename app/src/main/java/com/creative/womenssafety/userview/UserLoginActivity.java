package com.creative.womenssafety.userview;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class UserLoginActivity extends AppCompatActivity {
    String userName;
    String password;
    String loginType;

    Button loginB;
    EditText userNameEd, passwordEd;
    TextView forgetPassTxt;
    //SqliteDb theDb;
    ProgressDialog progressDialog;

    CheckBox staySignCheckBox;


    CheckDeviceConfig cd;


    SaveManager saveManager;


    private String gcmRegId;

    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        init();


        loginB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (cd.isConnectingToInternet()) {
                    userName = userNameEd.getText().toString();
                    password = passwordEd.getText().toString();

                    if (showWarningDialog()) {
                        progressDialog.show();
                        new GCMRegistrationTask().execute();
                    }

                } else {
                    cd.showAlertDialogToNetworkConnection(UserLoginActivity.this, "No Internet Connection",
                            "You don't have internet connection.", false);
                }

            }
        });

        staySignCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (((CheckBox) v).isChecked()) {

                } else {

                }
            }
        });

        forgetPassTxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Intent intent = new Intent(UserLoginActivity.this, ForgotPassActivity.class);
                // startActivity(intent);
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

        forgetPassTxt = (TextView) findViewById(R.id.forgotPassTxt);


        staySignCheckBox = (CheckBox) findViewById(R.id.stayLogIncheckBox);

        progressDialog = new ProgressDialog(UserLoginActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Login...");

    }

    public boolean showWarningDialog() {

        if (userNameEd.getText().toString().isEmpty()) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Empty Field", "UserName Field Empty", false);

        } else if (passwordEd.getText().toString().isEmpty()) {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Empty Field", "PassWord Field Empty", false);
        } else {
            return true;
        }
        return false;
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


        String url_login = AppConstant.getLoginUrl(userName, password, "dummy_deviceId", gcmRegId);

        Log.d("DEBUG_loginUrl", url_login);

        sendRequestToServer(url_login);

    }


    public void sendRequestToServer(String url_all_products) {
        String url = url_all_products;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {





                        saveManager.setIsLoggedIn(true);

                        Intent intent = new Intent(UserLoginActivity.this, MainActivity.class);

                        startActivity(intent);

                        LoginOrSingupActivity.loginOrSignUpActivity.finish();


                        if (progressDialog.isShowing()) progressDialog.dismiss();

                        finish();
                       // try {

                       //     if (response != null)
                        //        parseJsonFeed(new JSONObject(response));
                        //} catch (JSONException e) {




                       // }
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


            int status = response.getInt("success");
            Log.d("DEBUG_loginStatus", String.valueOf(status));


            if (progressDialog.isShowing()) progressDialog.dismiss();


            gotoFrontPage(1);


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

            LoginOrSingupActivity.loginOrSignUpActivity.finish();
        } else {
            AlertDialogForAnything.showAlertDialogWhenComplte(this, "Login Failed", "InValid Login Information", false);
        }
        finish();
    }
}
