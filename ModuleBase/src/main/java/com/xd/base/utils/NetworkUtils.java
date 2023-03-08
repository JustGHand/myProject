package com.pw.base.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetworkUtils {


    /**
     * 获取活动网络信息
     * @return NetworkInfo
     */
    @SuppressLint("MissingPermission")
    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo();
    }

    /**
     * 网络是否可用
     * @return
     */
    public static boolean isAvailable(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isAvailable();
    }

    /**
     * 网络是否连接
     * @return
     */
    public static boolean isConnected(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return info != null && info.isConnected();
    }

    /**
     * 判断wifi是否连接状态
     * <p>需添加权限 {@code <uses-permission android:name="android.permission
     * .ACCESS_NETWORK_STATE"/>}</p>
     *
     * @param context 上下文
     * @return {@code true}: 连接<br>{@code false}: 未连接
     */
    @SuppressLint("MissingPermission")
    public static boolean isWifiConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm != null && cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static String getConnectedName(Context context) {
        if (isWifiConnected(context)) {
            return "WIFI";
        }else {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager.getSimOperatorName();
            } catch (Exception e) {
                return "ERROR";
            }
        }
    }


}
