package com.pw.codeset.weidgt;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;
import com.pw.baseutils.utils.NStringUtils;


/**
 * Created by developer on 2019/7/17.
 */

public class MyProgressDialog extends Dialog {

    private Activity mActivity;
    private String contentText;
    private boolean isCancelAble = false;
    private boolean showBlackBack = true;

    public MyProgressDialog(@NonNull Activity activity, String content, boolean cancelAble) {
        super(activity,R.style.CommonDialog);
        mActivity = activity;
        this.contentText = content;
        this.isCancelAble = cancelAble;
    }


    public MyProgressDialog(@NonNull Activity activity, int theme, String content, boolean cancelAble, boolean showBlackBack) {
        super(activity,theme);
        mActivity = activity;
        this.showBlackBack = showBlackBack;
        this.contentText = content;
        this.isCancelAble = cancelAble;
    }

    public void setBackGround(Drawable backGround) {
        if (backGround!=null) {
            ConstraintLayout container = findViewById(R.id.myprogress_container);
            if (container!=null) {
                container.setBackground(backGround);
            }
        }
    }

    public void setContentText(String content) {
        this.contentText = content;
        TextView contentView = findViewById(R.id.myprogress_content);
        if (contentView != null) {
            if (NStringUtils.isBlank(contentText)) {
                contentView.setVisibility(View.GONE);
            } else {
                contentView.setVisibility(View.VISIBLE);
                contentView.setText(contentText);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_myprogress_dialog,
                null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        Window window = getWindow();
        // 设置显示动画
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.gravity = Gravity.CENTER;
//        wl.x = 0;
//        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight()/3;
//        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.MATCH_PARENT;

        //设置window背景，默认的背景会有Padding值，不能全屏。当然不一定要是透明，你可以设置其他背景，替换默认的背景即可。
//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // 设置显示位置
        onWindowAttributesChanged(wl);

        initViews();
    }

    private ProgressBar mLoadingProgressBar;


    private void initViews() {

        mLoadingProgressBar = findViewById(R.id.myprogress_loadingview);

        mLoadingProgressBar.setVisibility(View.VISIBLE);


        TextView content = findViewById(R.id.myprogress_content);
        if (NStringUtils.isBlank(contentText)) {
            content.setVisibility(View.GONE);
        }else {
            content.setVisibility(View.VISIBLE);
            content.setText(contentText);
        }

        setCancelable(isCancelAble);
        setCanceledOnTouchOutside(isCancelAble);


        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//                lp.alpha = 1;
//                mActivity.getWindow().setAttributes(lp);
            }
        });
    }


    @Override
    public void show() {
        super.show();

//        if (showBlackBack) {
//            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
//            lp.alpha = 0.9f;
//            mActivity.getWindow().setAttributes(lp);
//        }
    }
}
