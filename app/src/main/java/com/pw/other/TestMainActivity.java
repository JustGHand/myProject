package com.pw.other;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pw.annotation.inject.InjectView;
import com.pw.annotation.inject.InjectViewUtil;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.other.annotation.inject.InjectUtils;
import com.pw.other.annotation.inject.InjectViewTestActivity;
import com.pw.other.bean.TestFuncBean;

import java.util.ArrayList;
import java.util.List;

public class TestMainActivity extends BaseActivity {

    @InjectView(R.id.test_func_list)
    LinearLayout mFunBtnContainer;

    List<TestFuncBean> mFuncList;


    @Override
    protected int getContentId() {
        return R.layout.act_test_main;
    }

    @Override
    protected void initView() {
        mFuncList = new ArrayList<>();
        mFuncList.add(new TestFuncBean("InjectView", InjectViewTestActivity.class));


        dealWithFuncs();
    }

    @Override
    protected void dealWithData() {
    }

    private void dealWithFuncs() {
        if (mFuncList == null || mFuncList.isEmpty()) {
            return;
        }
        for (TestFuncBean funcBean : mFuncList) {
            if (funcBean != null) {
                Button button = new Button(this);
                button.setText(funcBean.getTitle());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(TestMainActivity.this, funcBean.getTarAct()));
                    }
                });
                mFunBtnContainer.addView(button);
            }
        }
    }

    public static void main(String[] args) {
        System.out.print("test");
    }

}
