package com.pw.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSeekBar;

import com.pw.base.R;

public class DayNightSeekBar extends AppCompatSeekBar {
    public DayNightSeekBar(Context context) {
        super(context);
    }

    public DayNightSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private boolean isNightMode;
    private Drawable dayModeProgressDrawable;
    private Drawable dayModeThumbDrawable;
    private Drawable nightModeProgressDrawable;
    private Drawable nightModeThumbDrawable;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightSeekBar);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightSeekBar_seekBarNightmode, false);
        dayModeProgressDrawable = getProgressDrawable();
        dayModeThumbDrawable = getThumb();
        nightModeProgressDrawable = typedArray.getDrawable(R.styleable.DayNightSeekBar_nightProgressDrawable);
        nightModeThumbDrawable = typedArray.getDrawable(R.styleable.DayNightSeekBar_nightThumb);
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
        toggleDrawable();
    }

    private void toggleDrawable() {
        if (isNightMode) {
            if (nightModeProgressDrawable != null) {
                setProgressDrawable(nightModeProgressDrawable);
            }
            if (nightModeThumbDrawable != null) {
                setThumb(nightModeThumbDrawable);
            }
        }else {
            setProgressDrawable(dayModeProgressDrawable);
            setThumb(dayModeThumbDrawable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
