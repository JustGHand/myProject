package com.pw.codeset.weidgt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pw.codeset.R;
import com.xd.baseutils.utils.NStringUtils;


public class MyDialog extends Dialog {

    public MyDialog(@NonNull Context context) {
        super(context);
    }

    public MyDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected MyDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public MyDialog(@NonNull Activity activity,String title,  String content, String confirmBtnText, String cancelBtnText, DialogListener listener, boolean cancelAbleOutSide) {
        super(activity, R.style.transparentFrameWindowStyle);
        mActivity = activity;
        mTitle = title;
        mContent = content;
        mConfirmText = confirmBtnText;
        mCancelText = cancelBtnText;
        mListener = listener;
        mCancelAbleOutSide = cancelAbleOutSide;
    }


    public MyDialog(@NonNull Activity activity, String title, String content, String singlebtn, DialogListener dialogListener){
        super(activity, R.style.transparentFrameWindowStyle);
        mActivity = activity;
        mTitle = title;
        mContent= content;
        mListener = dialogListener;
        mConfirmText = singlebtn;
        mCancelText = "";
        isSinglBtn = true;

    }


    private boolean isSinglBtn = false;


    private Activity mActivity;
    private boolean mCancelAbleOutSide = true;
    private String mContent;
    private String mTitle;
    private String mConfirmText;
    private String mCancelText;
    private DialogListener mListener;
    private Integer mCancelColor;
    private Integer mConfirmColor;
    TextView contentView;
    TextView titleView;
    TextView confirmView;
    TextView cancelView;
    View cutLineVertical;
    View cutLineHorizon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_commen,
                null);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.gravity = Gravity.CENTER;
//        wl.x = 0;
//        wl.y = mActivity.getWindowManager().getDefaultDisplay().getHeight()/3;
//        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        onWindowAttributesChanged(wl);

        initViews();
    }

    private void initViews() {
        titleView = findViewById(R.id.centerdialog_title);
        contentView = findViewById(R.id.centerdialog_content);
        confirmView = findViewById(R.id.centerdialog_confirm);
        cancelView = findViewById(R.id.centerdialog_cancel);
        cutLineHorizon = findViewById(R.id.centerdialog_deliverline2);
        cutLineVertical = findViewById(R.id.centerdialog_deliverline1);
        if (NStringUtils.isNotBlank(mContent)) {
            contentView.setText(mContent);
        }
        if (NStringUtils.isNotBlank(mTitle)) {
            titleView.setText(mTitle);
            titleView.setVisibility(View.VISIBLE);
        }else {
            titleView.setVisibility(View.GONE);
        }
        if (NStringUtils.isNotBlank(mConfirmText)) {
            confirmView.setText(mConfirmText);
        }
        if (mConfirmColor != null) {
            confirmView.setTextColor(mConfirmColor);
        }
        if (NStringUtils.isNotBlank(mCancelText)) {
            cancelView.setText(mCancelText);
        }
        if (mCancelColor != null) {
            cancelView.setTextColor(mCancelColor);
        }
        if (isSinglBtn) {
            cutLineHorizon.setVisibility(View.GONE);
            cancelView.setVisibility(View.GONE);
        }else {
            cutLineHorizon.setVisibility(View.VISIBLE);
            cancelView.setVisibility(View.VISIBLE);
        }
        confirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    boolean needDismiss = mListener.onConfirm();
                    if (needDismiss) {
                        dismiss();
                    }
                }
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    boolean needDismiss = mListener.onCancel();
                    if (needDismiss) {
                        dismiss();
                    }
                }
            }
        });
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
                lp.alpha = 1;
                mActivity.getWindow().setAttributes(lp);
                if (mListener != null) {
                    mListener.onDismiss();
                }
            }
        });
        setCanceledOnTouchOutside(mCancelAbleOutSide);
    }

    public void setCancelBtnTextColor(int color) {
        cancelView.setTextColor(color);
    }

    public void setConfirBtnTextColor(int color) {
        confirmView.setTextColor(color);
    }

    public void setContentTextColor(int color) {
        contentView.setTextColor(color);
    }

    @Override
    public void show() {
        super.show();
        if (mListener != null) {
            mListener.onShow();
        }
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 0.7f;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public void onBackPressed() {
        if (mCancelAbleOutSide) {
            super.onBackPressed();
        }
    }

    public interface DialogListener{
        boolean onConfirm();

        boolean onCancel();

        void onDismiss();

        void onShow();
    }
}
