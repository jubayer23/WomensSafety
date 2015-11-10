package com.creative.womenssafety.appdata;

/**
 * Created by comsol on 11/8/2015.
 */
public class AppConstant {


    public static final String GCM_SENDER_ID = "651399612622";

    public static String BaseUrl = "http://jubayer-domain.eu5.org/web_server_demo_gcm/";


    public static String getUserRegUrl(String regId, String name, String email, String password, String phone_no, String device_id) {
        return BaseUrl + "register_user.php?"
                + "regId=" + regId
                + "&name=" + name
                + "&email=" + email
                + "&password=" + password
                + "&phone_no=" + phone_no
                + "&device_id=" + device_id;
    }

    public static String getUrlForHelpSend(String regId, String lat, String lang) {
        return BaseUrl + "send_message_to_all.php?"
                + "regId=" + regId
                + "&lat=" + lat
                + "&lng=" + lang;

    }

    public static String getLoginUrl(String email, String password, String deviceId, String regId) {
        return BaseUrl + "login.php?"
                + "email=" + email
                + "&password=" + password
                + "&device_id=" + deviceId
                + "&reg_id=" + regId;

    }


}
