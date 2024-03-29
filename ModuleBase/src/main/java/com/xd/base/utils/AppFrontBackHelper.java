package com.pw.base.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.pw.base.base.BaseActivity;

/**
 * Created by developer on 2019/7/15.
 */

public class AppFrontBackHelper {


    private OnAppStatusListener mOnAppStatusListener;


    public AppFrontBackHelper() {

    }

    /**
     * 注册状态监听，仅在Application中使用
     * @param application
     * @param listener
     */
    public void register(Application application, OnAppStatusListener listener){
        mOnAppStatusListener = listener;
        application.registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    public void unRegister(Application application){
        application.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks);
    }

    private Application.ActivityLifecycleCallbacks activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        //打开的Activity数量统计
        private int activityStartCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (activity instanceof BaseActivity) {
                activityStartCount++;
                //数值从0变到1说明是从后台切到前台
                if (activityStartCount == 1) {
                    //从后台切到前台
                    if (mOnAppStatusListener != null) {
                        mOnAppStatusListener.onFront(activity);
                    }
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            if (mOnAppStatusListener != null) {
                mOnAppStatusListener.onActivityResume(activity);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            if (activity instanceof BaseActivity) {
                activityStartCount--;
                //数值从1到0说明是从前台切到后台
                if (activityStartCount == 0) {
                    //从前台切到后台
                    if (mOnAppStatusListener != null) {
                        mOnAppStatusListener.onBack();
                    }
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    };

    public interface OnAppStatusListener{
        void onActivityResume(Activity activity);
        void onFront(Activity activity);
        void onBack();
    }
}
