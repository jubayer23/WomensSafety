package com.creative.womenssafety.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by fahad_000 on 8/4/2015.
 */
public class DeviceInfoUtils {
    static String deviceId = "";
    public static String getDeviceID(Context context){
        if( deviceId.equals("") ) {
            deviceId = "os:android,";
            deviceId = deviceId + "version:" + getOsVersion() + ",type:" + getDeviceName() + ",retina:null,appversion:" + getAppVersion(context);
        }
        return deviceId;
    }

    public static String getAppVersion(Context context){
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        version.replaceAll(" " , "");
        return version;

    }

    public static int getOsVersion(){
        int currentapiVersion = Build.VERSION.SDK_INT;
        return currentapiVersion;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            String temp = capitalize(model);
            temp=temp.replaceAll(" ", "");
            return temp;
        } else {
            String temp = capitalize(manufacturer) + " " + model;
            temp=temp.replaceAll(" ", "");

            return temp;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
