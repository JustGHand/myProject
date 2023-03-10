package com.pw.other.annotation.inject;

import android.util.Log;

import com.pw.annotation.inject.InjectView;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.weidgt.ActivityHeaderView;

public class InjectViewTestActivity  extends BaseActivity {

    public static final String TAG = "AnnotationTest";

    @InjectView(R.id.activity_header)
    ActivityHeaderView mHeader;

    @Override
    protected int getContentId() {
        return R.layout.activity_inject_view_test;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void dealWithData() {
        Log.e(TAG, "headerView title : " + mHeader.getmTitleTextView());
    }
}