package com.kt.gigastorage.mobile.utils;

import android.content.Context;
import android.provider.Settings;

/**
 * Created by a-raise on 2016-09-26.
 */
public class DeviceUtil {
    public static String getDevicesUUID(Context context) {

        final String androidId;

        androidId = "" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        return androidId;
    }
}
