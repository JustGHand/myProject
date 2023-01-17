package com.pw.codeset.weidgt;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;


import com.pw.codeset.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 2019/6/14.
 */

public class WarpLinearLayout extends ViewGroup {

    private Type mType;
    private List<WarpLine> mWarpLineGroup;

    public WarpLinearLayout(Context context) {
        this(context, null);
    }

    public WarpLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.WarpLinearLayoutDefault);
    }

    public WarpLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mType = new Type(context, attrs);
        temMaxlines = mType.maxLines;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int with = 0;
        int height = 0;
        int lineCount = 0;
        int childCount = super.getChildCount();
        /**
         * 在调用childView。getMeasre之前必须先调用该行代码，用于对子View大小的测量
         */
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        /**
         * 计算宽度
         */
        switch (withMode) {
            case MeasureSpec.EXACTLY:
                with = withSize;
                break;
            case MeasureSpec.AT_MOST:
                for (int i = 0; i < childCount; i++) {
                    if (i != 0) {
                        with += mType.horizontal_Space;
                    }
                    with += getChildWidth(i);
                }
                with += getPaddingLeft() + getPaddingRight();
                with = with > withSize ? withSize : with;
                break;
            case MeasureSpec.UNSPECIFIED:
                for (int i = 0; i < childCount; i++) {
                    if (i != 0) {
                        with += mType.horizontal_Space;
                    }
                    with += getChildWidth(i);
                }
                with += getPaddingLeft() + getPaddingRight();
                break;
            default:
                with = withSize;
                break;

        }
        /**
         * 根据计算出的宽度，计算出所需要的行数
         */
        WarpLine warpLine = new WarpLine();
        /**
         * 不能够在定义属性时初始化，因为onMeasure方法会多次调用
         */
        mWarpLineGroup = new ArrayList<WarpLine>();
        for (int i = 0; i < childCount; i++) {
            if (warpLine.lineWidth + getChildWidth(i) + mType.horizontal_Space > with) {
                if (temMaxlines == 0 || lineCount < temMaxlines-1) {
                    if (warpLine.lineView.size() == 0) {
                        warpLine.addView(super.getChildAt(i));
                        mWarpLineGroup.add(warpLine);
                        warpLine = new WarpLine();
                    } else {
                        mWarpLineGroup.add(warpLine);
                        warpLine = new WarpLine();
                        warpLine.addView(super.getChildAt(i));
                    }
                    lineCount++;
                }else {
                    if(!haveAdExpand||i-1!=expandIndex) {
                        if (expandView != null) {
                            expandView.setVisibility(VISIBLE);
                            expandView.setTag(EXPANDVIEW_TAG);
                            expandIndex = i - 1;
                            expandView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (expandView.isSelected()) {
                                        temMaxlines = mType.maxLines;
                                        removeView(expandView);
                                        addView(expandView, expandIndex);
                                    } else {
                                        temMaxlines = Integer.MAX_VALUE;
                                        removeView(expandView);
                                        addView(expandView, getChildCount()+1);
                                    }
                                    expandView.setSelected(!expandView.isSelected());
                                    if (mListener != null) {
                                        mListener.onExpand(temMaxlines != Integer.MAX_VALUE);
                                    }
                                }
                            });
                            haveAdExpand = true;
                            if (expandView.getParent()!=null) {
                                ((ViewGroup) expandView.getParent()).removeView(expandView);
                            }
                            expandView.setSelected(false);
                            warpLine.removeView(warpLine.lineView.size()-1);
                            warpLine.addView(expandView);
                            addView(expandView, expandIndex);
                            break;
                        }
                    }
                }
            } else {
                warpLine.addView(super.getChildAt(i));
            }
        }
        /**
         * 添加最后一行
         */
        if (warpLine.lineView.size() > 0 && !mWarpLineGroup.contains(warpLine)) {
            mWarpLineGroup.add(warpLine);
        }
        /**
         * 计算宽度
         */
        height = getPaddingTop() + getPaddingBottom();
        for (int i = 0; i < mWarpLineGroup.size(); i++) {
            if (i != 0) {
                height += mType.vertical_Space;
            }
            height += mWarpLineGroup.get(i).height;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
                height = height > heightSize ? heightSize : height;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }
        setMeasuredDimension(with, height);
    }

    private final String EXPANDVIEW_TAG = "expandView";

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < super.getChildCount(); i++) {
            super.getChildAt(i).layout(0,0,0,0);
        }
        t = getPaddingTop();
        for (int i = 0; i < mWarpLineGroup.size(); i++) {
            int left = getPaddingLeft();
            WarpLine warpLine = mWarpLineGroup.get(i);
            int lastWidth = getMeasuredWidth() - warpLine.lineWidth;
            for (int j = 0; j < warpLine.lineView.size(); j++) {
                View view = warpLine.lineView.get(j);
                if (isFull()) {//需要充满当前行时
                    view.layout(left, t, left + view.getMeasuredWidth() + lastWidth / warpLine.lineView.size(), t + view.getMeasuredHeight());
                    left += view.getMeasuredWidth() + mType.horizontal_Space + lastWidth / warpLine.lineView.size();
                } else {
                    if (view.getTag() != null) {
                        if (EXPANDVIEW_TAG.equals((String) view.getTag())) {
                            int expandLeft = getPaddingLeft() + getMeasuredWidth() - view.getMeasuredWidth();
                            int expandRight = expandLeft + view.getMeasuredWidth();
                            view.layout(expandLeft, t, expandRight, t + view.getMeasuredHeight());
                            continue;
                        }
                    }
                    switch (getGrivate()) {
                        case 0://右对齐
                            view.layout(left + lastWidth, t, left + lastWidth + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                        case 2://居中对齐
                            view.layout(left + lastWidth / 2, t, left + lastWidth / 2 + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                        default://左对齐
                            view.layout(left, t, left + view.getMeasuredWidth(), t + view.getMeasuredHeight());
                            break;
                    }
                    left += view.getMeasuredWidth() + mType.horizontal_Space;
                }
            }
            t += warpLine.height + mType.vertical_Space;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findViews();
    }

    private void findViews() {
        if (mType.expandViewId != 0) {
            expandView = findViewById(mType.expandViewId);
        }
    }
    boolean haveAdExpand = false;
    private View expandView;
    private int expandIndex=-1;
    private int temMaxlines;

    /**
     * 用于存放一行子View
     */
    private final class WarpLine {
        private List<View> lineView = new ArrayList<View>();
        /**
         * 当前行中所需要占用的宽度
         */
        private int lineWidth = getPaddingLeft() + getPaddingRight();
        /**
         * 该行View中所需要占用的最大高度
         */
        private int height = 0;

        private void addView(View view) {
            if (lineView.size() != 0) {
                lineWidth += mType.horizontal_Space;
            }
            height = height > view.getMeasuredHeight() ? height : view.getMeasuredHeight();
            lineWidth += view.getMeasuredWidth();
            lineView.add(view);
        }

        private void removeView(int index) {
            if (lineView.get(index) != null) {
                lineWidth = (int) (lineWidth - mType.horizontal_Space);
                lineWidth = lineWidth - lineView.get(index).getMeasuredWidth();
                int finalHeight = 0;
                for (int i = 0; i < lineView.size(); i++) {
                    if (i != index) {
                        if (finalHeight < lineView.get(i).getMeasuredHeight()) {
                            finalHeight = lineView.get(i).getMeasuredHeight();
                        }
                    }
                }
                height = finalHeight;
                lineView.remove(index);
            }
        }
    }

    /**
     * 对样式的初始化
     */
    private final static class Type {
        /*
         *对齐方式 right 0，left 1，center 2
        */
        private int grivate;
        /**
         * 水平间距,单位px
         */
        private float horizontal_Space;
        /**
         * 垂直间距,单位px
         */
        private float vertical_Space;
        /**
         * 是否自动填满
         */
        private boolean isFull;

        /**
         * 最大显示行数
         */
        private int maxLines;

        private int expandViewId;

        Type(Context context, AttributeSet attrs) {
            if (attrs == null) {
                return;
            }
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WarpLinearLayout);
            grivate = typedArray.getInt(R.styleable.WarpLinearLayout_grivate, grivate);
            horizontal_Space = typedArray.getDimension(R.styleable.WarpLinearLayout_horizontal_Space, horizontal_Space);
            vertical_Space = typedArray.getDimension(R.styleable.WarpLinearLayout_vertical_Space, vertical_Space);
            isFull = typedArray.getBoolean(R.styleable.WarpLinearLayout_isFull, isFull);
            maxLines = typedArray.getInt(R.styleable.WarpLinearLayout_maxLines, maxLines);
            expandViewId = typedArray.getResourceId(R.styleable.WarpLinearLayout_expandviewId,0);
        }
    }

    public int getGrivate() {
        return mType.grivate;
    }

    public float getHorizontal_Space() {
        return mType.horizontal_Space;
    }

    public float getVertical_Space() {
        return mType.vertical_Space;
    }

    public boolean isFull() {
        return mType.isFull;
    }

    public int getMaxLindes() {
        return mType.maxLines;
    }

    public void setGrivate(int grivate) {
        mType.grivate = grivate;
    }

    public void setHorizontal_Space(float horizontal_Space) {
        mType.horizontal_Space = horizontal_Space;
    }

    public void setVertical_Space(float vertical_Space) {
        mType.vertical_Space = vertical_Space;
    }

    public void setIsFull(boolean isFull) {
        mType.isFull = isFull;
    }

    public void setMaxLines(int maxLines) {
        mType.maxLines = maxLines;
        temMaxlines = mType.maxLines;
        requestLayout();
        invalidate();
    }

    /**
     * 每行子View的对齐方式
     */
    public final static class Gravite {
        public final static int RIGHT = 0;
        public final static int LEFT = 1;
        public final static int CENTER = 2;
    }

    private int getChildWidth(int i) {
        int measureWidth = super.getChildAt(i).getMeasuredWidth();
        if (measureWidth == 0) {
            super.getChildAt(i).measure(MeasureSpec.EXACTLY, MeasureSpec.EXACTLY);
            measureWidth = super.getChildAt(i).getMeasuredWidth();
        }
        int minWidth = super.getChildAt(i).getMinimumWidth();
        return measureWidth > minWidth ? measureWidth : minWidth;
    }

    public int getShownChildCount() {
        int showCount = 0;
        for (int i = 0; i < mWarpLineGroup.size(); i++) {
            showCount += mWarpLineGroup.get(i).lineView.size();
        }
        return showCount;
    }

    @Override
    public int getChildCount() {
        if (haveAdExpand) {
            return super.getChildCount() - 1;
        }
        return super.getChildCount();
    }

    @Override
    public View getChildAt(int index) {
        if (haveAdExpand) {
            int expandIndex = indexOfChild(expandView);
            if (index >= expandIndex) {
                return super.getChildAt(index+1);
            }
        }
        return super.getChildAt(index);
    }

    private ExpandListener mListener;

    public void setExpandListener(ExpandListener listener) {
        mListener = listener;
    }

    public interface ExpandListener{
        void onExpand(boolean expand);
    }
}