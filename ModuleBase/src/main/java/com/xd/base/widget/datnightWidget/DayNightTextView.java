package com.xd.base.widget.datnightWidget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.xd.base.R;
import com.xd.base.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class DayNightTextView extends androidx.appcompat.widget.AppCompatTextView {
    public DayNightTextView(Context context) {
        super(context);
    }

    public DayNightTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);
    }

    public DayNightTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }

    private boolean isNightMode;
    private ColorStateList dayModeTextColor;
    private ColorStateList nightModeTextColor;
    private Drawable dayModeBackground;
    private Drawable nightModeBackground;
    private Drawable nightDrawableLeft;
    private Drawable nightDrawableRight;
    private Drawable nightDrawableTop;
    private Drawable nightDrawableBottom;
    private Drawable dayDrawableLeft;
    private Drawable dayDrawableRight;
    private Drawable dayDrawableTop;
    private Drawable dayDrawableBottom;
    private String mDayText;
    private String mNightText;

    private void initAttrs(Context context, AttributeSet attributeSet) {
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DayNightTextView);
        isNightMode = typedArray.getBoolean(R.styleable.DayNightTextView_textNightmode, false);
        dayModeTextColor = getTextColors();
        dayModeBackground = getBackground();
        dayDrawableLeft = getCompoundDrawables()[0];
        dayDrawableTop = getCompoundDrawables()[1];
        dayDrawableRight = getCompoundDrawables()[2];
        dayDrawableBottom = getCompoundDrawables()[3];
        CharSequence dayText = typedArray.getText(R.styleable.DayNightTextView_dayText);
        if (dayText!=null) {
            mDayText = String.valueOf(dayText);
        }
        CharSequence nightText = typedArray.getText(R.styleable.DayNightTextView_nightText);
        if (dayText!=null) {
            mNightText = String.valueOf(nightText);
        }
        nightModeTextColor = typedArray.getColorStateList(R.styleable.DayNightTextView_nightTextColor);
        nightModeBackground = typedArray.getDrawable(R.styleable.DayNightTextView_nightBackground);
        nightDrawableLeft = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableLeft);
        nightDrawableRight = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableRight);
        nightDrawableTop = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableTop);
        nightDrawableBottom = typedArray.getDrawable(R.styleable.DayNightTextView_nightDrawableBottom);
        toggleTextColor();
    }

    public void setNightMode(boolean isNightMode) {
        this.isNightMode = isNightMode;
        toggleTextColor();
    }

    private void toggleTextColor() {
        if (isNightMode) {
            if (nightModeTextColor != null) {
                setTextColor(nightModeTextColor);
            }

            if (nightModeBackground != null) {
                setBackground(nightModeBackground);
            }
            Drawable[] drawables = getCompoundDrawables();
            if (nightDrawableLeft != null) {
                drawables[0] = nightDrawableLeft;
            }
            if (nightDrawableTop != null) {
                drawables[1] = nightDrawableTop;
            }
            if (nightDrawableRight != null) {
                drawables[2] = nightDrawableRight;
            }
            if (nightDrawableBottom != null) {
                drawables[3] = nightDrawableBottom;
            }
            setCompoundDrawablesWithIntrinsicBounds(drawables[0],drawables[1],drawables[2],drawables[3]);
            if (NStringUtils.isNotBlank(mNightText)) {
                setText(mNightText);
            }
        }else {
            setTextColor(dayModeTextColor);
            setBackground(dayModeBackground);
            if (NStringUtils.isNotBlank(mDayText)) {
                setText(mDayText);
            }
            setCompoundDrawablesWithIntrinsicBounds(dayDrawableLeft,dayDrawableTop,dayDrawableRight,dayDrawableBottom);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    /**
     * 设置TextView段落间距
     */
    public void setParagraphSpacing(String content, int paragraphSpacing) {
        if (NStringUtils.isBlank(content)) {
            setText("");
            return;
        }
        if (!content.contains("\n")&&!content.contains("\r")) {
            setText(content);
            return;
        }
        try {
            content = content.replaceAll("\r", "\n");
            content = content.replace("\n", "\n\r");

            int previousIndex = content.indexOf("\n\r");
            //记录每个段落开始的index，第一段没有，从第二段开始
            List<Integer> nextParagraphBeginIndexes = new ArrayList<>();
            nextParagraphBeginIndexes.add(previousIndex);
            while (previousIndex != -1) {
                int nextIndex = content.indexOf("\n\r", previousIndex + 2);
                previousIndex = nextIndex;
                if (previousIndex != -1) {
                    nextParagraphBeginIndexes.add(previousIndex);
                }
            }
            //获取行高（包含文字高度和行距）
            float lineHeight = getLineHeight();

            //把\r替换成透明长方形（宽:1px，高：字高+段距）
            SpannableString spanString = new SpannableString(content);
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.paragraph_space);
            float density = getContext().getResources().getDisplayMetrics().density;
            float lineSpacingExtra = getLineSpacingExtra();
            //int强转部分为：行高 - 行距 + 段距
            d.setBounds(0, 0, 1, (int) (lineHeight - lineSpacingExtra + paragraphSpacing * density));

            for (int index : nextParagraphBeginIndexes) {
                // \r在String中占一个index
                spanString.setSpan(new ImageSpan(d), index + 1, index + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(spanString);
        } catch (Exception e) {
            setText(content);
        }
    }


    public void highlightSameWord(String tarWord, @ColorInt int color) {
        String curText = getText().toString().trim();
        if (NStringUtils.isBlank(curText) || NStringUtils.isBlank(tarWord)) {
            return;
        }
        for (int i = 0; i < tarWord.length(); i++) {
            try {
                String curChar = tarWord.substring(i, i + 1);
                if (curText.contains(curChar)) {
                    changeWordColor(curChar, color);
                }
            } catch (Exception e) {
                continue;
            }
        }
    }

    public void changeWordColor(String word, @ColorInt int color) {
        String content = getText().toString().trim();
        if (content.contains(word)) {
            changeWordColor(content.indexOf(word), content.indexOf(word)+word.length(), color);
        }
    }

    public void changeWordColor(int startindex, int endindex, @ColorInt int colr) {
        SpannableString spanString = new SpannableString(getText());
        ForegroundColorSpan agreeColorSpan = new ForegroundColorSpan(colr);
        spanString.setSpan(agreeColorSpan, startindex, endindex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        CharSequence cs = TextUtils.expandTemplate(spanString);
        setText(cs);
        setHighlightColor(getResources().getColor(R.color.trasparent));
    }
}
