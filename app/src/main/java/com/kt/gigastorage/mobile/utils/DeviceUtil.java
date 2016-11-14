package com.kt.gigastorage.mobile.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Created by a-raise on 2016-09-26.
 */
public class DeviceUtil {
    public static String getDevicesUUID(Context context) {

        /*final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);*/
        /*final String deviceId, serial, androidId;*/
        final String androidId;

        androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        /*androidId = Settings.Secure.ANDROID_ID;*/
        /*serial = "" + tm.getSimSerialNumber();
        deviceId = "" + tm.getDeviceId();*/

        /*UID device = new UUID(androidId.hashCode(), ((long) deviceId.hashCode() << 32) | serial.hashCode());
        String deviceUuid = device.toString();*/
        return androidId;
    }


    //클라이언트 IP GET 사용할일 있으면 사용
    /*public static String getDevicesIp(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ipAddress = String.valueOf(wifiInfo.getIpAddress());
            return ipAddress;
        } else if (mobile.isAvailable()) {
            getLocalIpAddress();
        }
        return "No IP Available";
    }

    private static String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if(!inetAddress.isLoopbackAddress()&&inetAddress instanceof Inet4Address){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            return "ERROR Obtaining IP";
        }
        return "No IP Available";
    }*/
}
