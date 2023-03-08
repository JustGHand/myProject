package com.pw.baseutils.retrypool;


import android.content.Context;

import androidx.annotation.NonNull;

import com.android.volley.RequestQueue;
import com.android.volley.error.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RequestRetryPool {

    private List<RequestModel> requestList;
    private String mTarFilePath;
    private RequestQueue mRequestQueue;
    private RequestListener mRequestListener;

    public interface RequestListener{
        void success();
    }

    private static RequestRetryPool sInstance;

    public static RequestRetryPool getInstance(){
        if (sInstance == null){
            synchronized (RequestRetryPool.class){
                if (sInstance == null){
                    sInstance = new RequestRetryPool();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     * @param tarFilePath 存储数据文件路径
     * @param context
     * @param requestQueue
     */
    public  void init(@NonNull String tarFilePath, Context context, RequestQueue requestQueue, RequestListener requestListener) {
        mTarFilePath = tarFilePath;
        mRequestListener = requestListener;
        if (requestQueue != null) {
            mRequestQueue = requestQueue;
        } else {
            mRequestQueue = Volley.newRequestQueue(context);
        }
        varifyRequestList();
    }

    /**
     * 添加需要重试的请求数据
     * @param requestModel
     */
    public  void addRequest(RequestModel requestModel) {
        if (requestModel == null || Utils.isBlank(requestModel.getUrl())) {
            return;
        }
        varifyRequestList();
        requestList.add(requestModel);
        writeRequestListToFile();
    }

    /**
     * 清空变量及文件中请求列表数据
     */
    public  void clearRequestList() {
        varifyRequestList();
        requestList = new ArrayList<>();
        writeRequestListToFile();
    }


    /**
     * 开始重试已添加的请求
     * 依次请求，成功一条删除一条再开始下一条请求
     * 本方法需使用者在合适的时机手动调用
     */
    public void startRetry() {
        varifyRequestList();
        if (mRequestQueue == null) {
            return;
        }
        if (requestList != null && requestList.size() > 0) {
            RequestModel requestModel = requestList.get(0);
            sendRequest(requestModel);
        }
    }

    /**
     * 根据requestModel获取数据发起请求
     * 本方法可根据项目更改请求方法，只需要在成功时调用requestRetrySuccess(RequestModel requestModel)方法
     * @param requestModel
     */
    private void sendRequest(final RequestModel requestModel) {
        if (requestModel == null) {
            return;
        }
        int curMethod;
        switch (requestModel.getMethod()) {
            case RequestModel.POST:
                curMethod = com.android.volley.Request.Method.POST;
                break;
            case RequestModel.GET:
                curMethod = com.android.volley.Request.Method.GET;
                break;
            default:
                curMethod = com.android.volley.Request.Method.POST;
                break;
        }

        String url = requestModel.getUrl();
        if (Utils.isBlank(url)) {
            return;
        }

        String param = "";
        if (requestModel.getParam() != null) {
            param = requestModel.getParam();
        }

        Request request = new Request(curMethod, url, param, new Request.ResponseListener() {
            @Override
            public void onResponse(int statusCode) {
                if (statusCode == 200) {
                    requestRetrySuccess(requestModel);
                }
            }
        }, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //失败不做处理
                //等待下一次使用者调用startRetry()方法
            }
        }, true);
        mRequestQueue.add(request);
    }

    /**
     * 请求成功则删除本条请求数据后再次调用startRetry()方法获取下一条数据并发起请求
     */
    private void requestRetrySuccess(RequestModel requestModel) {
        if (mRequestListener != null) {
            mRequestListener.success();
        }
        removeRequest(requestModel);
        startRetry();
    }

    /**
     * 检查请求列表数据，确保数据安全
     */
    private  void varifyRequestList() {
        if (requestList == null) {
            readRequestListFromFile();
        }
        if (requestList == null) {
            requestList = new ArrayList<>();
        }
    }

    /**
     * 从文件中读取请求列表数据
     */
    private  void readRequestListFromFile() {
        try {
            File file = Utils.getFile(mTarFilePath);
            String requestListString = Utils.readFile(file);
            requestList = new Gson().fromJson(requestListString, new TypeToken<List<RequestModel>>() {}.getType());
        } catch (Exception e) {
            requestList = new ArrayList<>();
        }
    }

    /**
     * 向文件写入请求列表数据
     */
    private  void writeRequestListToFile() {
        varifyRequestList();
        try {
            String requestListString = new Gson().toJson(requestList);
            File file = Utils.getFile(mTarFilePath);
            Utils.saveFile(requestListString, file);
        } catch (Exception e) {

        }
    }

    /**
     * 删除已成功重试的请求数据并写入文件
     * @param requestModel
     */
    private  void removeRequest(RequestModel requestModel) {
        varifyRequestList();
        if (requestModel != null) {
            requestList.remove(requestModel);
            writeRequestListToFile();
        }
    }

}
