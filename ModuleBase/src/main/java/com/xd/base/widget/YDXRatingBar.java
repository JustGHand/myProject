package com.pw.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.pw.base.R;

public class YDXRatingBar extends View {
    private Bitmap mStartNomal;
    private Bitmap mStartFocus;
    private int totalNumner=5;
    private int selectNumber=0;
    private int intervalNumner=10;
    private int starWidth=0;
    private int starHeight=0;
    private boolean isSelect=true;
    private YDXRatingListener mListener;
    public YDXRatingBar(Context context) {
        this(context,null);
    }

    public YDXRatingBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public YDXRatingBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray=context.obtainStyledAttributes(attrs, R.styleable.YDXRatingBar);
        int image1=typedArray.getResourceId(R.styleable.YDXRatingBar_statrNormal,0);
        int image2=typedArray.getResourceId(R.styleable.YDXRatingBar_statrFocus,0);
        intervalNumner= (int) typedArray.getDimension(R.styleable.YDXRatingBar_intervalNumner,px2dp(intervalNumner));
        starWidth= (int) typedArray.getDimension(R.styleable.YDXRatingBar_starWidth,0);
        starHeight= (int) typedArray.getDimension(R.styleable.YDXRatingBar_starHeight,0);
        mStartNomal= BitmapFactory.decodeResource(getResources(),image1);
        mStartFocus= BitmapFactory.decodeResource(getResources(),image2);
        totalNumner=  typedArray.getInt(R.styleable.YDXRatingBar_totalNumner,totalNumner);
        selectNumber=  typedArray.getInt(R.styleable.YDXRatingBar_selectNumber,selectNumber);
        isSelect=  typedArray.getBoolean(R.styleable.YDXRatingBar_isSelect,isSelect);
        if (starWidth == 0) {
            starWidth = mStartNomal.getWidth();
        }
        if (starHeight == 0) {
            starHeight = mStartNomal.getHeight();
        }
        typedArray.recycle();
    }

    private int px2dp(int yhsize) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,yhsize,getResources().getDisplayMetrics());
    }

    public void setRatingListener(YDXRatingListener listener) {
        mListener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //图片的高度
        int height = starHeight;
        int wight = starWidth * totalNumner + (intervalNumner - 1) * totalNumner;
        setMeasuredDimension(wight,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < totalNumner; i++) {
            int x = i * starWidth + intervalNumner * i;
            Rect dstRect = new Rect(x, 0, x+starWidth, starHeight);
            if (selectNumber>i){
                Rect srcRect = new Rect(0, 0, mStartFocus.getWidth(), mStartFocus.getHeight());
                canvas.drawBitmap(mStartFocus,srcRect,dstRect,null);
            }else{
                Rect srcRect = new Rect(0, 0, mStartNomal.getWidth(), mStartNomal.getHeight());
                canvas.drawBitmap(mStartNomal,srcRect,dstRect,null);
            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN://按下
            case MotionEvent.ACTION_MOVE://移动
            case MotionEvent.ACTION_UP://抬起
                if (isSelect){
                    float movex=event.getX();
                    int num=mStartNomal.getWidth()+intervalNumner;
                    int current= (int) (movex/num+1);
                    if (current<0){
                        current=0;
                    }
                    if (current>totalNumner){
                        current=totalNumner;
                    }
                    if (selectNumber != current) {
                        selectNumber=current;
                        if (mListener != null) {
                            mListener.onRatingChange(selectNumber);
                        }
                    }
                    //刷新
                    invalidate();
                }
                break;
        }
        return true;
    }

    public int getRating() {
        return selectNumber;
    }

    public interface YDXRatingListener{
        void onRatingChange(int rating);
    }
}