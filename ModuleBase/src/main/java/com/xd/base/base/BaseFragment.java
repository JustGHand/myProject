package com.pw.base.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by newbiechen on 17-3-31.
 */

public abstract class BaseFragment extends Fragment {

    protected CompositeDisposable mDisposable;

    private View root = null;

    private String extraData;

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public String getExtraData() {
        return extraData;
    }

    @LayoutRes
    protected abstract int getContentId();

    /*******************************init area*********************************/
    protected void addDisposable(Disposable d) {
        if (mDisposable == null) {
            mDisposable = new CompositeDisposable();
        }
        mDisposable.add(d);
    }


    protected void initData(Bundle savedInstanceState) {
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

    /**
     * 初始化零件
     */
    protected void initWidget(Bundle savedInstanceState) {
    }

    /******************************lifecycle area*****************************************/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getContentId();
        if (resId != 0)
            root = inflater.inflate(resId, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData(savedInstanceState);
        initWidget(savedInstanceState);
        initClick();
        processLogic();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    /**************************公共类*******************************************/
    public String getName() {
        return getClass().getName();
    }

    protected <VT> VT getViewById(int id) {
        if (root == null) {
            return null;
        }
        return (VT) root.findViewById(id);
    }
}


