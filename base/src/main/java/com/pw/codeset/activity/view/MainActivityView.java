package com.pw.codeset.activity.view;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;

public class MainActivityView extends BaseActivity {

    private Button toBazierButton;

    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        toBazierButton = findViewById(R.id.main_to_bazier);
        toBazierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityView.this,BezierCurveActivity.class));
            }
        });
    }
}