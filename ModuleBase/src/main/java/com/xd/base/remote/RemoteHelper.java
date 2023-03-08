package com.pw.base.remote;


import android.util.Base64;

import androidx.annotation.NonNull;

import com.pw.base.utils.XorEncryptUtil2;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by newbiechen on 17-4-20.
 */

public class RemoteHelper {

    public interface HttpListener {

        Map<String,String> getHeader();

        Response responseTransform(Response response);

        boolean onError(int httpErrorCode);
    }

    public static Retrofit creatRetrofit(@NonNull String baseUrl, Map<String, String> header, boolean needRetry,
                              long readTimeout,TimeUnit readTimeUnit,
                              long connectTimeout,TimeUnit connectTimeUnit,
                              long writeTimeout,TimeUnit writeTimeUnit) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(readTimeout, readTimeUnit)
                    .connectTimeout(connectTimeout,connectTimeUnit)
                    .writeTimeout(writeTimeout,writeTimeUnit)
                    .retryOnConnectionFailure(needRetry)
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request();
                                    Request newRequest = addBaseParm(request,header);

                                    //在这里获取到request后就可以做任何事情了
                                    Response response = chain.proceed(newRequest);
                                    if (!response.isSuccessful()) {
                                        RetryModel retryModel = new RetryModel(newRequest);
                                        retryModel.setRetryTime(0);
                                        RequestRetryManager.getInstance().addRequest(retryModel);
                                    }
                                    return response;
                                }
                            }
                    ).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }

    public static Retrofit creatRetrofit(@NonNull String baseUrl, Map<String, String> header, boolean needRetry,
                                         long readTimeout, TimeUnit readTimeUnit,
                                         long connectTimeout, TimeUnit connectTimeUnit,
                                         long writeTimeout, TimeUnit writeTimeUnit, HttpListener listener) {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(readTimeout, readTimeUnit)
                    .connectTimeout(connectTimeout,connectTimeUnit)
                    .writeTimeout(writeTimeout,writeTimeUnit)
                    .retryOnConnectionFailure(needRetry)
                    .addInterceptor(
                            new Interceptor() {
                                @Override
                                public Response intercept(Chain chain) throws IOException {
                                    Request request = chain.request();
                                    Request newRequest = addBaseParm(request,listener.getHeader());

                                    //在这里获取到request后就可以做任何事情了
                                    Response response = chain.proceed(newRequest);
                                    Response transformedResponse = listener.responseTransform(response);
                                    if (!transformedResponse.isSuccessful()) {
                                        RetryModel retryModel = new RetryModel(newRequest);
                                        retryModel.setRetryTime(0);
                                        RequestRetryManager.getInstance().addRequest(retryModel);
                                        listener.onError(transformedResponse.code());
                                    }
                                    return transformedResponse;
                                }
                            }
                    ).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(baseUrl)
                .build();
        return retrofit;
    }


    private static Request addBaseParm(Request oldRequest,Map<String,String> mHeaders) {

        if (mHeaders == null || mHeaders.isEmpty()) {
            return oldRequest;
        }

        HttpUrl.Builder builder = oldRequest.url()
                .newBuilder();
        //TODO ... 通用参数这里加
//                    .setEncodedQueryParameter("freeappid", hostInfo.getAppid())
//                    .setEncodedQueryParameter("isfree", hostInfo.getIsFree());

        Request.Builder requestBuilder = oldRequest.newBuilder()
                .method(oldRequest.method(), oldRequest.body());
        Set<Map.Entry<String,String>> entries=mHeaders.entrySet();
        for (Map.Entry entry:entries){
            String headerName = (String) entry.getKey();
            String headerValue = (String) entry.getValue();
            requestBuilder.addHeader(headerName,headerValue);
        }

        Request newRequest = requestBuilder
                .url(builder.build())
                .build();
        return newRequest;
    }


}
