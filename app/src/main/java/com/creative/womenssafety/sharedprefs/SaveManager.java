package com.creative.womenssafety.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.creative.womenssafety.appdata.AppConstant;

import java.util.ArrayList;

public class SaveManager {

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    private static final int PRIVATE_MODE = Context.MODE_PRIVATE;
    private static final String PREF_NAME = "com.creative.womenssafety";
    private static final String KEY_HOMEBUTTON_CLICK_COUNTER = "home_button_click_counter";
    private static final String KEY_HOMEBUTTON_LASTCLICK_TIME = "home_button_last_click_time";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_GCM_REG_ID = "user_reg_id";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_LAT = "user_lat";
    private static final String KEY_USER_LNG = "user_lng";
    private static final String KEY_USER_NOTIFICATION_RANGE = "user_notification_range";
    private static final String KEY_NOTIFICATION_MSG = "notification_msg";



    private SharedPreferences.Editor editor;
    private Context context;

    public SaveManager(Context context) {
        this.context = context;
        mSharedPreferences = getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = mSharedPreferences.edit();
        editor.apply();
    }


    private SharedPreferences getSharedPreferences(final String prefName,
                                                   final int mode) {
        return this.context.getSharedPreferences(prefName, mode);
    }


    public void setLat(String value) {
        editor.putString(KEY_USER_LAT, value);
        editor.apply();
    }

    public Double getLat() {
        return Double.parseDouble(mSharedPreferences.getString(KEY_USER_LAT, "24.913596"));
    }

    public void setLng(String value) {
        editor.putString(KEY_USER_LNG, value);
        editor.apply();
    }

    public Double getLng() {
        return Double.parseDouble(mSharedPreferences.getString(KEY_USER_LNG, "91.90391"));
    }

    public void setUserNotificationRange(int value) {
        editor.putInt(KEY_USER_NOTIFICATION_RANGE, value);
        editor.apply();
    }

    public int getUserNotificationRange() {
        return mSharedPreferences.getInt(KEY_USER_NOTIFICATION_RANGE, AppConstant.notification_range[0]);
    }

    public void setHomebuttonClickCounter(int value) {
        editor.putInt(KEY_HOMEBUTTON_CLICK_COUNTER, value);
        editor.apply();
    }

    public Integer getHomebuttonClickCounter() {
        return mSharedPreferences.getInt(KEY_HOMEBUTTON_CLICK_COUNTER, 0);
    }

    public void setHomebuttonLastclickTime(long value) {
        editor.putLong(KEY_HOMEBUTTON_LASTCLICK_TIME, value);
        editor.apply();
    }

    public long getHomeButtonLastclickTime() {
        return mSharedPreferences.getLong(KEY_HOMEBUTTON_LASTCLICK_TIME, (long) 0.0);
    }

    public void setIsLoggedIn(Boolean value) {
        editor.putBoolean(KEY_IS_LOGGED_IN, value);
        editor.apply();
    }

    public Boolean getIsLoggedIn() {
        return mSharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setUserGcmRegId(String value) {
        editor.putString(KEY_USER_GCM_REG_ID, value);
        editor.apply();
    }

    public String getUserGcmRegId() {
        return mSharedPreferences.getString(KEY_USER_GCM_REG_ID, "0");
    }

    public void setUserEmail(String value) {
        editor.putString(KEY_USER_EMAIL, value);
        editor.apply();
    }

    public String getUserEmail() {
        return mSharedPreferences.getString(KEY_USER_EMAIL, "abc@gmail.com");
    }

    public void setUserName(String value) {
        editor.putString(KEY_USER_NAME, value);
        editor.apply();
    }

    public String getUserId() {
        return mSharedPreferences.getString(KEY_USER_ID, "1");
    }

    public void setUserId(String value) {
        editor.putString(KEY_USER_ID, value);
        editor.apply();
    }

    public String getNotificationMsg() {
        return mSharedPreferences.getString(KEY_NOTIFICATION_MSG, "HELP ME HELP ME!!");
    }

    public void setNotificationMsg(String value) {
        editor.putString(KEY_NOTIFICATION_MSG, value);
        editor.apply();
    }

    public String getUserName() {
        return mSharedPreferences.getString(KEY_USER_NAME, "me");
    }

    public void setPhoneName(ArrayList<String> list) {
        //editor = pref.edit();
        editor.putInt("Count", list.size());
        int count = 0;
        for (String i : list) {
            editor.putString("phoneNameValue_" + count++, i);
        }

        editor.commit();
    }

    public void setPhoneNumber(ArrayList<String> list) {
        //editor = pref.edit();
        editor.putInt("Count", list.size());
        int count = 0;
        for (String i : list) {
            editor.putString("phoneNumberValue_" + count++, i);
        }

        editor.commit();
    }

    public ArrayList<String> getPhoneNameArray() {
        ArrayList<String> temp = new ArrayList<String>();

        int count = mSharedPreferences.getInt("Count", 0);
        temp.clear();
        for (int i = 0; i < count; i++) {
            temp.add(mSharedPreferences.getString("phoneNameValue_" + i, ""));
        }
        return temp;
    }

    public ArrayList<String> getPhoneNumberArray() {
        ArrayList<String> temp = new ArrayList<String>();

        int count = mSharedPreferences.getInt("Count", 0);
        temp.clear();
        for (int i = 0; i < count; i++) {
            temp.add(mSharedPreferences.getString("phoneNumberValue_" + i, ""));
        }
        return temp;
    }

}