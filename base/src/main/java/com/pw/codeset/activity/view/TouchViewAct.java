package com.pw.codeset.activity.view;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.read.TouchAnimView;

public class TouchViewAct extends BaseActivity {
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


}
