package com.xd.base.base;

/**
 * Created by newbiechen on 17-4-25.
 */

public abstract class BaseMVPActivity<T extends BaseContract.BasePresenter> extends BaseActivity implements BaseContract.BaseView{

    protected T mPresenter;

    protected abstract T bindPresenter();

    @Override
    protected void processLogic() {
        attachView(bindPresenter());
    }

    private void attachView(T presenter){
        mPresenter = presenter;
        mPresenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void noUser() {
//        ToastUtils.show("账号登陆已失效，请重新登陆");
//        UserManager.getInstance().loginOut();
    }

}