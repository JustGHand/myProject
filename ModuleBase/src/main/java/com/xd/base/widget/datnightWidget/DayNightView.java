package com.pw.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;

import com.pw.base.R;

public class DayNightView extends View {
    public DayNightView(Context context) {
        super(context);
    }

    public DayNightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private int daymodeColor;
    private int nightModeColor;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightView);
        daymodeColor = typedArray.getResourceId(R.styleable.DayNightView_cutlineColor,0);
        nightModeColor = typedArray.getResourceId(R.styleable.DayNightView_cutlineNightColor,0);
        toggleTextColor();
    }

    private boolean isNightMode = false;

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
        toggleTextColor();
    }

    public void setCutlineColor(@ColorRes int cutlineColor) {
        daymodeColor = cutlineColor;
        toggleTextColor();
    }
    public void setNightCutlineColor(@ColorRes int cutlineColor) {
        nightModeColor = cutlineColor;
        toggleTextColor();
    }

    private void toggleTextColor() {
        if (isNightMode) {
            if (nightModeColor != 0) {
                setBackgroundResource(nightModeColor);
            }
        }else {
            if (daymodeColor != 0) {
                setBackgroundResource(daymodeColor);
            }
        }

    }
    public interface DrawListener{
        void onDrawFinish();
        void onFirstDrawFinish();
    }

    public void setDrawListener(DrawListener listener) {
        mDrawListener = listener;
    }

    private DrawListener mDrawListener;
    private boolean isFirstDrawFinish = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isFirstDrawFinish) {
            isFirstDrawFinish = true;
            if (mDrawListener != null) {
                mDrawListener.onFirstDrawFinish();
            }
        }
    }

}
