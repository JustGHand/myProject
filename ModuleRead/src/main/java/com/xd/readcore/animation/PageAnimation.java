package com.pw.readcore.animation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

/**
 * Created by newbiechen on 17-7-24.
 * 翻页动画抽象类
 */

public abstract class PageAnimation {
    //正在使用的View
    protected View mView;
    //滑动装置
    protected Scroller mScroller;
    //监听器
    protected OnPageChangeListener mListener;
    //移动方向
    protected Direction mDirection = Direction.NONE;

    protected boolean isRunning = false;

    protected boolean isAutoReadRunning = false;

    //屏幕的尺寸
    protected int mScreenWidth;
    protected int mScreenHeight;
    //屏幕的间距
    protected int mMarginWidth;
    protected int mMarginHeight;
    //视图的尺寸
    protected int mViewWidth;
    protected int mViewHeight;
    //起始点
    protected float mStartX;
    protected float mStartY;
    //触碰点
    protected float mTouchX;
    protected float mTouchY;
    //上一个触碰点
    protected float mLastX;
    protected float mLastY;

    //记录是否开始动画
    protected boolean mBeginMoveAnimation;

    public PageAnimation(int w, int h,View view,OnPageChangeListener listener){
        this(w, h, 0, 0, view,listener);
    }

    public PageAnimation(int w, int h, int marginWidth, int marginHeight, View view,OnPageChangeListener listener){
        mScreenWidth = w;
        mScreenHeight = h;

        mMarginWidth = marginWidth;
        mMarginHeight = marginHeight;

        mViewWidth = mScreenWidth - mMarginWidth * 2;
        mViewHeight = mScreenHeight - mMarginHeight * 2;

        mView = view;
        mListener = listener;

        mBeginMoveAnimation = false;

        mScroller = new Scroller(mView.getContext(), new LinearInterpolator());
    }

    public void setStartPoint(float x,float y){
        mStartX = x;
        mStartY = y;

        mLastX = mStartX;
        mLastY = mStartY;
    }

    public void setTouchPoint(float x,float y){
        mLastX = mTouchX;
        mLastY = mTouchY;

        mTouchX = x;
        mTouchY = y;
    }

    public boolean isRunning(){
        return isRunning;
    }

    public boolean isAutoReadRunning() {
        return isAutoReadRunning;
    }

    /**
     * 开启翻页动画
     */
    public void startAnim(){
        if (isRunning){
            return;
        }
        isRunning = true;
    }

    public void setDirection(Direction direction){
        mDirection = direction;
    }

    public Direction getDirection(){
        return mDirection;
    }

    public void clear(){
        mView = null;
    }
    /**
     * 点击事件的处理
     * @param event
     */
    public abstract boolean onTouchEvent(MotionEvent event);

    /**
     * 绘制图形
     * @param canvas
     */
    public abstract void draw(Canvas canvas);

    /**
     * 滚动动画
     * 必须放在computeScroll()方法中执行
     */
    public abstract void scrollAnim();

    /**
     * 取消动画
     */
    public abstract void abortAnim();

    /**
     * 获取背景板
     * @return
     */
    public abstract Bitmap getBgBitmap();

    public abstract void setBgBitmap(Bitmap bitmap);

    /**
     * 获取内容显示版面
     */
    public abstract Bitmap getNextBitmap();

    public abstract Bitmap getmCurBitmap() ;
    public enum Direction {
        NONE(true),NEXT(true), PRE(true), UP(false), DOWN(false);

        public final boolean isHorizontal;

        Direction(boolean isHorizontal) {
            this.isHorizontal = isHorizontal;
        }
    }

    public interface OnPageChangeListener {
        boolean hasPrev();
        boolean hasNext();
        void pageCancel();

        void moveTurnPageBegin();
        void moveTurnPageFinished(boolean bFinished);
        void tapTurnPage();

        void autoReadPageFinish();

        void onScroll();
    }

    public abstract void btnChangePage(boolean isNext);

    protected void destroyBitmap(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
    }

}
