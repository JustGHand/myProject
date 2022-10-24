package com.pw.read;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.read.bean.ChaptersBean;
import com.pw.read.bean.TxtPage;
import com.pw.read.manager.PageDrawManager;
import com.pw.read.manager.ReadTouchInterface;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class PageView extends View {
    public PageView(@NonNull Context context) {
        super(context);
    }

    private TextView mContentTextView;
    private ReadTouchInterface mTouchInterface;
    private TxtPage mCurPageData;

    public void setTouchInterface(ReadTouchInterface touchInterface) {
        mTouchInterface = touchInterface;
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mTouchInterface != null) {
//                    mTouchInterface.onClick();
//                }
//            }
//        });
    }

    public void setContent(TxtPage page) {
        mCurPageData = page;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        PageDrawManager.getInstance().drawPage(canvas,mCurPageData,false);
        super.onDraw(canvas);
    }
}
