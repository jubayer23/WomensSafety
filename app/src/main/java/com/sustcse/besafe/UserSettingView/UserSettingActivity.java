package com.sustcse.besafe.UserSettingView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.sustcse.besafe.R;
import com.sustcse.besafe.sharedprefs.SaveManager;

/**
 * Created by comsol on 13-Jan-16.
 */
public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {


    private SaveManager saveManager;


    private Button btn_save;

    private EditText et_notifition_msg;

    private SeekBar rangeBar;

    private TextView seekbar_text;


    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_usersetting);

        init();


        // getActionBar().setDisplayHomeAsUpEnabled(true);
        rangeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekbar_text.setText(progress + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                saveManager.setUserNotificationRange(seekBar.getProgress());
            }
        });


    }

    private void init() {

        saveManager = new SaveManager(this);




        rangeBar = (SeekBar) findViewById(R.id.set_range);
        seekbar_text = (TextView) findViewById(R.id.seekbar_text);



        btn_save = (Button) findViewById(R.id.setting_save);

        et_notifition_msg = (EditText) findViewById(R.id.setting_notification_message);
        et_notifition_msg.setText(saveManager.getNotificationMsg().replaceAll("%20", " "));

        btn_save.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        seekbar_text.setText(saveManager.getUserNotificationRange()+" miles");
        rangeBar.setProgress(saveManager.getUserNotificationRange());

        /***********************************************/

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.setting_save) {


            if (et_notifition_msg.getText().toString().isEmpty()) {
                Toast.makeText(UserSettingActivity.this, "Message Cant Be Empty!!", Toast.LENGTH_LONG).show();


            } else {
                saveManager.setNotificationMsg(et_notifition_msg.getText().toString().replaceAll(" ", "%20"));

                Toast.makeText(UserSettingActivity.this, "Save Successfully", Toast.LENGTH_LONG).show();


                finish();
            }


        }
    }
}
