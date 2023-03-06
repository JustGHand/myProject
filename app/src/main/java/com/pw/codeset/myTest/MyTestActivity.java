package com.pw.codeset.myTest;

import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.utils.ResourceUtils;
import com.pw.read.transformer.PageTurnContainer;

import java.util.ArrayList;
import java.util.List;

public class MyTestActivity extends BaseActivity
{
    @Override
    protected int getContentId() {
        return R.layout.act_my_test;
    }

    PageTurnContainer mPageContainer;

    @Override
    protected void initView() {
        mPageContainer = findViewById(R.id.test_pageturn);
    }

    @Override
    protected void dealWithData() {

    }

    @Override
    protected void finishData() {
        super.finishData();
        List<View> views = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TextView view = new TextView(this);
            view.setText(String.valueOf(i));
            view.setGravity(Gravity.CENTER);
            if (i==0) {
                view.setBackgroundColor(ResourceUtils.getResColor(R.color.tag_back_color1));
            } else if (i == 1) {
                view.setBackgroundColor(ResourceUtils.getResColor(R.color.tag_back_color2));
            }else {
                view.setBackgroundColor(ResourceUtils.getResColor(R.color.tag_back_color3));
            }
            views.add(view);
        }
        mPageContainer.addPages(views);
    }
}
