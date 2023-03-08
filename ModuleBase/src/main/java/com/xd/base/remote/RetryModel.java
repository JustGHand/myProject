package com.pw.base.remote;

import okhttp3.Request;

public class RetryModel {
    Request request;
    int retryTime;

    public RetryModel(Request request) {
        this.request = request;
        this.retryTime = 1;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public void setRetryTime(int retryTime) {
        this.retryTime = retryTime;
    }
}
