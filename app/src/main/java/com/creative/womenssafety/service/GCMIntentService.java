package com.creative.womenssafety.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.creative.womenssafety.MapActivity;
import com.creative.womenssafety.R;
import com.creative.womenssafety.receiver.GCMBroadcastReceiver;
import com.creative.womenssafety.utils.GPSTracker;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    GPSTracker gps;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        gps = new GPSTracker(this);

        if (!extras.isEmpty()) {

            // read extras as sent from server
            String message = extras.getString("message");
            String serverTime = extras.getString("timestamp");
            String lat = "24.913596";
            String lng = "91.90391";
            String event_id = "1";
            String range = "5000";
            try {
                lat = extras.getString("lattitude");
                lat.toString().trim();
                lng = extras.getString("langitude");
                lng.toString().trim();
                event_id = extras.getString("event_id");
                event_id.trim();
                range = extras.getString("range");
                range.trim();

            } catch (Exception e) {
                e.printStackTrace();
                lat = "24.913596";
                lng = "91.90391";
            }


            if (gps.canGetLocation()) {


                try {
                    float[] results = new float[1];
                    Location.distanceBetween(gps.getLatitude(),
                            gps.getLongitude(), Double.parseDouble(lat),
                            Double.parseDouble(lng), results);
                    int int_result = (int) results[0];
                    if ( int_result <= Integer.parseInt(range)) {
                        sendNotification("Message: " + message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));

                    }
                } catch (Exception e) {
                    sendNotification("Message: " + message , Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));
                }


            } else {
                sendNotification("Message: " + message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));
            }


        }


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, double lat, double lng, int event_id) {


        Log.d("DEBUG_noti", "onSendNoti");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Intent notifyIntent = new Intent(this, MapActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notifyIntent.putExtra("lattitude", lat);
        notifyIntent.putExtra("langitude", lng);
        notifyIntent.putExtra("event_id", event_id);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        builder.setSmallIcon(R.mipmap.ic_launcher);

        builder.setContentTitle("HELP ME!!!");
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        builder.setContentText(msg);
        builder.setContentIntent(notifyPendingIntent);
        builder.setAutoCancel(true);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        //Vibration And Sound
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Uri notification = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(this, notification);
        r.play();


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_ID, builder.build());


    }

}
