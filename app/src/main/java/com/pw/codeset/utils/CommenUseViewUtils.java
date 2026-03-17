package com.pw.codeset.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.pw.codeset.R;

public class CommenUseViewUtils {

    public interface onLabelCheckListener{
        void onCheckedChange(String label, boolean isChecked);

        Boolean onLongClick(String label,View view);
    }

    public static CheckBox getNoteLabelView(Context context, String label,boolean defaultStatus , onLabelCheckListener checkedChangeListener) {
        return getNoteLabelView(context, label, defaultStatus, true, checkedChangeListener);
    }

    public static CheckBox getNoteLabelView(Context context, String label, boolean defaultStatus, boolean showBack, onLabelCheckListener checkedChangeListener) {

        int smallBtnHeight = (int) ResourceUtils.getSmallBtnHeight();

        CheckBox tagBtn = (CheckBox) LayoutInflater.from(context).inflate(R.layout.view_border_text, null, false);
        tagBtn.setText("#" + label);
        tagBtn.setTag(label);
        if (!showBack) {
            tagBtn.setBackground(null);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, smallBtnHeight);
        tagBtn.setLayoutParams(layoutParams);
        tagBtn.setChecked(defaultStatus);
        tagBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkedChangeListener != null) {
                    checkedChangeListener.onCheckedChange(label,isChecked);
                }
            }
        });
        tagBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return checkedChangeListener.onLongClick(label,tagBtn);
            }
        });
        return tagBtn;
    }
}
