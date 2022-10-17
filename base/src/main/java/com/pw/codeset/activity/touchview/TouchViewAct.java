package com.pw.codeset.activity.touchview;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.read.TouchAnimView;

import java.util.logging.LogRecord;

public class TouchViewAct extends BaseActivity {
    @Override
    protected void dealWithData() {

    }

    @Override
    protected int getContentId() {
        return R.layout.act_touch_view;
    }

    TouchAnimView mTouchView;

    @Override
    protected void initView() {
        mTouchView = findViewById(R.id.touch_view);
        mTouchView.setBackground(getResources().getDrawable(R.mipmap.leather));
    }

    static Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });

}
