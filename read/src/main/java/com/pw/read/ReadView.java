package com.pw.read;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

public class ReadView extends FrameLayout {

    public ReadView(@NonNull Context context) {
        super(context);
    }

    PageMode mCurPageMode;
    int mCurFontSize;



}
