package com.pw.codeset.abilities.login_mvp;

import com.pw.codeset.databean.UserInfoBean;
import com.pw.baseutils.utils.NStringUtils;

public class UserLogin implements UserLoginModel.LoginModel {

    UserLoginPresenter mPresenter;

    public UserLogin(UserLoginPresenter presenter) {
        mPresenter = presenter;
    }

    public void destroy() {
        mPresenter = null;
    }

    @Override
    public void login(String account, String password) {
        if (mPresenter == null) {
            return;
        }
        if (NStringUtils.isBlank(account)) {
            mPresenter.onFailed("请输入正确的用户名");
            return;
        }
        if (NStringUtils.isBlank(password)) {
            mPresenter.onFailed("请输入密码");
            return;
        }
        fakeLogin(account, password);
    }

    private void fakeLogin(String account, String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mPresenter == null) {
                    return;
                }

                UserInfoBean userInfoBean = null;
                if (NStringUtils.isNotBlank(account) && NStringUtils.isNotBlank(password)) {
                    if (account.equals("123") && password.equals("123")) {
                        userInfoBean = new UserInfoBean();
                    }
                }

                if (userInfoBean != null) {
                    mPresenter.onSuccess(userInfoBean);
                }else {
                    mPresenter.onFailed("账号密码错误");
                }
            }
        }).start();
    }
}
