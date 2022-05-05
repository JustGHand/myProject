package com.xd.base.widget.stylewidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.AppCompatTextView;

import com.xd.base.R;

public class StyleTextView extends AppCompatTextView {
    public StyleTextView(@NonNull Context context) {
        super(context);
    }

    public StyleTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public StyleTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private @StyleRes int styleId;

    private ColorStateList styleTextColor;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet,R.styleable.ydxReadStyle);
        styleTextColor = typedArray.getColorStateList(R.styleable.ydxReadStyle_dialogTextColor);
        updateTextStyle();
    }

    public void setStyle(int resId) {
        TypedArray typedArray = getContext().obtainStyledAttributes(resId, R.styleable.ydxReadStyle);
        styleTextColor = typedArray.getColorStateList(R.styleable.ydxReadStyle_dialogTextColor);
        updateTextStyle();
    }

    private void updateTextStyle() {
        if (styleTextColor != null) {
            setTextColor(styleTextColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        updateTextStyle();
        super.onDraw(canvas);
    }
}
