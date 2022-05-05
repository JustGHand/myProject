package com.xd.base.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.List;
import java.util.UUID;

public class DeviceUtils {

    /**
     * 获取设备唯一标识符
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        //读取保存的在sd卡中的唯一标识符
        String deviceId ;
        //用于生成最终的唯一标识符
        StringBuffer s = new StringBuffer();
        try {
            //获取IMES(也就是常说的DeviceId)
            deviceId = getIMIEStatus(context);
            s.append(deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            //获取设备的MACAddress地址 去掉中间相隔的冒号
            deviceId = getLocalMac(context).replace(":", "");
            s.append(deviceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        }
        try {
            s.append(Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //如果以上搜没有获取相应的则自己生成相应的UUID作为相应设备唯一标识符
        if (s == null || s.length() <= 0) {
            UUID uuid = UUID.randomUUID();
            deviceId = uuid.toString().replace("-", "");
            s.append(deviceId);
        }
        //为了统一格式对设备的唯一标识进行md5加密 最终生成32位字符串
//        String md5 = getMD5(s.toString(), false);
        return s.toString();
    }


    /**
     * 获取设备的DeviceId(IMES) 这里需要相应的权限<br/>
     * 需要 READ_PHONE_STATE 权限
     *
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static String getIMIEStatus(Context context) {
        String deviceId;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 29) {
            deviceId = Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            // request old storage permission
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            deviceId = tm.getDeviceId();
        }
        return deviceId;
    }

    @SuppressLint("MissingPermission")
    public static String getIMSI(Context context) {
        String deviceIMSI;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= 29) {
            deviceIMSI = "";
        } else {
            // request old storage permission
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            deviceIMSI = tm.getSubscriberId();
        }
        return deviceIMSI;
    }

    /**
     * 获取设备MAC 地址 由于 6.0 以后 WifiManager 得到的 MacAddress得到都是 相同的没有意义的内容
     * 所以采用以下方法获取Mac地址
     * @param context
     * @return
     */
    public static String getLocalMac(Context context) {
//        WifiManager wifi = (WifiManager) context
//                .getSystemService(Context.WIFI_SERVICE);
//        WifiInfo info = wifi.getConnectionInfo();
//        return info.getMacAddress();


        String macAddress = null;
        StringBuffer buf = new StringBuffer();
        NetworkInterface networkInterface = null;
        try {
            networkInterface = NetworkInterface.getByName("eth1");
            if (networkInterface == null) {
                networkInterface = NetworkInterface.getByName("wlan0");
            }
            if (networkInterface == null) {
                return "";
            }
            byte[] addr = networkInterface.getHardwareAddress();

            if (addr == null || addr.length <= 0){
                return "";
            }
            for (byte b : addr) {
                buf.append(String.format("%02X:", b));
            }
            if (buf.length() > 0) {
                buf.deleteCharAt(buf.length() - 1);
            }
            macAddress = buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return macAddress;


    }

//
//    /**
//     * 对挺特定的 内容进行 md5 加密
//     * @param message  加密明文
//     * @param upperCase  加密以后的字符串是是大写还是小写  true 大写  false 小写
//     * @return
//     */
//    public static String getMD5(String message, boolean upperCase) {
//        String md5str = "";
//        try {
//            MessageDigest md = MessageDigest.getInstance("MD5");
//
//            byte[] input = message.getBytes();
//
//            byte[] buff = md.digest(input);
//
//            md5str = bytesToHex(buff, upperCase);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return md5str;
//    }
//
//
//    public static String bytesToHex(byte[] bytes, boolean upperCase) {
//        StringBuffer md5str = new StringBuffer();
//        int digital;
//        for (int i = 0; i < bytes.length; i++) {
//            digital = bytes[i];
//
//            if (digital < 0) {
//                digital += 256;
//            }
//            if (digital < 16) {
//                md5str.append("0");
//            }
//            md5str.append(Integer.toHexString(digital));
//        }
//        if (upperCase) {
//            return md5str.toString().toUpperCase();
//        }
//        return md5str.toString().toLowerCase();
//    }


    /**
     * 判断是否是刘海屏
     * @return
     */
    public static boolean hasNotchScreen(Activity activity){
        if (getInt("ro.miui.notch",activity) == 1 || hasNotchAtHuawei(activity) || hasNotchAtOPPO(activity.getApplicationContext())
                || hasNotchAtVivo(activity) || isAndroidP(activity) != null){ //TODO 各种品牌
            return true;
        }

        return false;
    }


    /**
     * Android P 刘海屏判断
     * @param activity
     * @return
     */
    public static DisplayCutout isAndroidP(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        if (decorView != null && android.os.Build.VERSION.SDK_INT >= 28){
            WindowInsets windowInsets = decorView.getRootWindowInsets();
            if (windowInsets != null)
                return windowInsets.getDisplayCutout();
        }
        return null;
    }
    // 是否是小米手机
    public static boolean isXiaomi() {
        return "Xiaomi".equals(Build.MANUFACTURER);
    }
    /**
     * 小米刘海屏判断.
     * @return 0 if it is not notch ; return 1 means notch
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    public static int getInt(String key,Activity activity) {
        int result = 0;
        if (isXiaomi()){
            try {
                ClassLoader classLoader = activity.getClassLoader();
                @SuppressWarnings("rawtypes")
                Class SystemProperties = classLoader.loadClass("android.os.SystemProperties");
                //参数类型
                @SuppressWarnings("rawtypes")
                Class[] paramTypes = new Class[2];
                paramTypes[0] = String.class;
                paramTypes[1] = int.class;
                Method getInt = SystemProperties.getMethod("getInt", paramTypes);
                //参数
                Object[] params = new Object[2];
                params[0] = new String(key);
                params[1] = new Integer(0);
                result = (Integer) getInt.invoke(SystemProperties, params);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 华为刘海屏判断
     * @return
     */
    public static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }

    public static final int VIVO_NOTCH = 0x00000020;//是否有刘海
    public static final int VIVO_FILLET = 0x00000008;//是否有圆角

    /**
     * VIVO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtVivo(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class FtFeature = classLoader.loadClass("android.util.FtFeature");
            Method method = FtFeature.getMethod("isFeatureSupport", int.class);
            ret = (boolean) method.invoke(FtFeature, VIVO_NOTCH);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }
    /**
     * OPPO刘海屏判断
     * @return
     */
    public static boolean hasNotchAtOPPO(Context context) {
        return  context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
}
