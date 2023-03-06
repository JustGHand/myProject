package com.pw.read;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.pw.read.bean.ChaptersBean;
import com.pw.read.bean.LayoutMode;
import com.pw.read.bean.LineInfo;
import com.pw.read.bean.PageStyle;
import com.pw.read.bean.TxtPage;
import com.pw.read.interfaces.ReadCallBack;
import com.pw.read.interfaces.ReadDataInterface;
import com.pw.read.interfaces.ReadTouchInterface;
import com.pw.read.manager.PageDrawManager;
import com.pw.read.manager.ReadConfigManager;
import com.xd.baseutils.utils.ScreenUtils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ReadView extends ConstraintLayout {

    public ReadView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public ReadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private List<ChaptersBean> mChapterList;
    private ReadDataInterface mData;
    private ReadTouchInterface mTouchListener;

    private ReadCallBack mCallBack;

    ViewPager2 mViewPager;
    ReadPageAdapter mPageAdapter;

    int mCurPagePos;
    int mCurPagerPageIndex;

    private int mCurChapterPos;
    private List<TxtPage> mCurChapterPageList;

    private int mNextChapterPos;
    private List<TxtPage> mNextChapterPageList;

    private int mPreChapterPos;
    private List<TxtPage> mPreChapterPageList;


    boolean isMove;
    int mStartX;
    int mStartY;
    int mTouchX;
    int mTouchY;
    int mViewWidth;
    int mViewHeight;

    private RectF mCenterRect = null;

    private void initView(Context context) {
        PageDrawManager.getInstance().init(getContext());
        ReadConfigManager.getInstance().updateContext(context);
        LayoutMode layoutMode = ReadConfigManager.getInstance().getLayoutMode();
        int textSize = ReadConfigManager.getInstance().getFontSize();
        PageDrawManager.getInstance().setParams(ScreenUtils.spToPx(getContext(),textSize), layoutMode, null, false, 0);
        PageStyle style = new PageStyle(R.color.read_pagestyle_green_fontcolor, R.color.read_pagestyle_green_pageback,R.color.read_pagestyle_green_highlightback,R.color.read_pagestyle_green_highlighttextcolor,0);
        PageDrawManager.getInstance().setPageStyle(style);
        PageDrawManager.getInstance().updateWH(ScreenUtils.getAppSize(getContext())[0],ScreenUtils.getAppSize(getContext())[1]);
        LayoutInflater.from(context).inflate(R.layout.view_read, this, true);
        mViewPager = findViewById(R.id.read_pager);
        mPageAdapter = new ReadPageAdapter(new ReadPageAdapter.CAllAbck() {
            @Override
            public TxtPage getContent(int position) {
                return getPage(position);
            }

            @Override
            public int getPageCount() {
                if (mChapterList == null) {
                    return 0;
                }
                return (havePrev() && haveNext()) ? 3 : 2;
            }
        });
        mViewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
//                if (page instanceof ConstraintLayout) {
//                    PageView pageView = page.findViewById(R.id.item_page);
//                    if (pageView != null && pageView.getCurPage()==getCurPage()) {
//                        pageView.setTranslationX((-pageView.getWidth() * position));
//                        pageView.drawMove(mStartX,mStartY,mTouchX,mTouchY);
//                    }
//                }
                //只对[-1,1]之间处理，超过范围将属性设置为初始值

                if (position < -1) { // [-Infinity,-1)
//                    page.setPivotX(0f);
//                    page.setScaleX(1f);
//                    page.setTranslationX(0);
                    page.setAlpha(1);
                } else if (position <= 0) { // [-1,0]
//                    page.setPivotX(0f);
//                    page.setTranslationX(-position * page.getWidth());
//                    page.setScaleX(position + 1);
                    page.setAlpha(position);
                } else if (position <= 1) { // (0,1]
//                    page.setPivotX(page.getWidth());
//                    page.setTranslationX(-position * page.getWidth());
//                    page.setScaleX(1-position);
                    page.setAlpha(position);
                } else { // (1,+Infinity]
//                    page.setPivotX(0f);
//                    page.setScaleX(1f);
//                    page.setTranslationX(0);
                    page.setAlpha(1);
                }
            }
        });
    }

    int pageTurnShif = 0;

    public void init(List<ChaptersBean> chaptersBeans, ReadDataInterface readInterface) {
        mChapterList = chaptersBeans;
        mData = readInterface;
        mCurChapterPos = 0;
        mCurPagePos = 0;

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                pageTurnShif =position - mCurPagerPageIndex;
                mCurPagerPageIndex = position;
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                if (state==0) {
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            changePagePos(mCurPagePos + pageTurnShif);
                            mPageAdapter.notifyDataSetChanged();
                            updatePage();
                        }
                    });
                }
            }
        });
        mViewPager.setAdapter(mPageAdapter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        getContext().registerReceiver(mReceiver, intentFilter);
    }

    public void setReadCallBack(ReadCallBack callBack) {
        mCallBack = callBack;
    }

    // 接收电池信息和时间更新的广播
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                PageDrawManager.getInstance().setmBatteryLevel(level);
            }
            // 监听分钟的变化
            else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                reloadPage();
            }
        }
    };
    public void setTouchListener(ReadTouchInterface touchInterface) {
        mTouchListener = touchInterface;
    }

    public void openBook() {
        List<PageView> pageViewList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            PageView page = new PageView(getContext());
            page.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            pageViewList.add(page);
        }
        mCurPagePos = 0;
        mPageAdapter.notifyDataSetChanged();
    }

    public void setTextSize(int textSize) {
        PageDrawManager.getInstance().setUpTextParams(ScreenUtils.spToPx(getContext(), textSize));
        ReadConfigManager.getInstance().setFontSize(textSize);
        reloadPage();
    }

    public int getTextSize() {
        return ReadConfigManager.getInstance().getFontSize();
    }

    public void setTextInterval(int textInterval) {
        ReadConfigManager.getInstance().setTextInterval(textInterval);
        LayoutMode layoutMode = ReadConfigManager.getInstance().getLayoutMode();
        PageDrawManager.getInstance().setLayoutModeParm(layoutMode);
        reloadPage();
    }

    public int getTextInterval() {
        return ReadConfigManager.getInstance().getTextInterval();
    }

    public void setPagePadding(int pagePadding) {
        ReadConfigManager.getInstance().setPagePadding(pagePadding);
        LayoutMode layoutMode = ReadConfigManager.getInstance().getLayoutMode();
        PageDrawManager.getInstance().setLayoutModeParm(layoutMode);
        reloadPage();
    }

    public int getPagePadding() {
        return ReadConfigManager.getInstance().getPagePadding();
    }

    public void reloadPage() {
        toPage(mCurChapterPos,getCurPageCharPos());
    }

    public void toChapter(int chapterPos) {
        toPage(chapterPos,0);
    }

    public void prePage() {
//        changePagePos(mCurPagePos - 1);
//        mCurPagerPageIndex--;
        mViewPager.setCurrentItem(mCurPagerPageIndex-1,true);
    }

    public void nextPage() {
//        changePagePos(mCurPagePos + 1);
//        mCurPagerPageIndex++;
        mViewPager.setCurrentItem(mCurPagerPageIndex+1,true);
    }

    public void toPage(int chapterPos, int charPos) {

        if (mChapterList == null || mChapterList.size() <= 0) {
            return;
        }
        if (chapterPos >= 0 && chapterPos < mChapterList.size()) {
            changeChapterPos(chapterPos);
            List<TxtPage> curChapterPageList = getCurChapterPageList();
            if (curChapterPageList != null && curChapterPageList.size() > 0) {
                for (int i = 0; i < curChapterPageList.size(); i++) {
                    TxtPage page = curChapterPageList.get(i);
                    if (page.isCharInPage(charPos)) {
                        changePagePos(i);
                        break;
                    }
                }
                mViewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        mPageAdapter.notifyDataSetChanged();
                        updatePage();
                    }
                });
            }
        }

    }

    private void updatePage() {
        if (mCurPagerPageIndex == 0 ) {
            if (mCurPagePos == 0 && mCurChapterPos == 0) {
                return;
            }
            mCurPagerPageIndex = 1;
            mViewPager.setCurrentItem(1, false);
        }
        if (mCurPagerPageIndex > 1) {
            mCurPagerPageIndex = 1;
            mViewPager.setCurrentItem(1,false);
        } else if (mCurPagerPageIndex == 1) {
            if (mCurChapterPos == 0 && mCurPagePos == 0) {
                mCurPagerPageIndex = 0;
                mViewPager.setCurrentItem(0,false);
                return;
            }
            mCurPagerPageIndex = 1;
            mViewPager.setCurrentItem(1,false);
        }
        if (mCallBack != null) {
            mCallBack.onPageChange();
        }
    }

    private TxtPage getPage(int pos) {
        if (pos == 0) {
            if (!havePrev()) {
                return getCurPage();
            }else {
                return getPrePage();
            }
        } else if (pos == 1) {
            if (!havePrev()) {
                return getNextPage();
            }else {
                return getCurPage();
            }
        } else if (pos == 2) {
            return getNextPage();
        }
        return null;
    }

    private void changePagePos(int tarPos) {
        if (tarPos < 0) {
            if (havePrev()) {
                tarPos = getPreChapterPageList().size() - 1;
                changeChapterPos(mCurChapterPos - 1);
            }else {
                tarPos = 0;
            }
        } else if (tarPos > getCurChapterPageList().size() - 1) {
            if (haveNext()) {
                tarPos = 0;
                changeChapterPos(mCurChapterPos + 1);
            }else {
                tarPos = getCurChapterPageList().size() - 1;
            }
        }
        mCurPagePos = tarPos;

    }

    private void changeChapterPos(int tarChapterPos) {
        if (tarChapterPos > mChapterList.size() - 1) {
            tarChapterPos = mChapterList.size() - 1;
        } else if (tarChapterPos < 0) {
            tarChapterPos = 0;
        } else {
            if (tarChapterPos == mCurChapterPos) {
                mCurChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos));
                if (mCurChapterPos == 0) {
                    mPreChapterPageList = null;
                } else {
                    mPreChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos - 1));
                }
                if (mCurChapterPos == mChapterList.size() - 1) {
                    mNextChapterPageList = null;
                } else {
                    mNextChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos + 1));
                }
                return;
            } else if (Math.abs(tarChapterPos - mCurChapterPos) > 1) {
                mCurChapterPageList = getChapterPageList(mChapterList.get(tarChapterPos));
                if (tarChapterPos == 0) {
                    mPreChapterPageList = null;
                } else {
                    mPreChapterPageList = getChapterPageList(mChapterList.get(tarChapterPos - 1));
                }
                if (tarChapterPos == mChapterList.size() - 1) {
                    mNextChapterPageList = null;
                } else {
                    mNextChapterPageList = getChapterPageList(mChapterList.get(tarChapterPos + 1));
                }
            } else if (tarChapterPos < mCurChapterPos) {
                    mNextChapterPageList = mCurChapterPageList;
                    mCurChapterPageList = mPreChapterPageList;
                    if (tarChapterPos == 0) {
                        mPreChapterPageList = null;
                    } else {
                        mPreChapterPageList = getChapterPageList(mChapterList.get(tarChapterPos - 1));
                    }
            } else {
                    mPreChapterPageList = mCurChapterPageList;
                    mCurChapterPageList = mNextChapterPageList;
                    if (tarChapterPos == mChapterList.size() - 1) {
                        mNextChapterPageList = null;
                    } else {
                        mNextChapterPageList = getChapterPageList(mChapterList.get(tarChapterPos + 1));
                    }
            }
        }
        mCurChapterPos = tarChapterPos;
    }

    public TxtPage getCurPage() {
        List<TxtPage> curChapterPageList = getCurChapterPageList();
        if (curChapterPageList != null && curChapterPageList.size() > mCurPagePos) {
            return curChapterPageList.get(mCurPagePos);
        }
        return null;
    }

    private TxtPage getPrePage() {
        if (mCurPagePos == 0) {
            List<TxtPage> preChapterPageList = getPreChapterPageList();
            if (preChapterPageList == null || preChapterPageList.isEmpty()) {
                return null;
            }
            return preChapterPageList.get(preChapterPageList.size() - 1);
        }else {
            List<TxtPage> curChapterPageList = getCurChapterPageList();
            if (curChapterPageList != null && curChapterPageList.size() > mCurPagePos - 1) {
                return curChapterPageList.get(mCurPagePos - 1);
            }else {
                return null;
            }
        }
    }
    private TxtPage getNextPage() {
        if (mCurPagePos == getCurChapterPageList().size()-1) {
            List<TxtPage> nextChapterPageList = getNextChapterPageList();
            if (nextChapterPageList == null || nextChapterPageList.isEmpty()) {
                return null;
            }
            return nextChapterPageList.get(0);
        }else {
            List<TxtPage> curChapterPageList = getCurChapterPageList();
            if (curChapterPageList != null && curChapterPageList.size() > mCurPagePos + 1) {
                return curChapterPageList.get(mCurPagePos + 1);
            }else {
                return null;
            }
        }
    }

    boolean havePrev() {
        if (mCurPagePos == 0 && mCurChapterPos == 0) {
            return false;
        }
        return true;
    }

    boolean haveNext() {
        if (mCurPagePos == getCurChapterPageList().size() - 1 && mCurChapterPos == mChapterList.size() - 1) {
            return false;
        }
        return true;
    }

    private List<TxtPage> getPreChapterPageList() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return new ArrayList<>();
        }
        if (mPreChapterPageList==null) {
            if (mCurChapterPos == 0) {
                mPreChapterPageList = null;
            } else {
                mPreChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos - 1));
            }
        }
        return mPreChapterPageList;
    }

    private List<TxtPage> getCurChapterPageList() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return new ArrayList<>();
        }
        if (mCurChapterPageList == null) {
            mCurChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos));
        }
        return mCurChapterPageList;
    }

    private List<TxtPage> getNextChapterPageList() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return new ArrayList<>();
        }
        if (mNextChapterPageList==null) {
            if (mCurChapterPos == mChapterList.size() - 1) {
                mNextChapterPageList = null;
            } else {
                mNextChapterPageList = getChapterPageList(mChapterList.get(mCurChapterPos + 1));
            }
        }
        return mNextChapterPageList;
    }

    private List<TxtPage> getChapterPageList(ChaptersBean chaptersBean) {
        List<TxtPage> pageList = new ArrayList<>();
        if (mData != null) {
            BufferedReader br = mData.getChapterReader(chaptersBean);
            try {
                if (br != null) {
                    pageList = PageDrawManager.getInstance().loadPages(chaptersBean, 0, br, getContext());
                }
                br.close();
            } catch (Exception e) {

            }
        }
        return pageList;
    }


    public int getCurPagePos() {
        return mCurPagePos;
    }

    public int getCurChapterPos() {
        return mCurChapterPos;
    }

    public int getCurPageCharPos() {
        List<TxtPage> curChapterPageList = getCurChapterPageList();
        if (curChapterPageList != null && curChapterPageList.size() > mCurPagePos) {
            TxtPage curPage = curChapterPageList.get(mCurPagePos);
            if (curPage != null) {
                return curPage.getStartCharPos();
            }
        }
        return 0;
    }

    public int getCurPageCharCount() {
        TxtPage curPage = getCurPage();
        if (curPage != null) {
            return curPage.pageCharCount();
        }
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mStartX = x;
            mStartY = y;
            isMove = false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if (!isMove) {
                isMove = Math.abs(mStartX - ev.getX()) > slop || Math.abs(mStartY - ev.getY()) > slop;
            }
            mTouchX = x;
            mTouchY = y;
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {

            mTouchX = 0;
            mTouchY = 0;
            if (!isMove) {
                //设置中间区域范围
                if (mCenterRect == null) {
                    mCenterRect = new RectF(mViewWidth / 4, mViewHeight / 5,
                            mViewWidth * 3 / 4, mViewHeight * 4 / 5);
                }

                //是否点击了中间
                if (mCenterRect.contains(x, y)) {
                    onCenterClick();
                    return true;
                }

                if (isTouchNext(x, y)) {
                    nextPage();
                }else {
                    prePage();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchNext(int x, int y) {

        if (isMove) {
            return x - mStartX < 0;
        }

        boolean isNext = false;
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
        PageDrawManager.getInstance().updateWH(w,h);
        reloadPage();
    }


    private void onCenterClick() {
        if (mTouchListener != null) {
            mTouchListener.onCenterClick();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            if (mReceiver != null){
                getContext().unregisterReceiver(mReceiver);
            }
        }catch (Exception e){
        }
        mReceiver = null;
        PageDrawManager.getInstance().destroy();
        ReadConfigManager.getInstance().destroy();
    }
}
