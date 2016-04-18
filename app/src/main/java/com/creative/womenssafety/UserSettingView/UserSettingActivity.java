package com.creative.womenssafety.UserSettingView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.creative.womenssafety.MainActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.appdata.AppConstant;
import com.creative.womenssafety.sharedprefs.SaveManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by comsol on 13-Jan-16.
 */
public class UserSettingActivity extends AppCompatActivity implements View.OnClickListener {

    Spinner spinner_range;
    TextView tv_gps_url;

    private SaveManager saveManager;

    private List<Integer> list_range;

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
        spinner_range.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Integer item = (Integer) parent.getItemAtPosition(position);


                saveManager.setUserNotificationRange(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        list_range = new ArrayList<Integer>();


        spinner_range = (Spinner) findViewById(R.id.setting_spinner_range);

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

        list_range.add(saveManager.getUserNotificationRange());

        for (int i = 0; i < AppConstant.notification_range.length; i++) {

            if (list_range.contains(AppConstant.notification_range[i])) continue;

            list_range.add(AppConstant.notification_range[i]);

        }
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>
                (this, R.layout.spinner_item, list_range);

        spinner_range.setAdapter(dataAdapter);

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

                Toast.makeText(UserSettingActivity.this, "Saved Successfull", Toast.LENGTH_LONG).show();


                finish();
            }


        }
    }
}
