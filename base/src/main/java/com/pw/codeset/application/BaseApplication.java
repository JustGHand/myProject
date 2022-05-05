package com.pw.codeset.application;

import android.app.Application;
import android.content.Context;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    public static BaseApplication mInstance;

    public static Context getContext() {
        return mInstance.getContext();
    }

}
