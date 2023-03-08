package com.pw.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.pw.base.R;

public class DayNightImageView extends androidx.appcompat.widget.AppCompatImageView {
    public DayNightImageView(Context context) {
        super(context);
    }

    public DayNightImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private boolean isNightMode;
    private Drawable dayModeDrawable;
    private Drawable nightModeDrawable;
    private Drawable nightModeBack;
    private Drawable dayModeBack;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightImageView);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightImageView_ImageNightmode, false);
        dayModeDrawable = getDrawable();
        dayModeBack = getBackground();
        nightModeDrawable = typedArray.getDrawable(R.styleable.DayNightImageView_ImageNightSrc);
        nightModeBack = typedArray.getDrawable(R.styleable.DayNightImageView_ImageNightBackground);
    }

    public void setDayImage(Drawable drawable) {
        this.dayModeDrawable = drawable;
        toggleImageDrawable();
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
        toggleImageDrawable();
    }

    public void setNightImage(Drawable drawable) {
        this.nightModeDrawable = drawable;
        toggleImageDrawable();
    }

    public void setDayModeBack(Drawable dayModeBack) {
        this.dayModeBack = dayModeBack;
    }
    public void setNightModeBack(Drawable nightModeBack) {
        this.nightModeBack = nightModeBack;
    }

    public void setTotalBack(Drawable drawable) {
        this.dayModeBack = drawable;
        this.nightModeBack = drawable;
        toggleImageDrawable();
    }

    private void toggleImageDrawable() {
        if (isNightMode) {
            if (nightModeDrawable != null) {
                setImageDrawable(nightModeDrawable);
            }
            if (nightModeBack != null) {
                setBackground(nightModeBack);
            }
        }else {
            setImageDrawable(dayModeDrawable);
            setBackground(dayModeBack);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
