package com.pw.read;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pw.read.bean.TxtPage;
import com.pw.read.interfaces.PageAnimCallback;
import com.pw.read.manager.PageDrawManager;
import com.pw.read.transformer.SimulationPageAnim;

import java.util.ArrayList;
import java.util.List;

public class PageView extends View {
    public PageView(@NonNull Context context) {
        super(context);
    }

    public PageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private TxtPage mCurPageData;
    boolean isMove;
    List<int[]> mMoveSteps;
    int mStartX;
    int mStartY;
    int mMoveY;
    int mMoveX;
    float mMovePosition;
    Boolean isNext;
    Paint mPaint;
    PageAnimCallback mAnimCallback;

    SimulationPageAnim mAnim;

    public void setAnimCallBack(PageAnimCallback callBack) {
        mAnimCallback = callBack;
    }

    public void setContent(TxtPage page) {
        mCurPageData = page;
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(5);
        mAnim = new SimulationPageAnim();
        mAnim.init(getWidth(), getHeight(), mCurPageData);
        postInvalidate();
    }

    public TxtPage getCurPage() {
        return mCurPageData;
    }

    public void startTouch(int x, int y) {

//        int[] loc = new int[2];
//        loc[0] = x;
//        loc[1] = y;
//        if (mMoveSteps == null) {
//            mMoveSteps = new ArrayList<>();
//        }
//        mMoveSteps.add(loc);
        mStartX = x;
        mStartY = y;
        isNext = null;
        mAnim.setStartPoint(x, y);
    }

    public void drawMove(int startX, int startY, int moveX, int moveY) {
        isMove = true;
        if (mStartX==-1) {
            startTouch(moveX,moveY);
        }else {
            mMoveX = moveX;
            mMoveY = moveY;
            if (isNext == null) {
                isNext = mStartX > mMoveX;
            }
            mAnim.setTouchPoint(moveX, mMoveY, isNext);
        }
        postInvalidate();
    }

    public void touchEnd(int startX, int startY, int endX, int endY) {
        isMove = false;
        isNext = null;
        mStartX = -1;
        mStartY = -1;
        mMoveSteps = new ArrayList<>();
    }

    public void startAnim(boolean animIn) {
        if (mAnimCallback != null) {
            mAnimCallback.onAnimStart();
        }
        if (mAnimCallback != null) {
            mAnimCallback.onAnimFinish();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurPageData!=null) {
            if (isMove){
                mAnim.drawMove(canvas, mMoveX, mMoveY);
            }else {
                PageDrawManager.getInstance().drawPage(canvas, mCurPageData, false);
            }
            if (isMove && mMoveSteps != null && mMoveSteps.size() > 0) {
                int[] startLoc = mMoveSteps.get(0);
                for (int i = 1; i < mMoveSteps.size(); i++) {
                    int[] stepLoc = mMoveSteps.get(i);
                    canvas.drawLine(startLoc[0], startLoc[1], stepLoc[0], stepLoc[1], mPaint);
                    startLoc = stepLoc;
                }
            }
        }
        super.onDraw(canvas);
    }

    public void startTransform(float position, int touchX, int touchY) {
        Log.d("pageview", mCurPageData.getStartCharPos() + "---" + position);
//        if (position < 1 && position > -1) {
//            if (mMovePosition ==0) {
//                mAnim.setStartPoint(touchX,touchY);
//            }else {
//                mAnim.setTouchPoint(touchX, touchY, position < 0);
//            }
//        }
        mMovePosition = position;
        mMoveX = touchX;
        mMoveY = touchY;
    }
}
