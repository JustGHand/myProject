package com.pw.readcore.manager;

import com.pw.readcore.bean.TxtChapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdViewLayoutManager {
    public static AdViewLayoutManager mInstance;

    public static AdViewLayoutManager getInstance(ReadDispatch readDispatch) {
        if (mInstance == null) {
            synchronized (AdViewLayoutManager.class) {
                if (mInstance == null) {
                    mInstance = new AdViewLayoutManager(readDispatch);
                }
            }
        }
        return mInstance;
    }

    private ReadDispatch mReadDispatch;
    public AdViewLayoutManager(ReadDispatch readDispatch) {
        mReadDispatch = readDispatch;
    }

    public void init() {

    }

    public void destroy() {
        mReadDispatch = null;
        mInstance = null;
    }

    public static final int AD_STRATEGY_NATIVE_MODE_UNKNOWN = -1;//广告策略 默认 未知
    public static final int AD_STRATEGY_NATIVE_MODE_INTER = 1;//广告策略 信息流广告模式 ：插页模式
    public static final int AD_STRATEGY_NATIVE_MODE_PAGE = 2;//广告策略 信息流广告模式 ：大页模式
    public static final int AD_STRATEGY_NATIVE_MODE_MIX = 3;//广告策略 信息流广告模式 ：混合模式(章节结束一个大页，章内为插页)
    private boolean mAdRemoveStatus = false;//去广告状态
    private boolean mTailInterStatus = false;//章节末尾填充插页
    private boolean mInterChapterMode = false;//章节模式
    private int mNativeAdCount = 5; // 广告间隔
    private int mNativeMode = AD_STRATEGY_NATIVE_MODE_UNKNOWN;//广告模式

    private Map<Integer, Integer> mAdPageShiftMap; // 已排版章节的剩余未展示广告页面数，用于实现跨章节的广告间隔

    /**
     * 更新广告状态模式
     * @param removeAd 是否去广告
     * @param needTailInter 是否需要章节末尾插页广告填充空白
     * @param chapterMode 是否是章节模式，固定在第二页展示插页广告
     * @param nativeAdCount 广告间隔页数
     * @param nativeAdMode 广告模式：插页模式、大页模式、混合模式（章节末尾大页，其余插页）
     */
    public void setAdStatus(boolean removeAd, boolean needTailInter, boolean chapterMode, int nativeAdCount, int nativeAdMode) {
        mAdRemoveStatus = removeAd;
        mTailInterStatus = needTailInter;
        mInterChapterMode = chapterMode;
        mNativeAdCount = nativeAdCount;
        mNativeMode = nativeAdMode;
    }

    /**
     * 记录章节排版 广告展示信息
     * 章节排版结束调用
     * @param chapterPos
     * @param pageCount
     * @param lastPageIndex
     */
    public void onAdPageLoad(int chapterPos, int pageCount, int lastPageIndex) {
        if (pageCount == 0 || lastPageIndex == -1) {//数据错误或不展示广告，不做记录
            return;
        }
        if (mAdPageShiftMap == null) {
            mAdPageShiftMap = new HashMap<>();
        }
        int pageShift = pageCount - lastPageIndex -1;
        if (pageShift == mNativeAdCount - 1) { // 剩余量等于广告间隔-1，即：下一章第一页需要展示广告，将偏移量前移一页，保证广告出现在第二页
            pageShift = pageShift - 1;
        }
        mAdPageShiftMap.put(chapterPos, pageShift);
    }

    /**
     * 页面变化导致重新排版时重置所有章节记录
     */
    public void clearPageAdData() {
        if (mAdPageShiftMap != null) {
            mAdPageShiftMap.clear();
        }
        mAdPageShiftMap = new HashMap<>();
    }

    /**
     * 是否去广告状态
     * @return
     */
    public boolean isAdRemove() {
        if (mAdRemoveStatus) {
            return true;
        }
        if (mNativeMode == AD_STRATEGY_NATIVE_MODE_UNKNOWN) {
            return true;
        }
        return false;
    }

    /**
     * 是否支持章节末尾填充插页广告
     * @return
     */
    public boolean supportTailInterAd() {
        if (isAdRemove()) {
            return false;
        }
        return mTailInterStatus;
    }

    /**
     * 是否是章节模式，仅展示第二页插页广告
     * @return
     */
    public boolean supportSecondPageInterAd() {
        if (isAdRemove()) {
            return false;
        }
        return mInterChapterMode;
    }

    /**
     * 是否支持插页广告
     * @return
     */
    public boolean supportInterAd() {
        if (isAdRemove()) {
            return false;
        }
        if (supportSecondPageInterAd()) {
            //章节模式需要支持插页广告
            return true;
        }

        if (mNativeMode == AD_STRATEGY_NATIVE_MODE_INTER || mNativeMode == AD_STRATEGY_NATIVE_MODE_MIX) {
            //插页模式和混合模式需要支持插页广告
            return true;
        }
        return false;
    }

    /**
     * 是否需要预留插页广告空间
     * 页面排版是调用
     * @param chapterPos
     * @param pageIndex
     * @param isTailPage
     * @return
     */
    public boolean needReserveInterAd(int chapterPos, int pageIndex, boolean isTailPage) {
        if (!supportInterAd()) {
            return false;
        }
        if (supportSecondPageInterAd()) {//章节模式
            if (pageIndex==1) { //第二页需要
                return true;
            }else {//其余页面不需要
                if (supportTailInterAd() && isTailPage) {//章节尾页填充插页广告
                    return true;
                }else {
                    return false;
                }
            }
        }
        if (supportTailInterAd() && isTailPage) {//章节尾页填充插页广告
            return true;
        }
        if (mNativeAdCount <= 0) {//配置错误
            return false;
        }
        if (pageIndex == 0) {//首页不展示广告
            return false;
        }
        pageIndex++;//传入的index为从0开始计数，修正为从1开始计数
        return needReverseAd(mNativeAdCount, pageIndex, chapterPos);//插页广告直接使用mNativeAdCount作为广告间隔
    }

    /**
     * 是否支持大页广告
     * @return
     */
    public boolean supportPageAd() {
        if (isAdRemove()) {
            return false;
        }
        if (supportSecondPageInterAd()) {
            //章节模式不需要支持插页广告
            return false;
        }
        if (mNativeMode == AD_STRATEGY_NATIVE_MODE_PAGE || mNativeMode == AD_STRATEGY_NATIVE_MODE_MIX) {
            return true;
        }
        return false;
    }

    /**
     * 是否需要预留插页广告空间
     * 页面排版是调用
     * @param chapterPos
     * @param pageIndex
     * @param isTailPage
     * @return
     */
    public boolean needReservePageAd(int chapterPos, int pageIndex, boolean isTailPage) {
        if (!supportPageAd()) {
            return false;
        }
        if (isTailPage){
            return mNativeMode == AD_STRATEGY_NATIVE_MODE_MIX;//混合模式 需要在章节末尾添加大页广告,其他页固定不展示大页
        }
        if (mNativeMode==AD_STRATEGY_NATIVE_MODE_PAGE) {
            if (mNativeAdCount <= 0) {//配置错误
                return false;
            }
            pageIndex++;//传入的index为从0开始计数，修正为从1开始计数
            return needReverseAd(mNativeAdCount, pageIndex, chapterPos);//大页广告使用mNativeAdCount+1作为广告间隔
        }else {
            return false;
        }
    }

    private boolean needReverseAd(int adInterval, int pageIndex, int chapterPos) {
        int pageShift = 0;
        if (mAdPageShiftMap != null) {
            if (chapterPos > 0) {
                Integer shift = mAdPageShiftMap.get(chapterPos - 1);//去前一章节排版剩余的页面数量作为偏移量 , 未取到则不偏移，从头开始
                if (shift != null) {
                    pageShift = shift;
                }else {
                    pageShift = 0;
                }
            }
        }
        pageIndex = pageIndex + pageShift;
        return pageIndex % adInterval == 0;
    }
}
