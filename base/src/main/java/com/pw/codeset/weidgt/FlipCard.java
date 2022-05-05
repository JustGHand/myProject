package com.pw.codeset.weidgt;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;

import java.util.Random;

public class FlipCard extends ConstraintLayout {
    public FlipCard(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public FlipCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initAttr(context,attrs);
    }

    public FlipCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initAttr(context,attrs);
    }

    public FlipCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
        initAttr(context,attrs);
    }

    ConstraintLayout mFrontView;
    ConstraintLayout mBackView;

    private boolean isFrontShowing = true;

    public boolean isFrontShowing() {
        return isFrontShowing;
    }

    public interface onAnimListener{
        void onStart();

        void onFinish();
    }

    onAnimListener mListener;

    public void setOnAnimListener(onAnimListener listener) {
        mListener = listener;
    }

    public boolean isAnimRunning() {
        return isAnimRunning;
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_flipcard, this, true);
        mFrontView = findViewById(R.id.flip_front);
        mBackView = findViewById(R.id.flip_back);
    }

    int mSpeed;
    int circleSpeed;

    private void initAttr(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.FlipCard);
        mSpeed = typedArray.getInt(R.styleable.FlipCard_flipSpeed, 300);
        boolean showFront = typedArray.getBoolean(R.styleable.FlipCard_showFront, true);
        if (!showFront) {
            mFrontView = findViewById(R.id.flip_back);
            mBackView = findViewById(R.id.flip_front);
        }
        mFrontView.setAlpha(1);
        mBackView.setAlpha(0);
    }

    public void showView(boolean front) {
        if (!front) {
            mFrontView = findViewById(R.id.flip_back);
            mBackView = findViewById(R.id.flip_front);
        }else {
            mFrontView = findViewById(R.id.flip_front);
            mBackView = findViewById(R.id.flip_back);
        }
        isFrontShowing = front;
        mFrontView.setAlpha(1);
        mBackView.setAlpha(0);
    }

    public void setSpeed(int speed) {
        mSpeed = speed;
    }

    public void flip() {

        if (isAnimRunning) {
            return;
        }
        isAnimRunning = true;
        finishAnimCount = 0;

        int speed = needCircle ? circleSpeed : mSpeed;

        ValueAnimator frontAnim = ValueAnimator.ofFloat(0, 180);
        frontAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (value >= 90) {
                    mFrontView.setAlpha(0);
                }
                mFrontView.setRotationY(value);
            }
        });
        frontAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mFrontView.setRotationY(0);
        mFrontView.setAlpha(1);
        frontAnim.setDuration(speed);
        frontAnim.start();


        ValueAnimator backAnim = ValueAnimator.ofFloat(180, 0);
        backAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (value <= 90) {
                    mBackView.setAlpha(1);
                }
                mBackView.setRotationY(-value);
            }
        });
        backAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animFinish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mBackView.setAlpha(0);
        mBackView.setRotationY(-180);
        backAnim.setDuration(speed);
        backAnim.start();
        isFrontShowing = !isFrontShowing;
        if (mListener != null && !isCircleing()) {
            mListener.onStart();
        }
    }

    boolean isAnimRunning = false;

    int finishAnimCount = 0;

    private void animFinish() {
        finishAnimCount++;
        if (finishAnimCount >= 2) {
            ConstraintLayout tmpView = mBackView;
            mBackView = mFrontView;
            mFrontView = tmpView;
            isAnimRunning = false;
            if (needCircle) {
                flip();
            }else {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        }
    }

    boolean needCircle = false;

    public boolean isCircleing() {
        return needCircle;
    }

    public void startRound() {
        needCircle = true;
        circleSpeed = 100+(int)(Math.random()*50);
        flip();
    }

    public void endRound() {
        needCircle = false;
    }

}
