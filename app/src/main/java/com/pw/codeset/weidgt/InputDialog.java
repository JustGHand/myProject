package com.pw.codeset.weidgt;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.codeset.utils.LogToastUtils;
import com.xd.baseutils.utils.NStringUtils;

/**
 * Created by developer on 2019/7/17.
 */

public class InputDialog extends Dialog {

    private Activity mActivity;
    private DialogListener dialogListener;
    private String confirmText;
    private String cancelText;
    private String editHint;
    private String titleText;

    public InputDialog(@NonNull Activity activity, DialogListener dialogListener, String hint, String title) {
        super(activity, R.style.transparentFrameWindowStyle);
        mActivity = activity;
        this.dialogListener = dialogListener;
        editHint = hint;
        titleText = title;
    }

    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }

    public void setCancelText(String cancelText) {
        this.cancelText = cancelText;
    }

    public interface DialogListener{
        void cancel();

        void confirm(String content);

        void editChange(String content);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.view_dialog_input,
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
        TextView confirm = findViewById(R.id.inputdialog_confirm);
        TextView cancel = findViewById(R.id.inputdialog_cancel);
        TextView title = findViewById(R.id.inputdialog_title);
        EditText editText = findViewById(R.id.inputdialog_edit);
        confirm.setText(NStringUtils.isBlank(confirmText) ? "确认" : confirmText);
        cancel.setText(NStringUtils.isBlank(cancelText) ? "取消" : cancelText);
        title.setText(NStringUtils.isBlank(titleText) ? "输入" : titleText);
        editText.setHint(editHint);
        IconImageView clearBtn = findViewById(R.id.inputdialog_clear);
        clearBtn.setEnabled(false);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                clearBtn.setEnabled(false);
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    if (NStringUtils.isBlank(editText.getText().toString())) {
                        LogToastUtils.show("请输入正确内容");
                        return;
                    }
                    dialogListener.confirm(editText.getText().toString());
                    dismiss();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.cancel();
                    dismiss();
                }
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dialogListener != null) {
                    dialogListener.editChange(s.toString());
                }
                clearBtn.setEnabled(true);
            }
        });
    }

    public void setEditText(String content) {
        EditText editText = findViewById(R.id.inputdialog_edit);
        if (editText != null) {
            editText.setText(content);
        }
    }
}
