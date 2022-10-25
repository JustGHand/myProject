package com.pw.codeset.abilities.login_mvp;

public interface UserLoginView {

    String getUserAccount();

    String getUserPassword();

    void showLoading();

    void hideLoading();

    void loginSuccess();

    void showError(String msg);

}
