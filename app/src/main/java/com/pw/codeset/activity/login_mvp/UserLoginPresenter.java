package com.pw.codeset.activity.login_mvp;

import android.app.Activity;

import com.pw.codeset.databean.UserInfoBean;

public class UserLoginPresenter implements UserLoginModel.LoginListener{

    UserLoginView mView;
    UserLogin mModel;
    Activity mActivity;

    public void detach() {
        mModel.destroy();
        mModel = null;
        mView = null;
    }

    public UserLoginPresenter(UserLoginView mView,Activity activity) {
        mModel = new UserLogin(this);
        mActivity = activity;
        this.mView = mView;
    }

    public void login() {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.showLoading();
            }
        });
        String account = mView.getUserAccount();
        String pw = mView.getUserPassword();
        mModel.login(account,pw);
    }

    @Override
    public void onSuccess(UserInfoBean userInfo) {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.hideLoading();
                mView.loginSuccess();
            }
        });
    }

    @Override
    public void onFailed(String msg) {
        if (mActivity == null) {
            return;
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mView.hideLoading();
                mView.showError(msg);
            }
        });
    }
}
