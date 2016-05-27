package com.sustcse.besafe.service;

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

import com.sustcse.besafe.MapActivity;
import com.sustcse.besafe.R;
import com.sustcse.besafe.receiver.GCMBroadcastReceiver;
import com.sustcse.besafe.sharedprefs.SaveManager;
import com.sustcse.besafe.utils.GPSTracker;
import com.sustcse.besafe.utils.LastLocationOnly;

import java.util.Random;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1000;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;
    private LastLocationOnly gps;
    private SaveManager saveManager;

    public GCMIntentService() {
        super(GCMIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        gps = new LastLocationOnly(this);
        saveManager = new SaveManager(this);

        if (!extras.isEmpty()) {

            //Log.d("DEBUG","its here 1");

            // read extras as sent from server
            String message = extras.getString("message");
            String lat, lng, event_id, range;
            try {
                lat = extras.getString("lattitude");
                lat.toString().trim();
                lng = extras.getString("langitude");
                lng.toString().trim();
                event_id = extras.getString("event_id");
                event_id.trim();
                range = extras.getString("range");
                range.trim();

                if (gps.canGetLocation()) {


                    try {
                        float[] results = new float[1];
                        Location.distanceBetween(gps.getLatitude(),
                                gps.getLongitude(), Double.parseDouble(lat),
                                Double.parseDouble(lng), results);
                        int int_result = (int) results[0];
                        int_result = (int_result / 1609);
                        if (int_result <= Integer.parseInt(range)) {
                            sendNotification(message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));

                        }
                    } catch (Exception e) {
                        sendNotification(message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));
                    }


                } else if (saveManager.getLat() != 0.0 && saveManager.getLng() != 0.0) {

                    try {
                        float[] results = new float[1];
                        Location.distanceBetween(saveManager.getLat(),
                                saveManager.getLng(), Double.parseDouble(lat),
                                Double.parseDouble(lng), results);
                        int int_result = (int) results[0];
                        int_result = (int_result / 1609);
                        if (int_result <= Integer.parseInt(range)) {
                            sendNotification(message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));

                        }
                    } catch (Exception e) {
                        sendNotification(message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));
                    }
                } else {

                    //Log.d("DEBUG","its here 2");
                    sendNotification(message, Double.parseDouble(lat), Double.parseDouble(lng), Integer.parseInt(event_id));
                }

            } catch (Exception e) {
                e.printStackTrace();
                // lat = "24.913596";
                // lng = "91.90391";
            }


        }


        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, double lat, double lng, int event_id) {


        // Log.d("DEBUG_noti", "onSendNoti");
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
        // mNotificationManager.notify(NOTIFICATION_ID, builder.build());
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        mNotificationManager.notify(m, builder.build());


    }

}
