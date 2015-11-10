package com.creative.womenssafety.userview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.creative.womenssafety.MainActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.sharedprefs.SaveManager;

/**
 * Created by comsol on 11/9/2015.
 */
public class LoginOrSingupActivity extends AppCompatActivity {

    public static Activity loginOrSignUpActivity;

    Button btn_login, btn_signUp;


    private SaveManager saveManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginOrSignUpActivity = this;

        saveManager = new SaveManager(this);

        if (saveManager.getIsLoggedIn()) {
            Intent intent = new Intent(LoginOrSingupActivity.this, MainActivity.class);

            startActivity(intent);

            finish();
        }


        setContentView(R.layout.activity_loginorsignup);

        init();

        btn_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent loginIntent = new Intent(LoginOrSingupActivity.this, UserLoginActivity.class);
                startActivity(loginIntent);

            }
        });
        btn_signUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent signUpIntent = new Intent(LoginOrSingupActivity.this, UserRegistrationActivity.class);
                startActivity(signUpIntent);

            }
        });


    }

    private void init() {

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signUp = (Button) findViewById(R.id.btn_signup);
    }
}
