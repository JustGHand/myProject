package com.xd.base.base;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.xd.base.R;
import com.xd.base.event.NetworkChangeEvent;
import com.xd.base.receiver.NetworkChangeReceiver;
import com.xd.base.utils.statusbar.StatusBarUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by PC on 2016/9/8.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final int INVALID_VAL = -1;

    protected CompositeDisposable mDisposable;
    //ButterKnife
    private Toolbar mToolbar;

    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    /****************************abstract area*************************************/

    @LayoutRes
    protected abstract int getContentId();

    /************************init area************************************/
    public void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }

    public void removeDisposable(Disposable d){
        if (mDisposable != null && d != null){
            mDisposable.remove(d);
        }
    }

    /**
     * 配置Toolbar
     *
     * @param toolbar
     */
    protected void setUpToolbar(Toolbar toolbar) {
    }

    protected void initData(Bundle savedInstanceState) {
    }

    /**
     * 初始化零件
     */
    protected void initWidget() {

    }

    /**
     * 初始化点击事件
     */
    protected void initClick() {
    }

    /**
     * 逻辑使用区
     */
    protected void processLogic() {
    }

    /*************************lifecycle area*****************************************************/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            savedInstanceState.putParcelable("android:support:fragments", null);
        }
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            StatusBarUtils.myStatusBar(this, getResources().getColor(R.color.white), true);
        }
        setContentView(getContentId());

//        App.getInstance().addActivity(this);

        initData(savedInstanceState);
//        initToolbar();
        initWidget();
        initClick();
        processLogic();

        //接受在线参数获取成功的事件
        Disposable disposable = RxBus.getInstance()
                .toObservable(NetworkChangeEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        (event) ->  {
                            onNetworkChangeEvent(event);
                        },throwable -> {}
                );
        addDisposable(disposable);

        intentFilter=new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver,intentFilter);


    }

    protected void onNetworkChangeEvent(NetworkChangeEvent event) {

    }

    private void initToolbar() {
        //更严谨是通过反射判断是否存在Toolbar
        mToolbar = findViewById(R.id.toolbar);
        if (mToolbar != null) {
            supportActionBar(mToolbar);
            setUpToolbar(mToolbar);
        }
    }

    /**
     * 点击title退出
     */
    protected void onActionBarFinish() {
        finish();
    }


    @Override
    protected void onDestroy() {
//        App.getInstance().removeActivity(this);
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
        unregisterReceiver(networkChangeReceiver);
    }

    /**************************used method area*******************************************/

    protected void startActivity(Class<? extends AppCompatActivity> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    protected ActionBar supportActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(
                (v) -> onActionBarFinish()
        );
        return actionBar;
    }

    protected void setStatusBarColor(int statusColor) {
//        StatusBarCompat.compat(this, ContextCompat.getColor(this, statusColor));
    }

    public void setStatusBarTranslucent() {
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
        localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);}



    @Override
    protected void attachBaseContext(Context newBase) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            Resources resources = newBase.getResources();
            Configuration newConfig = resources.getConfiguration();
            newConfig.fontScale = 1f;
            Context context = newBase.createConfigurationContext(newConfig);
            super.attachBaseContext(context);
            return;
        }
        super.attachBaseContext(newBase);
    }

}
