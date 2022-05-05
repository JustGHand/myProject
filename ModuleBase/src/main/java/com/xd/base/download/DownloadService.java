package com.xd.base.download;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.liulishuo.okdownload.DownloadSerialQueue;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.OkDownload;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.xd.base.R;
import com.xd.base.file.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Project:DownloadDemo</p>
 * <p>Package:PACKAGE_NAME</p>
 * <p>Description:下载 后台服务</p>
 * <p>Company: 公众号 Coder栈</p>
 *
 * @author ryandu
 * @date 2017/7/27
 */
public class DownloadService extends Service {

    private static final String TAG = "XD_okDownload";

    private String downloadUrl;

    private Activity mActivity;

    public DownloadService() {
    }

    private DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    Map<String,DownloadSerialQueue> queueMap;

    private DownloadSerialQueue getDefaultQueue() {
        DownloadSerialQueue downloadSerialQueue = getQueue("default");
        if (downloadSerialQueue == null) {
            downloadSerialQueue = new DownloadSerialQueue();
            queueMap.put("default", downloadSerialQueue);
        }
        return downloadSerialQueue;
    }

    private void addQueue(String tag, DownloadSerialQueue queue) {
        if (queueMap == null || queueMap.isEmpty()) {
            queueMap = new HashMap<>();
        }
        queueMap.put(tag, queue);
    }

    private DownloadSerialQueue getQueue(String tag) {
        if (queueMap == null || queueMap.isEmpty()) {
            queueMap = new HashMap<>();
        }
        DownloadSerialQueue downloadSerialQueue = queueMap.get(tag);
        return downloadSerialQueue;
    }

    public class DownloadBinder extends Binder{

        public void init(int maxParalle) {
            OkDownload.Builder builder = new OkDownload.Builder(DownloadService.this)
                    ;
            try {
                OkDownload.setSingletonInstance(builder.build());
            } catch (Exception e) {

            }
            DownloadDispatcher.setMaxParallelRunningCount(maxParalle);
            queueMap = new HashMap<>();
        }

        public void setNotificationActivity(Activity activity) {
            if (activity != null) {
                mActivity = activity;
            }
        }

        public DownloadTask startDownloadWithQueueTag(String queueTag,DownloadTask task,DownloadListener downloadListener){
            DownloadSerialQueue downloadSerialQueue = getQueue(queueTag);
            if (downloadSerialQueue == null) {
                downloadSerialQueue = new DownloadSerialQueue(downloadListener);
                addQueue(queueTag,downloadSerialQueue);
            }else {
                downloadSerialQueue.setListener(downloadListener);
            }
            downloadSerialQueue.enqueue(task);
            return task;
        }

        public void startTaskImmediately(String queueTag, DownloadTask task,DownloadListener downloadListener) {
            DownloadSerialQueue downloadSerialQueue = getQueue(queueTag);
            if (downloadSerialQueue == null) {
                downloadSerialQueue = new DownloadSerialQueue(downloadListener);
                addQueue(queueTag,downloadSerialQueue);
            } else if (downloadListener != null) {
                downloadSerialQueue.setListener(downloadListener);
            }
            downloadSerialQueue.taskStart(task);
        }

        public void pauseDownload(){
        }

        public void cancelAll() {
            OkDownload.with().downloadDispatcher().cancelAll();
        }

        public void cancelTask(DownloadTask task){
            task.cancel();
        }

        public void deleteTask(DownloadTask task) {
            task.cancel();
            OkDownload.with().breakpointStore().remove(task.getId());
        }

        public boolean isDownloading(DownloadTask downloadTask) {
            return StatusUtil.isSameTaskPendingOrRunning(downloadTask);
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this,mActivity.getClass());
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0) {
            //当progress大于或等0时才需要显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("notification_id");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("notification_id", "notification_name", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
        return builder.build();
    }
}
