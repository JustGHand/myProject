package com.pw.codeset.other.HandlerThread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class MyHandlerThread extends Thread{

    private Looper mLooper;
    private Handler mHandler;

    @Override
    public void run() {
        super.run();
        Looper.prepare();
        synchronized (this) {
            if (mLooper == null) {
                mLooper = Looper.myLooper();
            }
            notifyAll();
        }
        Looper.loop();//loop不会退出，需要手动调用quit方法
    }

    public Handler getThreadHandler() {
        if (mHandler == null) {
            mHandler = new Handler(getLooper());
        }
        return mHandler;
    }

    private Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        synchronized (this) {
            while (isAlive() && mLooper == null) {
                try {
                    wait();
                } catch (Exception e) {

                }
            }
        }
        return mLooper;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    public boolean quit() {
        if (mLooper != null) {
            mLooper.quit();
            return true;
        }
        return false;
    }

    public boolean quitSafely() {
        if (mLooper != null) {
            mLooper.quitSafely();
            return true;
        }
        return false;
    }
}
