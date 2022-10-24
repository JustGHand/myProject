package com.pw.read;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pw.read.bean.ChaptersBean;
import com.pw.read.bean.LayoutMode;
import com.pw.read.bean.PageStyle;
import com.pw.read.bean.TxtPage;
import com.pw.read.manager.PageDrawManager;
import com.pw.read.manager.ReadDataInterface;
import com.pw.read.manager.ReadTouchInterface;
import com.xd.baseutils.utils.ScreenUtils;

import java.io.BufferedReader;
import java.util.List;

public class ReadView extends FrameLayout {

    public ReadView(@NonNull Context context) {
        super(context);
    }

    public ReadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ReadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private List<ChaptersBean> mChapterList;
    private ReadDataInterface mData;

    private int mCurPagePos;
    private PageView mCurPage;

    private int mNextPagePos;
    private PageView mNextPage;

    private int mPrePagePos;
    private PageView mPrePage;

    private int mCurChapterPos;
    private List<TxtPage> mCurChapterPageList;

    private int mNextChapterPos;
    private List<TxtPage> mNextChapterPageList;

    private int mPreChapterPos;
    private List<TxtPage> mPreChapterPageList;

    public void init(List<ChaptersBean> chaptersBeans, ReadDataInterface readInterface) {
        mChapterList = chaptersBeans;
        mData = readInterface;
        mCurChapterPos = 0;
        mCurPagePos = 0;
        PageDrawManager.getInstance().init(getContext());
        LayoutMode layoutMode = new LayoutMode(10, 20, 20, 1, 40, 0, 0);
        PageDrawManager.getInstance().setParams(40, layoutMode, null, false, 0);
        PageStyle style = new PageStyle(R.color.read_pagestyle_green_fontcolor, R.color.read_pagestyle_green_pageback,R.color.read_pagestyle_green_highlightback,R.color.read_pagestyle_green_highlighttextcolor,0);
        PageDrawManager.getInstance().setPageStyle(style);
        PageDrawManager.getInstance().updateWH(ScreenUtils.getAppSize(getContext())[0],ScreenUtils.getAppSize(getContext())[1]);
    }

    public void nextChapter() {
        int nextChapterPos = mCurChapterPos + 1;
        if (nextChapterPos<mChapterList.size()) {
            toChapter(nextChapterPos);
        }else {
            Toast.makeText(getContext(),"已经是最后一章了",Toast.LENGTH_SHORT).show();
        }
    }

    public void preChapter() {
        int preChapterPos = mCurChapterPos - 1;
        if (preChapterPos>0){
            toChapter(preChapterPos);
        }else {
            Toast.makeText(getContext(),"已经是第一章了",Toast.LENGTH_SHORT).show();
        }
    }

    public void toChapter(int chapterPos) {
        if (mChapterList == null || mChapterList.size() <= 0) {
            return;
        }
        if (chapterPos >= 0 && chapterPos < mChapterList.size()) {
            ChaptersBean chaptersBean = mChapterList.get(chapterPos);
            if (mData != null) {
                BufferedReader br = mData.getChapterReader(chaptersBean);
                try {
                    if (br != null) {
                        mCurPagePos = 0;
                        mCurChapterPageList = PageDrawManager.getInstance().loadPages(chaptersBean, 0, br, getContext());

                        if (mCurChapterPageList != null && mCurChapterPageList.size() > 0) {
                            mCurChapterPos = chapterPos;
                            mCurPage = getPage();
                            mCurPage.setContent(mCurChapterPageList.get(mCurPagePos));
                            addView(mCurPage);
                        }
                    }
                    br.close();
                } catch (Exception e) {

                }
            }
        }

    }

    public void prePage() {
        mCurPagePos--;
        if (mCurPagePos>=0) {
            mCurPage.setContent(mCurChapterPageList.get(mCurPagePos));
        }else {
            mCurPagePos = 0;
            preChapter();
        }
    }

    public void nextPage() {
        mCurPagePos++;
        if (mCurPagePos<mCurChapterPageList.size()) {
            mCurPage.setContent(mCurChapterPageList.get(mCurPagePos));
        }else {
            mCurPagePos = 0;
            nextChapter();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private PageView getPage() {
        if (mCurPage != null) {
            removeView(mCurPage);
            mCurPage = null;
        }
        PageView page = new PageView(getContext());
        page.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        page.setTouchInterface(new ReadTouchInterface() {
            @Override
            public boolean onTouch(MotionEvent event) {



                return false;
            }

            @Override
            public void onClick() {
                nextPage();
            }
        });
        return page;
    }

    boolean isMove;
    int mStartX;
    int mStartY;

    int mViewWidth;
    int mViewHeight;

    private RectF mCenterRect = null;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN){
            isMove = false;
        }

        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                // 判断是否大于最小滑动值。
                int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.getX()) > slop || Math.abs(mStartY - event.getY()) > slop;
                }



                // 如果滑动了，则进行翻页。
                if (isMove) {
                }
                break;
            case MotionEvent.ACTION_UP:

                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = new RectF(mViewWidth / 4, mViewHeight / 5,
                                mViewWidth * 3 / 4, mViewHeight * 4 / 5);
                    }

                    //是否点击了中间
                    if (mCenterRect.contains(x, y)) {

                        return true;
                    }
                }

                if (isTouchNext(x, y)) {
                    nextPage();
                }else {
                    prePage();
                }

                break;
            default:
                break;
        }
        return true;
    }

    private boolean isTouchNext(int x, int y) {
        boolean isNext = false;
//        if (x < mScreenWidth / 2){
//            isNext = false;
//        }else{
//            isNext = true;
//        }
        if (x < mViewWidth / 4) {//点击左半边
            isNext = false;
        } else if ((x < (mViewWidth * 3 / 4)) && (y < mViewHeight / 5)) {//点击菜单区域上部分
            isNext = false;
        }else {
            isNext = true;
        }
        return isNext;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;

    }
}
