package com.pw.codeset.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pw.codeset.R;
import com.pw.codeset.utils.AnimUtils;
import com.pw.codeset.weidgt.ActivityHeaderView;
import com.pw.codeset.weidgt.MyProgressDialog;
import com.pw.other.annotation.inject.InjectUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity {

    private static final int HANDLER_HIDE_DIALOG = 1;
    private static final int HANDLER_FINISH_DATA = 11;

    private boolean haveAttachedToWindow = false;

    private Thread mResumeThread;

    protected CompositeDisposable mDisposable;

    ActivityHeaderView mHeader;
    boolean isHeadVisible;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        InjectUtils.injectView(this);
        initHeader();
        initView();
        isCreating = true;
    }

    private void initHeader() {
        mHeader = findViewById(R.id.activity_header);
        if (mHeader != null) {
            mHeader.setOnBackClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackClick(v);
                }
            });
            mHeader.setOnMenuClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMenuClick(v);
                }
            });
            isHeadVisible = mHeader.getVisibility() == View.VISIBLE;
        }
    }

    private boolean isCreating = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isCreating) {
//            showLoading();
            startDataThread();
//            hideLoading();
        }else {
            onNormalResume();
        }
        isCreating = false;
        Log.e("BaseActivity", "onResume");
    }

    protected void onNormalResume() {

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.e("BaseActivity", "onAttachedToWindow");
        haveAttachedToWindow = true;
        try {
            notifyAll();
        } catch (Exception e) {

        }
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        if (mResumeThread != null && mResumeThread.isAlive()) {
            mResumeThread.interrupt();
            mResumeThread = null;
        }
        super.onDestroy();
    }

    protected void showLoading() {
        showProgressDialog(getResources().getString(R.string.loading),true);
    }

    protected void hideLoading() {
        hideProgressDialog();
    }

    private void startDataThread() {
        showLoading();
        mResumeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                dealWithData();
                finishDataThread();
            }
        });
        mResumeThread.start();
    }

    private void finishDataThread() {
        if (mResumeThread == null) {
            return;
        }
        synchronized (mResumeThread) {
            while (!haveAttachedToWindow) {
                try {
                    wait();
                } catch (Exception e) {

                }
            }
        }
        if (handler != null) {
            handler.sendEmptyMessage(HANDLER_FINISH_DATA);
        }
        hideLoading();
    }

    public void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    public void showStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    protected abstract int getContentId();

    protected abstract void initView();

    /**
     * run on new thread
     */
    protected abstract void dealWithData();

    protected void finishData() {

        Log.e("BaseActivity", "data finish");
    }

    protected void onBackClick(View view) {
        finish();
    }

    protected void onMenuClick(View view) {

    }

    protected void setActTitle(String title) {
        if (mHeader != null) {
            mHeader.getmTitleTextView().setText(title);
        }
    }


    MyProgressDialog progressDialog;
    private long showDialogTime = 0;

    public void showProgressDialog(String title,boolean cancelAble) {
        if (progressDialog == null) {
            progressDialog = new MyProgressDialog(this, title, cancelAble);
        }else {
            progressDialog.setContentText(title);
            progressDialog.setCancelable(cancelAble);
            progressDialog.setCanceledOnTouchOutside(cancelAble);
        }
        if (!this.isFinishing()) {
            showDialogTime = System.currentTimeMillis();
            progressDialog.show();
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case HANDLER_HIDE_DIALOG:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    break;
                case HANDLER_FINISH_DATA:
                    finishData();
                    break;
                default:break;
            }
            return false;
        }
    });

    public void hideProgressDialog() {
        if (System.currentTimeMillis()-showDialogTime>500) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }, 500-System.currentTimeMillis() + showDialogTime);
        }
    }


    public void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    public void removeDisposable(Disposable d){
        if (mDisposable != null && d != null){
            mDisposable.remove(d);
        }
    }

    public void toggleHeaderVisible() {
        if (isHeadVisible) {
            hideHeader();
        }else {
            showHeader();
        }
    }

    public void hideHeader() {
        isHeadVisible = false;
        if (mHeader != null) {
            mHeader.setVisibility(View.GONE);
            AnimUtils.TopOut(mHeader);
        }
    }

    public void showHeader() {
        isHeadVisible = true;
        if (mHeader != null) {
            mHeader.setVisibility(View.VISIBLE);
            AnimUtils.TopIn(mHeader);
        }
    }

}
