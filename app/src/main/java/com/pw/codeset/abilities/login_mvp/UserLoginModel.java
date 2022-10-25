package com.pw.codeset.abilities.login_mvp;

import com.pw.codeset.databean.UserInfoBean;

public class UserLoginModel {

    public interface LoginModel{
        /**
         * 登录
         *
         * @param account  账号
         * @param password 密码
         */
        void login(String account, String password);
    }

    public interface LoginListener {
        /**
         * 登录成功
         * @param userInfo 用户信息
         */
        void onSuccess(UserInfoBean userInfo);

        /**
         * 登录失败
         * @param msg 失败消息
         */
        void onFailed(String msg);
    }
}
