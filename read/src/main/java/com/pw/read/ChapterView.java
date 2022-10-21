package com.pw.read;

import android.content.Context;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class ChapterView extends FrameLayout {
    public ChapterView(@NonNull Context context) {
        super(context);
        init();
    }

    List<TextView> mPageViews;

    private void init() {

    }

    public void setContent(String content) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setLineSpacing(10,1);
        textView.setText(content);
        addView(textView);
    }

}
