package com.xd.base.remote;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xd.base.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * creat by pw
 * 本功能仅依赖现有网络框架
 * 可替换sendRequest(RequestModel requestModel)方法中的请求方法，只需要在成功请求时调用requestRetrySuccess(RequestModel requestModel)方法
 * 重试的网络请求没有任何回调和响应，仅作为数据上传使用
 */
public class RequestRetryManager {

    private String ERROR_MSG_REQUESTNULL = "request_null";

    private List<RetryModel> requestList;
    private String mTarFilePath;
    private RequestListener mRequestListener;

    public interface RequestListener{
        void success(Response response);

        void error(String msg);
    }

    private static RequestRetryManager sInstance;

    public static RequestRetryManager getInstance(){
        if (sInstance == null){
            synchronized (RequestRetryManager.class){
                if (sInstance == null){
                    sInstance = new RequestRetryManager();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     * @param tarFilePath 存储数据文件路径
     */
    public  void init(@NonNull String tarFilePath, RequestListener requestListener) {
        mTarFilePath = tarFilePath;
        mRequestListener = requestListener;
        varifyRequestList();
    }

    /**
     * 添加需要重试的请求数据
     * @param request
     */
    public  void addRequest(RetryModel request) {
        if (request == null) {
            return;
        }
        varifyRequestList();
        requestList.add(request);
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

        for (int i = 0; i < requestList.size(); i++) {
            RetryModel retryModel = requestList.get(i);
            sendRequest(retryModel);
            break;
        }

    }
    

    /**
     * 根据requestModel获取数据发起请求
     * 本方法可根据项目更改请求方法，只需要在成功时调用requestRetrySuccess(RequestModel requestModel)方法
     * @param retryModel
     */
    private void sendRequest(final RetryModel retryModel) {
        Request request = retryModel.getRequest();
        if (request == null) {
            requestList.remove(retryModel);
            requestRetryFaild(retryModel,ERROR_MSG_REQUESTNULL);
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requestRetryFaild(retryModel,e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                requestRetrySuccess(retryModel,response);
            }

        });
    }

    /**
     * 请求成功则删除本条请求数据后再次调用startRetry()方法获取下一条数据并发起请求
     */
    private void requestRetrySuccess(RetryModel retryModel, Response response) {
        if (mRequestListener != null) {
            mRequestListener.success(response);
        }
        removeRequest(retryModel);
        startRetry();
    }

    /**
     * 重试失败时将该请求置于列表末尾
     * @param retryModel
     * @param msg
     */
    private void requestRetryFaild(RetryModel retryModel,String msg) {
        if (mRequestListener != null) {
            mRequestListener.error(msg);
        }
        requestList.remove(retryModel);
        if (requestList != null && requestList.size() >= 1) {
            startRetry();
        }
        int requestRetryTime = retryModel.getRetryTime();
        if (requestRetryTime < 3) {
            requestRetryTime++;
            retryModel.setRetryTime(requestRetryTime);
            requestList.add(retryModel);
        }
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
            File file = FileUtils.getFile(mTarFilePath);
            String requestListString = FileUtils.readFile(file);
            requestList = new Gson().fromJson(requestListString, new TypeToken<List<Request>>() {}.getType());
        } catch (Exception e) {

        }
        if (requestList == null || requestList.size() <= 0) {
            requestList = new ArrayList<>();
        }
        for (int i = 0; i < requestList.size(); i++) {
            try {
                Request request = requestList.get(i).getRequest();
                if (request == null) {
                    requestList.remove(i);
                }
            } catch (Exception e) {
                requestList.remove(i);
            }
        }
    }

    /**
     * 向文件写入请求列表数据
     */
    private  void writeRequestListToFile() {
        varifyRequestList();
        try {
            String requestListString = new Gson().toJson(requestList);
            File file = FileUtils.getFile(mTarFilePath);
            FileUtils.saveFile(requestListString, file);
        } catch (Exception e) {

        }
    }

    /**
     * 删除已成功重试的请求数据并写入文件
     * @param retryModel
     */
    private  void removeRequest(RetryModel retryModel) {
        varifyRequestList();
        if (retryModel != null) {
            requestList.remove(retryModel);
            writeRequestListToFile();
        }
    }

}
