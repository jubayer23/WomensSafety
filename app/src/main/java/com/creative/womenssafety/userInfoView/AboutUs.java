package com.creative.womenssafety.userInfoView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.creative.womenssafety.R;
import com.creative.womenssafety.appdata.AppConstant;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by comsol on 04-May-16.
 */
public class AboutUs extends AppCompatActivity {


    TextView tv_dev_1, tv_dev_2, tv_dev_3, tv_dev_4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        init();

        int count = 0;
        ArrayList<Integer> list = new ArrayList<>();
        while (true) {
            Random rn = new Random();
            int ranvalue = rn.nextInt(4);
            if(list.contains(ranvalue))continue;
            list.add(ranvalue);
            switch (count) {
                case 0:
                    tv_dev_1.setText(AppConstant.developer_name[ranvalue]);
                    count++;
                    break;
                case 1:
                    tv_dev_2.setText(AppConstant.developer_name[ranvalue]);
                    count++;
                    break;
                case 2:
                    tv_dev_3.setText(AppConstant.developer_name[ranvalue]);
                    count++;
                    break;
                case 3:
                    tv_dev_4.setText(AppConstant.developer_name[ranvalue]);
                    count++;
                    break;
            }


            if (count > 3) break;
        }
    }

    private void init() {
        tv_dev_1 = (TextView) findViewById(R.id.dev_1);
        tv_dev_2 = (TextView) findViewById(R.id.dev_2);
        tv_dev_3 = (TextView) findViewById(R.id.dev_3);
        tv_dev_4 = (TextView) findViewById(R.id.dev_4);
    }
}
