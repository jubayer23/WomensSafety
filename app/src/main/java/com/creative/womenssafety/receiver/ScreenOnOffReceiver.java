package com.creative.womenssafety.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.creative.womenssafety.service.MyService;
import com.creative.womenssafety.sharedprefs.SaveManager;

/**
 * Created by comsol on 11/7/2015.
 */
public class ScreenOnOffReceiver extends BroadcastReceiver {


    private SaveManager saveManager;
    private long mLastClickTime = 0;
    @Override
    public void onReceive(Context context, Intent intent) {

        saveManager = new SaveManager(context);

        int homeBtnClickCounter = saveManager.getHomebuttonClickCounter();

        if (SystemClock.elapsedRealtime() - saveManager.getHomeButtonLastclickTime() < 1000) {

            homeBtnClickCounter++;

            saveManager.setHomebuttonClickCounter(homeBtnClickCounter);

            //return;
        }else
        {
            homeBtnClickCounter = 0;
            saveManager.setHomebuttonClickCounter(homeBtnClickCounter);
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        saveManager.setHomebuttonLastclickTime(mLastClickTime);

        Log.i("[BUTTONCLICK]", String.valueOf(homeBtnClickCounter));

        //if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
        //    Log.i("[BroadcastReceiver]", "Screen ON");
       // } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
       //     Log.i("[BroadcastReceiver]", "Screen OFF");
        //}

        if(homeBtnClickCounter >= 2 )
        {
            Log.i("DEBUG", String.valueOf(homeBtnClickCounter));
            //startTheservice
            Intent intent2 = new Intent(context,
                    MyService.class);
            context.startService(intent2);
        }

    }
}
