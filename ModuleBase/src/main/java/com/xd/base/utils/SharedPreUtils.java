package com.pw.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * Created by newbiechen on 17-4-16.
 */

public class SharedPreUtils {
    private static final String SHARED_NAME = "IReader_pref";
    private static SharedPreUtils sInstance;
    private SharedPreferences sharedReadable;
    private SharedPreferences.Editor sharedWritable;

    private SharedPreUtils(Context context){
        sharedReadable = context
                .getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
        sharedWritable = sharedReadable.edit();
    }

    public static SharedPreUtils getInstance(Context context){
        if(sInstance == null){
            synchronized (SharedPreUtils.class){
                if (sInstance == null){
                    sInstance = new SharedPreUtils(context);
                }
            }
        }
        return sInstance;
    }

    public String getString(String key){
        return sharedReadable.getString(key,"");
    }

    public void putString(String key,String value){
        sharedWritable.putString(key,value);
        sharedWritable.commit();
    }

    public void putInt(String key,int value){
        sharedWritable.putInt(key, value);
        sharedWritable.commit();
    }
    public void putFloat(String key,float value){
        sharedWritable.putFloat(key, value);
        sharedWritable.commit();
    }

    public void putLong(String key, long value) {
        sharedWritable.putLong(key,value);
        sharedWritable.commit();
    }

    public void putBoolean(String key,boolean value){
        sharedWritable.putBoolean(key, value);
        sharedWritable.commit();
    }

    public int getInt(String key,int def){
        return sharedReadable.getInt(key, def);
    }
    public float getFloat(String key,float def){
        return sharedReadable.getFloat(key, def);
    }

    public long getLong(String key,long def){
        return sharedReadable.getLong(key, def);
    }

    public boolean getBoolean(String key,boolean def){
        return sharedReadable.getBoolean(key, def);
    }

    public Map<String, ?> getAll() {
        return sharedReadable.getAll();
    }
//
//    public boolean checkKeyExpired(String key) {
//        long curTime = System.currentTimeMillis() / 1000;
//        long cacheTimeSecond = SharedPreUtils.getInstance().getLong(key, 0);
//        if (YYOLParmManage.getInstance().getExpireCacheSecond() == 0) {
//            return false;
//        }
//
//        //当前时间减去在线参数过期秒数  >  缓存时间 则过期
//        if (curTime - YYOLParmManage.getInstance().getExpireCacheSecond() > cacheTimeSecond) {
//            return true;
//        }
//        return false;
//    }
}
