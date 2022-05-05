package com.pw.codeset.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;

import com.pw.codeset.R;
import com.pw.codeset.weidgt.ActivityHeaderView;
import com.pw.codeset.weidgt.MyProgressDialog;

public abstract class BaseActivity extends Activity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        initHeader();
        initView();
        isCreating = true;
    }

    private void initHeader() {
        ActivityHeaderView mHeader = findViewById(R.id.activity_header);
        if (mHeader != null) {
            mHeader.setOnBackClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackClick();
                }
            });
            mHeader.setOnMenuClick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onMenuClick();
                }
            });
        }
    }

    private boolean isCreating = false;

    @Override
    protected void onResume() {
        super.onResume();
        if (isCreating) {
//            showLoading();
            dealWithData();
//            hideLoading();
        }
        isCreating = false;
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        super.onDestroy();
    }

    protected void showLoading() {
        showProgressDialog(getResources().getString(R.string.loading),false);
    }

    protected void hideLoading() {
        hideProgressDialog();
    }


    protected abstract int getContentId();

    protected abstract void initView();

    protected abstract void dealWithData();

    protected void onBackClick() {
        finish();
    }

    protected void onMenuClick() {

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

    Handler handler = new Handler();

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

}
