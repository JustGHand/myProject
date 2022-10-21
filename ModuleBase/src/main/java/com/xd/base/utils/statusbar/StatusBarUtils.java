package com.xd.base.utils.statusbar;
import static com.xd.base.utils.statusbar.XiaoMiStatusBarTextFontUtils.MIUISetStatusBarLightMode;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Create by tsm
 * on 2021/6/24
 * 对外提供沉浸式方法
 */
public class StatusBarUtils {

    public static void fullScreen(Activity activity,boolean fullScreen,int statusColor,boolean isDarkText) {
        if (fullScreen) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            StatusBarUtils.setStatusBarColor(activity,statusColor,isDarkText);
        }
    }

    /**
     *  沉浸式状态栏
     * @param activity
     * @param isDarkMode
     */
    public static void fitStatusBar(Activity activity,boolean isDarkMode){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Window window = activity.getWindow();
                    //始终显示状态栏
                    int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                    //适配刘海屏
                    LollipStatusBarUtils.fitsNotchScreen(window);
                    //修改状态栏颜色,同时将flag增加 将布局内容拓展到状态栏的后面 ,这里也可以修改底部导航栏颜色
                    uiFlags=LollipStatusBarUtils.initBarAboveLOLLIPOP(activity,uiFlags);
                    //设置深色底部导航栏 即黑白图标
                    uiFlags = LollipStatusBarUtils.setNavigationIconDark(uiFlags,isDarkMode);
                    ///修改字体颜色
                    uiFlags=AndroidStatusbarTextFontUtils.setAndroidNativeLightStatusBar(uiFlags,isDarkMode);
                    //修改状态栏可见性
                    uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//将布局内容拓展到状态栏的后面
                    window.getDecorView().setSystemUiVisibility(uiFlags);
                    ///修改小米6和魅族部分系统的字体颜色
                    LollipStatusBarUtils.setOtherStatusBarLightMode(activity.getWindow(), isDarkMode);
                }else{//19-20 只能添加控件, 并修改背景颜色-------
                    KitKatStatusBar.initBarKitKat(activity,isDarkMode);
                }
            }
        }catch (Exception e){

        }
    }

    public static void setStatusBarColor(Activity activity, int color,boolean isDarkText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LollipStatusBarUtils.setStatusBarColor(activity,color);
            if (isDarkText) {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }else {
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
    }


    public static void myStatusBar(Activity activity,int backColor,boolean isWordDark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUISetStatusBarLightMode(activity.getWindow(), isWordDark)) {//MIUI
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    activity.getWindow().setStatusBarColor(backColor);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    try {
                        SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                        tintManager.setStatusBarTintEnabled(true);
                        tintManager.setStatusBarTintResource(backColor);
                    } catch (Exception e) {

                    }
                }
            } else if (FlymeSetStatusBarLightMode(activity.getWindow(), isWordDark)) {//Flyme
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0
                    activity.getWindow().setStatusBarColor(backColor);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    try {
                    SystemBarTintManager tintManager = new SystemBarTintManager(activity);
                    tintManager.setStatusBarTintEnabled(true);
                    tintManager.setStatusBarTintResource(backColor);
                    } catch (Exception e) {

                    }
                }
                }
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
                    activity.getWindow().setStatusBarColor(backColor);
                    activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
            } catch (Exception e) {

            }

    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean MIUISetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     *
     * @param window 需要设置的窗口
     * @param dark   是否把状态栏字体及图标颜色设置为深色
     * @return boolean 成功执行返回true
     */
    public static boolean FlymeSetStatusBarLightMode(Window window, boolean dark) {
        boolean result = false;
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }
    public static void fitStatusBarWithColor(Activity activity,boolean isDarkMode,int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    Window window = activity.getWindow();
                    //始终显示状态栏
                    int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                    //适配刘海屏
                    LollipStatusBarUtils.fitsNotchScreen(window);
                    //修改状态栏颜色,同时将flag增加 将布局内容拓展到状态栏的后面 ,这里也可以修改底部导航栏颜色
                    uiFlags=LollipStatusBarUtils.initBarAboveLOLLIPOPWithColor(activity,uiFlags,color);
                    //设置深色底部导航栏 即黑白图标
                    uiFlags = LollipStatusBarUtils.setNavigationIconDark(uiFlags,isDarkMode);
                    ///修改字体颜色
//                    uiFlags=AndroidStatusbarTextFontUtils.setAndroidNativeLightStatusBar(uiFlags,isDarkMode);
                    //修改状态栏可见性
                    uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;//将布局内容拓展到状态栏的后面
                    window.getDecorView().setSystemUiVisibility(uiFlags);
                    ///修改小米6和魅族部分系统的字体颜色
                    LollipStatusBarUtils.setOtherStatusBarLightMode(activity.getWindow(), isDarkMode);
                }else{//19-20 只能添加控件, 并修改背景颜色-------
                    KitKatStatusBar.initBarKitKat(activity,isDarkMode);
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 沉浸式态栏,再修改字体颜色,并修改Toolbar高度
     * @param activity
     * @param isDarkMode
     * @param toolbar
     */
    public static void fitStatusBarAttachToolBar(Activity activity,boolean isDarkMode,View toolbar){
        fitStatusBar(activity,isDarkMode);//沉浸式
        attachToolBarToStatusBar(toolbar);//修改顶部View的高度
    }


    /**
     *  修正状态栏高度
     * @param toolbar
     */
    public static void attachToolBarToStatusBar(View toolbar){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){// 达到了19就需要修改状态栏高度了
            if(!HuaWeiStatusBarUtils.isEMUI3_x()){
                int left=toolbar.getPaddingLeft();
                int top=toolbar.getPaddingTop();
                int right=toolbar.getPaddingRight();
                int bottom=toolbar.getPaddingBottom();
                toolbar.setPadding(left,top+getStatusBarHeight(),right,bottom);
            }else{///状态栏会自动隐藏,所以需要监听,先这样吧,

            }
        }
    }

    /**
     * 修改状态栏字体样式
     * @param activity
     * @param isDarkMode
     */
    public static void setStatusBarLightMode(@NonNull final Activity activity,
                                             final boolean isDarkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LollipStatusBarUtils.setStatusBarLightMode(activity.getWindow(), isDarkMode);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            KitKatStatusBar.setStatusBarView(activity,isDarkMode);
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight() {
        Resources resources = Resources.getSystem();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }
}
