package com.pw.base.download;

public interface DownloadListener {

    void onProgress(DownloadBean task);

    void onTaskStart(DownloadBean task);

    void onDownloadStart(DownloadBean task);

    void onTaskEnd(DownloadBean task);


}
