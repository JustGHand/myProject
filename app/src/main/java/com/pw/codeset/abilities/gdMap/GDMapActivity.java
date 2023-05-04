package com.pw.codeset.abilities.gdMap;

import android.view.View;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.gdmap.GDMapUtils;

public class GDMapActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_gd_map;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void dealWithData() {

    }

    public void start(View view) {
        GDMapUtils.init(this);
        GDMapUtils.start(this);
    }
}
