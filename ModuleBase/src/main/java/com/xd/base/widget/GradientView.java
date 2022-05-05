package com.xd.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GradientView extends View {
    public GradientView(Context context) {
        super(context);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GradientView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Integer startColor;
    private Integer endColor;

    public void setColor(int startColor, int endColor) {
        this.startColor = startColor;
        this.endColor = endColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (startColor != null && endColor != null) {
            int width = getWidth();
            int height = getHeight();
            Paint paint = new Paint();
            LinearGradient linearGradient = new LinearGradient(0, 0, 0, height, new int[]{startColor, endColor},null, Shader.TileMode.CLAMP);
            paint.setShader(linearGradient);
            canvas.drawRect(0,0,width,height,paint);
        }
    }
}
