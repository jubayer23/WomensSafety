package com.creative.womenssafety.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveManager {

    // Shared Preferences
    private static SharedPreferences mSharedPreferences;

    private static final int PRIVATE_MODE = Context.MODE_PRIVATE;
    private static final String PREF_NAME = "com.creative.womenssafety";
    private static final String KEY_HOMEBUTTON_CLICK_COUNTER= "home_button_click_counter";
    private static final String KEY_HOMEBUTTON_LASTCLICK_TIME= "home_button_last_click_time";
    private static final String KEY_IS_LOGGED_IN= "is_logged_in";
    private static final String KEY_USER_GCM_REG_ID= "user_reg_id";






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

}