package com.pw.codeset.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.pw.codeset.R;

public class CommenUseViewUtils {

    public interface onLabelCheckListener{
        void onCheckedChange(String label, boolean isChecked);
    }

    public static CheckBox getNoteLabelView(Context context, String label,boolean defaultStatus , onLabelCheckListener checkedChangeListener) {
        return getNoteLabelView(context, label, defaultStatus, true, checkedChangeListener);
    }

    public static CheckBox getNoteLabelView(Context context, String label, boolean defaultStatus, boolean showBack, onLabelCheckListener checkedChangeListener) {

        int defaultMargin = (int) ResourceUtils.getDefaultMargin();

        CheckBox tagBtn = (CheckBox) LayoutInflater.from(context).inflate(R.layout.view_border_text, null, false);
        tagBtn.setText("#" + label);
        tagBtn.setTag(label);
        if (!showBack) {
            tagBtn.setBackground(null);
        }
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, defaultMargin);
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
        return tagBtn;
    }
}
