package com.xd.base.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


/**
 * Created by YYSky on 2019/2/18.
 */

public class LocalVersionUtils {
    /**
     * 获取本地软件版本号
     */
    public static int getCodeVersion(Context ctx) {
        int localVersion = 0;
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
            System.out.println("本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        if (ctx == null) {
            return "";
        }
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            System.out.println("本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 版本的比较
     */
    public static boolean isVersionLaster(Context ctx, String version){
        if (NStringUtils.isBlank(version)) {
            return false;
        }
        String localVersion = getLocalVersionName(ctx);
        String[] localVStrs = localVersion.split("\\.");
        String[] lastVStrs = version.split("\\.");

        for (int i=0; i< lastVStrs.length; i++){
            if (Integer.parseInt(lastVStrs[i]) > Integer.parseInt(localVStrs[i])) {
                return true;
            } else if (Integer.parseInt(lastVStrs[i]) < Integer.parseInt(localVStrs[i])) {
                return false;
            }
        }
        return false;
    }
//
//    public static String getForbiddenUpdateFlagV(){
//        String flagv = ConfigUtils.getForbiddenUpdateFlage();
//        if (NStringUtils.isBlank(flagv)){
//            return "0";
//        }
//        return flagv;
//    }
//
//    public static boolean canUpdateWithLatestVersion(Context ctx, String version){
//        if (StringUtils.isBlank(version)) {
//            return false;
//        }
//        String localVersion = getLocalVersionName(ctx);
//        String appChanel = ConfigUtils.getPackageFlavor();
//        String flagV = LocalVersionUtils.getForbiddenUpdateFlagV();
//
//        YYForbiddenUpdateModel forbiddenUpdateModel = YYOLParmManage.getInstance().getForbiddenUpdateInfo();
//        if (forbiddenUpdateModel.getChannel().contains(appChanel) &&
//            localVersion.equals(forbiddenUpdateModel.getFromVersion()) &&
//            version.equals(forbiddenUpdateModel.getToVersion()) &&
//            flagV.equals(forbiddenUpdateModel.getFlagV())){
//            return false;
//        }
//        return true;
//    }
}
