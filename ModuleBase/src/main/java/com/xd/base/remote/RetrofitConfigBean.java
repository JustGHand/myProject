package com.xd.base.remote;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetrofitConfigBean {
    String baseUrl;
    Map<String, String> header;
    Boolean needRetry;
    Long readTimeout;
    Long connectTimeout;
    Long writeTimeout;

    public RetrofitConfigBean(String baseUrl, Map<String, String> header, boolean needRetry,
                       long readTimeout,
                       long connectTimeout,
                       long writeTimeout){
        this.baseUrl = baseUrl;
        this.header = header;
        this.needRetry = needRetry;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
        this.writeTimeout = writeTimeout;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }

    public boolean isNeedRetry() {
        if (needRetry == null) {
            needRetry = true;
        }
        return needRetry;
    }

    public void setNeedRetry(Boolean needRetry) {
        this.needRetry = needRetry;
    }

    public long getReadTimeout() {
        if (readTimeout == null) {
            readTimeout = 4L;
        }
        return readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public long getConnectTimeout() {
        if (connectTimeout == null) {
            connectTimeout = 3L;
        }
        return connectTimeout;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public long getWriteTimeout() {
        if (writeTimeout == null) {
            writeTimeout = 5L;
        }
        return writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }
}
