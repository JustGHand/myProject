package com.pw.base.download;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.pw.base.file.FileUtils;
import com.pw.base.utils.NStringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DownloadManager {

    public static DownloadManager mInstance;
    public static DownloadManager getInstance() {
        if (mInstance == null) {
            synchronized (DownloadManager.class) {
                if (mInstance == null) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    public DownloadManager() {
    }

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceListener mServiceListener;

    public void startDownloadService(Context context,ServiceListener serviceListener) {
        mServiceListener = serviceListener;
        Intent intent = new Intent(context,DownloadService.class);
        context.startService(intent);//启动服务
        context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                downloadBinder = (DownloadService.DownloadBinder) service;
                downloadBinder.init(3);
                mServiceListener.onServiceConnected(name, service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceListener.onServiceDisconnected(name);
            }
        }, Context.BIND_AUTO_CREATE);//绑定服务

    }

    /**
     * 添加下载任务到队列
     * @param downloadBean
     */
    public void addDownloadTask(DownloadBean downloadBean,DownloadListener downloadListener) {
        DownloadTask task = creatDownloadTask(downloadBean);
        downloadBean.setId(task.getId());
        downloadBinder.startDownloadWithQueueTag(downloadBean.getTag(),task,new DownloadListener4WithSpeed() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {
                downloadBean.setStatus(0);
                downloadListener.onTaskStart(downloadBean);
            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
//                downloadBean.setTotalLength(task.getInfo().getTotalLength());
//                downloadBean.setCurrentLength(task.getInfo().getTotalOffset());
                downloadBean.setStatus(1);
                downloadListener.onDownloadStart(downloadBean);
            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                downloadBean.setTotalLength(task.getInfo().getTotalLength());
                downloadBean.setCurrentLength(currentOffset);
                downloadListener.onProgress(downloadBean);
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                if (cause == EndCause.COMPLETED) {
                    String donwloadFilePath = downloadBean.getTarFilePath()+File.separator+task.getFilename();
                    if (NStringUtils.isNotBlank(donwloadFilePath)) {
                        File file = task.getFile();
                        if (file.exists()&&file.getAbsolutePath().endsWith(".tmp")) {
                            try {
                                String finalPath = file.getAbsolutePath().split(".tmp")[0];
                                File finalFile = new File(finalPath);
                                file.renameTo(finalFile);
                            } catch (Exception e) {

                            }
                        }
                    }
                    downloadBean.setStatus(4);
                } else if (cause == EndCause.CANCELED) {
                    downloadBean.setStatus(2);
                }else {
                    downloadBean.setStatus(-1);
                }
                downloadListener.onTaskEnd(downloadBean);
            }
        });
//        if (!downloadBinder.isDownloading(task)) {
//            downloadBinder.startDownloadWithQueueTag()
//        }
    }

    /**
     * 暂停下载任务
     * @param downloadBean
     */
    public void pauseDownloadTask(DownloadBean downloadBean) {
        DownloadTask task = creatDownloadTask(downloadBean);
        downloadBinder.cancelTask(task);
    }

    public void pauseAllTask() {
        downloadBinder.cancelAll();
    }

    /**
     * 删除下载任务
     * @param downloadBean
     */
    public void deleteDownloadTask(DownloadBean downloadBean) {
        DownloadTask task = creatDownloadTask(downloadBean);
        downloadBinder.deleteTask(task);
    }

    public void startDownloadImmediately(DownloadBean downloadBean) {
        DownloadTask task = creatDownloadTask(downloadBean);
        downloadBinder.startTaskImmediately(downloadBean.getTag(),task,null);
    }

    public DownloadBean syncTask(DownloadBean downloadBean) {
        BreakpointInfo info = StatusUtil.getCurrentInfo(downloadBean.getUrl(), downloadBean.getTarFilePath(), downloadBean.getFileName());
        if (info != null) {
            long totallength = info.getTotalLength();
            long totaloffset = info.getTotalOffset();
            downloadBean.setCurrentLength(totaloffset);
            downloadBean.setTotalLength(totallength);
        }
        return downloadBean;
    }

    /**
     * 创建下载任务
     */
    private DownloadTask creatDownloadTask(DownloadBean downloadBean) {
        File file = new File(downloadBean.getTarFilePath());
        DownloadTask task = new DownloadTask.Builder(downloadBean.getUrl(), file)
                .setFilename(downloadBean.getFileName())
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(100)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(true)
                .setPriority(downloadBean.getPriority())
                .build();
        task.setTag(downloadBean.getTag());
        return task;
    }

    public interface ServiceListener{
        void onServiceConnected(ComponentName componentName, IBinder iBinder);
        void onServiceDisconnected(ComponentName componentName);
    }
}
