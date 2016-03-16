package com.creative.womenssafety.appdata;

import com.creative.womenssafety.model.History;

import java.util.ArrayList;

/**
 * Created by comsol on 11/8/2015.
 */
public class AppConstant {





    public static final String GCM_SENDER_ID = "651399612622";

    public static String BaseUrl = "http://women-safety.co.nf/web_server_demo_gcm/";

    public static int NUM_OF_UNSEEN_HISTORY = 2;

    public static ArrayList<History> histories = new ArrayList<History>();

    public static final int[] notification_range = {500,1000,1500,2000, 2500, 3000, 3500, 4000};


    public static String getUserRegUrl(String regId, String name, String email, String password, String phone_no, String device_id) {
        return BaseUrl + "register_user.php?"
                + "regId=" + regId
                + "&name=" + name
                + "&email=" + email
                + "&password=" + password
                + "&phone_no=" + phone_no
                + "&device_id=" + device_id;
    }

    public static String getUrlForHelpSend(String regId, String lat, String lang, int range) {
        return BaseUrl + "send_message_to_all.php?"
                + "regId=" + regId
                + "&lat=" + lat
                + "&lng=" + lang
                + "&range=" + String.valueOf(range);

    }

    public static String getLoginUrl(String email, String password, String deviceId, String regId) {
        return BaseUrl + "login.php?"
                + "email=" + email
                + "&password=" + password
                + "&device_id=" + deviceId
                + "&reg_id=" + regId;

    }

    public static String getUrlForPoliceInfo(String region) {
        return BaseUrl + "police.php?"
                + "region=" + region;
    }

    public static String getUrlForHistoryList(String user_id,double lat,double lng,int range) {
        return BaseUrl + "history.php?"
                + "&user_id=" + user_id
                + "&lat=" + lat
                + "&lng=" + lng
                + "&range=" + String.valueOf(range);

    }
    public static String getUrlForSetHistorySeenUnseen(String user_id,String event_id) {
        return BaseUrl + "seen.php?"
                + "user_id=" + user_id + "&event_id=" + event_id;

       // user_id=16&event_id=2
    }
    public static String getUrlForHeatMap() {
        return BaseUrl + "hitmap.php";
    }

    public static String DirectionApiUrl(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("AIzaSyBJonkf9zcXK2o1Y9mSbVfHiYjjw6qFkRY");

        return urlString.toString();
    }


}
