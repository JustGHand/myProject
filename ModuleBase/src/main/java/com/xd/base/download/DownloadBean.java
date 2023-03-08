package com.pw.base.download;

import com.pw.base.utils.NStringUtils;

import java.io.Serializable;

public class DownloadBean implements Serializable {

    DownloadBean() {

    }

    int id;
    int status;//0:启动中；1：下载中；2：暂停；3：等待；4：结束；-1：错误
    long totalLength;
    long currentLength;
    String fileName;
    String url;
    String tarFilePath;
    String Tag;
    int priority;
    boolean useTemp = false;

    String taskName;

    public boolean isUseTemp() {
        return useTemp;
    }

    public void setUseTemp(boolean useTemp) {
        this.useTemp = useTemp;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getStatusName() {
        String statusName = "未开始";
        switch (status) {
            case 0:
                statusName = "未开始";
                break;
            case 1:
                statusName = "下载中";
                break;
            case 2:
                statusName = "暂停中";
                break;
            case 3:
                statusName = "等待中";
                break;
            case 4:
                statusName = "已完成";
                break;
            default:
                statusName = "异常";
                break;
        }
        return statusName;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    public DownloadBean(String tag,String url, String tarFilePath) {
        this.Tag = tag;
        this.url = url;
        this.tarFilePath = tarFilePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getProgress() {

        if (totalLength == 0) {
            return 0;
        }

        float progress = (float) currentLength / totalLength * 100;

        return progress;
    }


    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getCurrentLength() {
        return currentLength;
    }

    public void setCurrentLength(long currentLength) {
        this.currentLength = currentLength;
    }

    public String getFileName() {
        if (NStringUtils.isBlank(fileName) && NStringUtils.isNotBlank(tarFilePath)) {
            fileName = NStringUtils.getFileNameFromPath(tarFilePath);
        }
        if (useTemp) {
            fileName = fileName + ".tmp";
        }
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTarFilePath() {
        return tarFilePath;
    }

    public void setTarFilePath(String tarFilePath) {
        this.tarFilePath = tarFilePath;
    }
}
