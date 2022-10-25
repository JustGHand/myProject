package com.pw.read;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private TxtPage mCurPageData;
    boolean isMove;
    List<int[]> mMoveSteps;
    int mStartX;
    int mStartY;
    int mMoveY;
    int mMoveX;
    Paint mPaint;

    public void setContent(TxtPage page) {
        mCurPageData = page;
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        postInvalidate();
    }

    public TxtPage getCurPage() {
        return mCurPageData;
    }

    public void startTouch(int x, int y) {

        int[] loc = new int[2];
        loc[0] = x;
        loc[1] = y;
        if (mMoveSteps == null) {
            mMoveSteps = new ArrayList<>();
        }
        mMoveSteps.add(loc);
    }

    public void drawMove(int startX, int startY, int moveX, int moveY) {
        isMove = true;
        int[] loc = new int[2];
        loc[0] = moveX;
        loc[1] = moveY;
        mMoveSteps.add(loc);
        mStartX = startX;
        mStartY = startY;
        mMoveX = moveX;
        mMoveY = moveY;
        postInvalidate();
    }

    public void touchEnd(int startX, int startY, int endX, int endY) {
        isMove = false;
        mMoveSteps = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        PageDrawManager.getInstance().drawPage(canvas,mCurPageData,false);
        if (isMove && mMoveSteps != null && mMoveSteps.size() > 0) {
            int[] startLoc = mMoveSteps.get(0);
            for (int i = 1; i < mMoveSteps.size(); i++) {
                int[] stepLoc = mMoveSteps.get(i);
                canvas.drawLine(startLoc[0], startLoc[1], stepLoc[0], stepLoc[1], mPaint);
                startLoc = stepLoc;
            }
        }
        super.onDraw(canvas);
    }
}
