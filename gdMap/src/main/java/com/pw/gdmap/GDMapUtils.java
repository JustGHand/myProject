package com.pw.gdmap;

import android.content.Context;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.amap.api.navi.NaviSetting;

public class GDMapUtils {

    public static void init(Context context) {
        NaviSetting.updatePrivacyAgree(context,true);
        NaviSetting.updatePrivacyShow(context,true,true);
        try {
            AMapNavi mAMapNavi = AMapNavi.getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void start(Context context) {
        //构建导航组件配置类，没有传入起点，所以起点默认为 “我的位置”
        AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
//启动导航组件
        AmapNaviPage.getInstance().showRouteActivity(context, params, null);
    }

}
