package com.pw.codeset.abilities.login_mvp;

import android.view.View;
import android.widget.EditText;

import com.pw.codeset.R;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseActivity;

public class UserLoginActivity extends BaseActivity implements UserLoginView{

    private UserLoginPresenter mPresenter;

    private EditText mNameInput;
    private EditText mPwInput;


    @Override
    protected void dealWithData() {

    }

    @Override
    protected int getContentId() {
        return R.layout.activity_login_mvp;
    }

    @Override
    protected void initView() {

        mNameInput = findViewById(R.id.login_name_input);
        mPwInput = findViewById(R.id.login_pw_input);


        mPresenter = new UserLoginPresenter(this,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detach();
        mPresenter = null;
    }

    public void loginMvp(View view) {
        mPresenter.login();
    }

    @Override
    public String getUserAccount() {
        String userName = null;
        if (mNameInput!=null) {
            userName = mNameInput.getText().toString();
        }
        return userName;
    }

    @Override
    public String getUserPassword() {
        String pw = null;
        if (mPwInput != null) {
            pw = mPwInput.getText().toString();
        }
        return pw;
    }

    @Override
    public void showLoading() {
        showProgressDialog("登录中...",false);
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }

    @Override
    public void loginSuccess() {
        MyApp.getInstance().showToast("登陆成功!");
        finish();
    }

    @Override
    public void showError(String msg) {
        MyApp.getInstance().showToast("登录失败:" + msg);
    }
}
