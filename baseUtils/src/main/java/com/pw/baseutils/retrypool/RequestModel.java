package com.pw.baseutils.retrypool;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class RequestModel {

    public static final int POST = 0;
    public static final int GET = 1;

    @IntDef({POST, GET})
    @Retention(RetentionPolicy.SOURCE)
    public @interface method {}

    private String url;
    private String param;
    private int method;
    private Map<String, String> header;

    public RequestModel(String url, String param, @method int method, Map<String, String> header) {
        this.url = url;
        this.param = param;
        this.method = method;
        this.header = header;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

}
