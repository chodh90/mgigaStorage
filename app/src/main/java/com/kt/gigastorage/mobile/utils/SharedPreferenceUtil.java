package com.kt.gigastorage.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by a-raise on 2016-09-23.
 */
public class SharedPreferenceUtil {

    public static void putSharedPreference
            (Context context, String key, String value){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static void putSharedPreference
            (Context context, String key, boolean isChecked){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(key,isChecked);
        editor.commit();
    }

    public static String getSharedPreference
            (Context context, String key){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(key, null);
    }

    public static boolean getCheckedSharedPreference
            (Context context, String key){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getBoolean(key,false);
    }
}
