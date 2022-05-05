package com.xd.base.remote;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;

/**
 * Created with IntelliJ IDEA.
 * User: ivan
 * Date: 14-8-24
 * Time: 10:02
 */
public class RequestManager {


    public static RequestManager mInstance;

    public static RequestManager getInstance() {
        if (mInstance == null) {
            synchronized (RequestManager.class) {
                if (mInstance == null) {
                    mInstance = new RequestManager();
                }
            }
        }
        return mInstance;
    }


    //初始化volley的普通request
    public void init(String errorFilePath, RequestRetryManager.RequestListener requestRetryListener) {
        RequestRetryManager.getInstance().init(errorFilePath,requestRetryListener);
    }


    public Retrofit creatRetrofit(RetrofitConfigBean retrofitConfigBean) {
        Retrofit retrofit = RemoteHelper.creatRetrofit(retrofitConfigBean.getBaseUrl(),
                retrofitConfigBean.getHeader(),
                retrofitConfigBean.isNeedRetry(),
                retrofitConfigBean.getReadTimeout(), TimeUnit.SECONDS,
                retrofitConfigBean.getConnectTimeout(), TimeUnit.SECONDS,
                retrofitConfigBean.getWriteTimeout(), TimeUnit.SECONDS);
        return retrofit;
    }

    public Retrofit creatRetrofit(RetrofitConfigBean retrofitConfigBean, RemoteHelper.HttpListener listener) {
        Retrofit retrofit = RemoteHelper.creatRetrofit(retrofitConfigBean.getBaseUrl(),
                retrofitConfigBean.getHeader(),
                retrofitConfigBean.isNeedRetry(),
                retrofitConfigBean.getReadTimeout(), TimeUnit.SECONDS,
                retrofitConfigBean.getConnectTimeout(), TimeUnit.SECONDS,
                retrofitConfigBean.getWriteTimeout(), TimeUnit.SECONDS,
                listener);
        return retrofit;
    }


}
