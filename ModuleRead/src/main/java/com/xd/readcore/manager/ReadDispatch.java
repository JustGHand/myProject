package com.pw.readcore.manager;

import static com.pw.readcore.utils.Constant.DEFAULTCONFIG_AD_PAGEADENTER_Y;
import static com.pw.readcore.utils.Constant.InsertViewType.VIEWTYPE_FULLPAGE;
import static com.pw.readcore.utils.Constant.InsertViewType.VIEWTYPE_INSERT;
import static com.pw.readcore.utils.Constant.InsertViewType.VIEWTYPE_TAIL_PAGEAD;
import static com.pw.readcore.utils.Constant.STATUS_CATEGORY_EMPTY;
import static com.pw.readcore.utils.Constant.STATUS_ERROR;
import static com.pw.readcore.utils.Constant.STATUS_FINISH;
import static com.pw.readcore.utils.Constant.STATUS_LOADING;
import static com.pw.readcore.utils.Constant.STATUS_PARING;
import static com.pw.readcore.utils.Constant.STATUS_PARSE_ERROR;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import androidx.annotation.Nullable;

import com.pw.base.utils.NStringUtils;
import com.pw.base.utils.NetworkUtils;
import com.pw.base.utils.ScreenUtils;
import com.pw.readcore.utils.Constant;
import com.pw.readcore.bean.LayoutMode;
import com.pw.readcore.bean.LineInfo;
import com.pw.readcore.bean.PageMode;
import com.pw.readcore.bean.PageStyle;
import com.pw.readcore.bean.YYAdView;
import com.pw.base.ad.YYFrame;
import com.pw.base.ad.YYInsertView;
import com.pw.readcore.bean.BookMarkBean;
import com.pw.readcore.bean.BookRecordBean;
import com.pw.readcore.bean.TxtChapter;
import com.pw.readcore.bean.TxtPage;
import com.pw.readcore.ReadInterface.NativeAdListener;
import com.pw.readcore.ReadInterface.PageChangeListener;

import java.io.BufferedReader;
import java.util.List;

public class ReadDispatch {

    public static ReadDispatch mInstance;

    public static ReadDispatch getInstance() {
        if (mInstance == null) {
            synchronized (ReadDispatch.class) {
                if (mInstance == null) {
                    mInstance = new ReadDispatch();
                }
            }
        }
        return mInstance;
    }

    public ReadDispatch() {

    }


    // 监听器
    protected PageChangeListener mPageChangeListener;
    protected NativeAdListener mNativeAdListener;

    private Context mContext;

    // 当前的状态
    protected int mStatus = STATUS_LOADING;
    // 判断章节列表是否加载完成
    protected boolean isChapterListPrepare;

    private boolean isFirstOpen = true;
    private boolean isClose;


    private PageDrawManager mPageDrawManager;
    private AdViewLayoutManager mAdViewLayoutManager;
    private PageView mPageView;
    private ReadConfigManager mReadConfigManager;
    private TurningManager mTurningManager;
    private ExternalManager mExternal;

    public void init(Context context, PageView pageView,ExternalManager externalManager) {
        mContext = context;
        mAdViewLayoutManager = AdViewLayoutManager.getInstance(mInstance);
        mPageView = pageView;
        mReadConfigManager = ReadConfigManager.getInstance(mInstance);
        mTurningManager = TurningManager.getInstance(mInstance);
        mExternal = externalManager;
        mAdViewLayoutManager.init();
        mTurningManager.init();
        initPageDraw();

        mReadConfigManager.init();
        mPageView.init(mInstance);


        updateBookRecord(null);
    }

    private void initPageDraw() {
        mPageDrawManager = PageDrawManager.getInstance(mInstance);
        mPageDrawManager.init(mContext);
        mPageDrawManager.setParams(getTextSize(), getLayoutMode(), getFontPath(), getHaveDisplayCutout(), getDisplayCutoutRight());
    }

    protected void drawPage(Bitmap bitmap,Bitmap bgBitmap, boolean isUpdate) {

        mPageDrawManager.drawPage(bitmap,bgBitmap,isUpdate);

        //更新绘制
        mPageView.postInvalidate();
    }

    protected void drawBackInPage(Bitmap bgBitMap, Rect rect) {
        mPageDrawManager.drawBack(bgBitMap, rect);
    }

    protected void cleanPageViewInsert() {
        mPageView.cleanAdView();
    }

    protected boolean drawInsertView(Constant.InsertViewType viewType, Bitmap bitmap, YYInsertView insertView, YYFrame frame) {
        return mPageView.drawInsertView(viewType, bitmap, insertView, frame);
    }

    public YYInsertView getCurPageAdView() {
        return mPageView.getAdView();
    }

    public YYFrame getReloadBtnFram() {
        return mPageDrawManager.getReloadBtnFram();
    }
    public boolean curPageShowReloadBtn() {
        return mPageDrawManager.curPageShowReloadBtn();
    }

    public String getAdRemoveBtnContent() {
        int adRemoveTime = getAdRemoveTime();
        String content = "看视频免广告";
        if (adRemoveTime > 0) {
            content = content + NStringUtils.getTimeStr(adRemoveTime);
        }
        content = content + ">>";
        return content;
    }

    public int getAdRemoveTime() {
        if (mExternal != null) {
            return mExternal.getAdRemoveTime();
        }
        return 0;
    }

    public void setPageBtnExtraPercent(int percent) {
        if (mPageView != null) {
            mPageView.setPageBtnExtraPercent(percent);
        }
    }

    public void setPageBtnNeedAnim(boolean needAnim) {
        if (mPageView != null) {
            mPageView.setNeedPageRemoveAdBtnAnim(needAnim);
        }
    }

    protected Context getContext() {
        return mContext;
    }

    public void setPageMode(PageMode pageMode, boolean isTemporary) {
        if (isTemporary) {
            savePageMode(pageMode);
        }
        Boolean needReDrow = false;
        if (mPageView.getmPageMode() == PageMode.SCROLL || pageMode == PageMode.SCROLL){
            //有scroll就要重新绘制,非scroll之间的动画切换，不重画，提高性能
            needReDrow = true;
        }
        mPageView.setPageMode(pageMode);

        // 重新绘制当前页
        if (needReDrow) {
            mTurningManager.reLoadPage();
            mPageView.drawCurPage(false);
        }
    }

    public PageMode getCurPageMode() {
        return mPageView.getmPageMode();
    }

    public PageMode getSavedPageMode() {
        return mReadConfigManager.getPageMode();
    }

    public void savePageMode(PageMode pageMode) {
        mReadConfigManager.setPageMode(pageMode);
    }

    public void setPageStyle(PageStyle pageStyle) {
        mReadConfigManager.setPageStyle(pageStyle);
        mPageDrawManager.setPageStyle(pageStyle);
        mPageView.drawCurPage(false);
    }

    /**
     * 是否正在自动阅读
     */
    public boolean isAutoRead() {
        return mPageView.isAutoRead();
    }

    public void setAutoReadListener(PageView.AutoReadListener autoReadListener) {
        mPageView.setmAutoReadListener(autoReadListener);
    }

    public boolean isAutoReadRunning() {
        return mPageView.isAutoReadRunning();
    }

    public int getAutoReadSpeed() {
        return mExternal.getAutoReadSpeed();
    }

    public void pauseAutoRead() {
        mPageView.pauseAutoRead();
    }

    public void continueAutoRead() {
        mPageView.continueAutoRead();
    }

    /**
     * 开始自动阅读
     */
    public void startAutoRead() {
        mPageView.setPageMode(PageMode.AUTO);
        mPageView.drawCurPage(false);
        mPageView.startAutoRead();
    }

    /**
     * 关闭自动阅读
     * 恢复原先翻页模式
     */
    public void endAutoRead() {
        mPageView.setPageMode(getSavedPageMode());
        mPageView.drawCurPage(false);
        mPageView.endAutoRead();
    }


    /**
     * 排版模式
     *
     * @param layoutMode:排版模式
     * @see LayoutMode
     */
    public void setLayoutMode(LayoutMode layoutMode){
        mPageDrawManager.setLayoutModeParm(layoutMode);
        // 重设广告 size
        if (mNativeAdListener != null){
            mNativeAdListener.updateNativeAdSize();
        }
        reloadPage();
    }


    /**
     * 设置内容与屏幕的间距
     *
     * @param marginWidth  :单位为 px
     * @param marginHeight :单位为 px
     */
    public void setMargin(int marginWidth, int marginHeight) {

        mPageDrawManager.setMargin(marginWidth, marginHeight);

        // 如果是滑动动画，则需要重新创建了
        if (getCurPageMode() == PageMode.SCROLL) {
            mPageView.setPageMode(PageMode.SCROLL);
        }

        reloadPage();
    }

    /**
     * 设置页面切换监听
     *
     * @param listener
     */
    public void setOnPageChangeListener(PageChangeListener listener) {
        mPageChangeListener = listener;

        // 如果目录加载完之后才设置监听器，那么会默认回调
        if (isChapterListPrepare) {
            mPageChangeListener.onCategoryFinish(mTurningManager.getChapterList());
        }
    }

    public void setmNativeAdListener(@Nullable NativeAdListener mNativeAdListener) {
        this.mNativeAdListener = mNativeAdListener;
    }

    public void setPageTouchListner(PageView.TouchListener touchListener) {
        mPageView.setTouchListener(touchListener);
    }

    public void setPageTurningListner(PageView.TurnPageListener turningListner) {
        mPageView.setmTurnPageListener(turningListner);
    }

    protected boolean hasChapterData(TxtChapter chapter){
        return mExternal.hasChapterData(chapter);
    };

    protected void requestChapters(List<TxtChapter> chapters) {
        if (!chapters.isEmpty()) {
            mPageChangeListener.requestChapters(chapters);
        }
    }

    protected void preloadChapter(List<TxtChapter> chapterList) {

    }

    protected void setStatus(int status) {
        mStatus = status;
    }


    protected boolean isChapterListPrepare() {
        return isChapterListPrepare;
    }

    protected BufferedReader getChapterReader(TxtChapter chapter) throws Exception {
        return mExternal.getChapterReader(chapter);
    }

    public void setAdMode(boolean removeAd, boolean needTailInter, boolean chapterMode, int nativeAdCount, int nativeAdMode) {
        if (mAdViewLayoutManager != null) {
            mAdViewLayoutManager.setAdStatus(removeAd, needTailInter, chapterMode, nativeAdCount, nativeAdMode);
        }
    }

    protected void onAdPageLoad(int chapterPos, int pageCount, int lastAdPageIndex) {
        if (mAdViewLayoutManager != null) {
            mAdViewLayoutManager.onAdPageLoad(chapterPos, pageCount, lastAdPageIndex);
        }
    }

    protected boolean supportInterAd() {
        if (mAdViewLayoutManager != null) {
            return mAdViewLayoutManager.supportInterAd();
        }
        return false;
    }

    protected boolean needReserveInterAd(int chapterPos, int pageIndex, boolean isTailPage) {
        if (mAdViewLayoutManager != null) {
            boolean needReserveInterAd =  mAdViewLayoutManager.needReserveInterAd(chapterPos, pageIndex, isTailPage);
            if (needReserveInterAd) {
                return true;
//                boolean hasInterAd = isInserViewReady(VIEWTYPE_INSERT);
//                return hasInterAd;
            }
        }
        return false;
    }


    protected boolean supportPageAd() {
        if (mAdViewLayoutManager != null) {
            return mAdViewLayoutManager.supportPageAd();
        }
        return false;
    }

    protected boolean needReversePageAd(int chapterPos, int pageIndex, boolean isTailPage) {
        if (mAdViewLayoutManager != null&& NetworkUtils.isAvailable(getContext())) {
            boolean needReserveInterAd =  mAdViewLayoutManager.needReservePageAd(chapterPos, pageIndex, isTailPage);
            if (needReserveInterAd) {
                return true;
            }
        }
        return false;
    }


    protected int getPageAdEnterY() {
        if (mExternal != null) {
            return mExternal.getPageAdEnterY();
        }
        return DEFAULTCONFIG_AD_PAGEADENTER_Y;
    }

    protected List<TxtPage> loadPages(TxtChapter chapter,int chapterPos, BufferedReader br) {
        if (getContext() == null) {
            return null;
        }
        return mPageDrawManager.loadPages(chapter,chapterPos, br, getContext());
    }

    protected void prepareDisplay(int w, int h) {
        mPageDrawManager.updateWH(w, h);
        // 重置 PageMode
        mPageView.setPageMode(getSavedPageMode());

        if (!mTurningManager.isChapterOpen()) {
            // 展示加载界面
            mPageView.drawCurPage(false);
            // 如果在 display 之前调用过 openChapter 肯定是无法打开的。
            // 所以需要通过 display 再重新调用一次。
            if (!isFirstOpen) {
                // 打开书籍
                openChapter();
            }
        } else {
            // 如果章节已显示，那么就重新计算页面
            if (mStatus == STATUS_FINISH) {
                mTurningManager.reLoadPage();
            }
            mPageView.drawCurPage(false);
        }
    }


    protected int getTextSize() {
        return mExternal.getTextSize();
    }

    protected LayoutMode getLayoutMode() {
        return mExternal.getLayoutMode();
    }
    protected boolean getHaveDisplayCutout() {
        return mExternal.getHaveDisplayCutout();
    }
    protected int getDisplayCutoutRight() {
        return mExternal.getDisplayCutoutRight();
    }
    protected String getFontPath() {
        return mExternal.getFontPath();
    }
    protected PageMode getPageMode() {
        return mExternal.getPageMode();
    }

    protected PageStyle getPageStyle() {
        return mExternal.getPageStyle();
    }

    protected void readError() {
        mExternal.readError();
    }

    public boolean bCurPageHaveAd(){
        if (mTurningManager.getmCurPage() == null){
            return false;
        }
        return mTurningManager.getmCurPage().bHaveAd();
    }
    public LineInfo.LineAdType curPageAdType(){
        if (mTurningManager.getmCurPage() == null){
            return LineInfo.LineAdType.LineAdTypeNone;
        }
        return mTurningManager.getmCurPage().getAdType();
    }

    public YYFrame getCurPageAdCloseFrameFromAdFrame(YYFrame adFrame, LineInfo.LineAdType type){
        if (adFrame.isZeroFrame()){
            return adFrame;
        }

        if (type == LineInfo.LineAdType.LineAdTypeInterNative){
            //缩小点击范围，提高点击率
            int margin = ScreenUtils.dpToPx(getContext(),8);
            int closeWidth = ScreenUtils.dpToPx(getContext(),51-4);
            int closeHeight = ScreenUtils.dpToPx(getContext(),17-2);
            int closeX = adFrame.getX()+adFrame.getWidth()-closeWidth-margin;
            int closeY = adFrame.getY()+margin;
            return new YYFrame(closeX,closeY,closeWidth,closeHeight);
        }else if (type == LineInfo.LineAdType.LineAdTypeFullPage){
            //大页广告关闭区域
            int closeHeight = ScreenUtils.dpToPx(getContext(),25);
            return new YYFrame(adFrame.getX(), adFrame.getY()+adFrame.getHeight()-closeHeight, adFrame.getWidth(), closeHeight);
        }
        return YYFrame.YYFrameZero();
    }
    protected YYFrame getCurPageAdFrame() {
        return mTurningManager.getmCurPage().getAdFrame();
    }
    public LineInfo.LineAdType getInsertViewType() {
        return mTurningManager.getInsertViewType();
    }

    public BookMarkBean creatBookMark() {
        return mTurningManager.creatBookMark();
    }
    public int getCurPageStartCharPos() {
        return mTurningManager.getCurPageStartCharPos();
    }

    public int getCurPageCharCount() {
        return mTurningManager.getCurPageCharCount();
    }

    public int getPageStartPos(int pagePos) {
        return mTurningManager.getPageStartPos(pagePos);
    }

    public int getPageCharCount(int pagePos) {
        return mTurningManager.getPageCharCount(pagePos);
    }

    public void jumpToBookMarkPage(BookMarkBean bookMarkBean){

        // 打开指定章节
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mTurningManager.getChapterList().isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        mTurningManager.loadBookMark(bookMarkBean);

        mPageView.drawCurPage(false);
    }

    public float getTextHeight() {
        return mPageDrawManager.getTextHeight();
    }

    public TxtPage getCurPage() {
        return mTurningManager.getmCurPage();
    }

    public boolean isCurPageHasAd() {
        if (getCurPage() != null) {
            return getCurPage().bHaveAd();
        }
        return false;
    }

    public LineInfo.LineAdType getCurPageAdType() {
        if (getCurPage() != null) {
            return getCurPage().getAdType();
        }
        return LineInfo.LineAdType.LineAdTypeNone;
    }

    public List<TxtPage> getCurPageList() {
        List<TxtPage> pageList = mTurningManager.getmCurPageList();
        return pageList;
    }

    public TxtPage getPage(int pagePos) {
        List<TxtPage> pageList = getCurPageList();
        if (pageList != null && pagePos < pageList.size()) {
            return pageList.get(pagePos);
        }
        return null;
    }

    public TxtChapter getCurChapter() {
        return mTurningManager.getCurChapter();
    }
    public TxtChapter getChapter(int chapterPos) {
        List<TxtChapter> chapterList = mTurningManager.getChapterList();
        if (chapterList != null && chapterPos < chapterList.size()) {
            return chapterList.get(chapterPos);
        }
        return null;
    }

    public String getCurChapterTitle() {
        return mTurningManager.getCurChapterTitle();
    }
//
//    protected boolean supportAd(Constant.InsertViewType viewType) {
//        boolean support = false;
//        if (mNativeAdListener != null) {
//            switch (viewType) {
//                case VIEWTYPE_INSERT:
//                    support = mNativeAdListener.isSupportNativeAd();
//                    break;
//                case VIEWTYPE_FULLPAGE:
//                    support = mNativeAdListener.isSupportPageAd();
//                    break;
//                default:
//                    break;
//            }
//        }
//        return support;
//    }

    protected boolean isInserViewReady(Constant.InsertViewType viewType) {

        boolean isReady = false;

        if (mNativeAdListener != null) {
            switch (viewType) {
                case VIEWTYPE_INSERT:
                    isReady = mNativeAdListener.isHaveNativeAd();
                    break;
                case VIEWTYPE_TAIL_PAGEAD:
                    isReady = mNativeAdListener.isHavePageAd();
                    break;
                case VIEWTYPE_FULLPAGE:
                    isReady = mNativeAdListener.isHavePageAd();
                    break;
                default:
                    break;
            }
        }
        return isReady;
    }

    protected boolean prev() {
        return mTurningManager.prev();
    }

    protected boolean next() {
        return mTurningManager.next();
    }

    protected void pageCancel() {
        mTurningManager.pageCancel();
    }

    protected YYInsertView getInsertView(Constant.InsertViewType viewType, YYAdView adView, YYFrame frame) {
        YYInsertView insertView = null;
        if (mNativeAdListener != null) {
            switch (viewType) {
                case VIEWTYPE_INSERT:
                    insertView = mNativeAdListener.getInterAd(adView, frame);
                    break;
                case VIEWTYPE_FULLPAGE:
                    insertView = mNativeAdListener.getPageAd(adView, frame);
                    break;
                default:
                    break;
            }
        }
        return insertView;
    }

    protected void drawNextPage() {
        mPageView.drawNextPage();
    }

    protected void onReadParaChanged(int highlightPara) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onReadParaChanged(highlightPara);
        }
    }
    protected void onPageChange(int pos,int wordcount,int chapterPos,int pageWordCount) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChange(pos, wordcount, chapterPos, pageWordCount);
        }
    }
    protected void onPageChangeFinish(int pos,boolean success) {
        if (mPageChangeListener != null) {
            mPageChangeListener.onPageChangeFinish(pos, success);
        }
    }
    protected void onChapterChange(int pos,int wordCount) {
        mPageChangeListener.onChapterChange(pos, wordCount);
    }

    protected void onChapterChangeByRead(int pos, int wordCount, String chapterId,boolean isNext) {
        mPageChangeListener.onChapterChangeByRead(pos, wordCount,chapterId,isNext);
    }

    protected void onPageCountChange(int count) {
        mPageChangeListener.onPageCountChange(count);
    }

    protected void onBookReadFinish() {
        if (mPageChangeListener != null) {
            mPageChangeListener.onReadFinish();
        }
    }

    protected void pauseAd() {
        if (mNativeAdListener != null) {
            mNativeAdListener.pauseAd();
        }
    }

    protected boolean isIntertPosRandom() {
        return true;
    }

    public int getmStatus() {
        return mStatus;
    }

    public void setmStatus(int mStatus) {
        this.mStatus = mStatus;
    }

    public boolean hightlightWithYPos(float guideLineY) {
        return syncHighLight( guideLineY);
    }

    protected void redrawPage() {
        if (mPageView != null) {
            mPageView.drawCurPage(false);
        }
    }

    public boolean syncHighLight(float guideLineY) {
        TxtPage curPage = mTurningManager.getmCurPage();
        if (curPage == null) {
            return false;
        }
        boolean needSyncBack = false;

        int currentSpeechParaIndex = curPage.getSpeechingPara();

        if (currentSpeechParaIndex == -1) {
            return false;
        }

        float currentSpeechStartY = curPage.getPara(currentSpeechParaIndex).getStartY();
        float currentSpeechEndY = curPage.getPara(currentSpeechParaIndex).getEndY();

        if (guideLineY >= currentSpeechStartY && guideLineY <= currentSpeechEndY) {
            needSyncBack = false;
        } else if (guideLineY < currentSpeechStartY) {
            needSyncBack = true;
            currentSpeechParaIndex--;
        } else if (guideLineY > currentSpeechEndY) {
            needSyncBack = true;
            currentSpeechParaIndex++;
        }

        if (needSyncBack) {
            onReadParaChanged(currentSpeechParaIndex);
            curPage.setSpeechingPara(currentSpeechParaIndex);
            redrawPage();
        }

        return needSyncBack;
    }

    public boolean isSpeeching() {
        return mPageView.getIsBookSpeeching();
    }

    public boolean cancelHighlight() {
        drawHighLightBack(-1);
        return true;
    }

    public boolean drawHighLightBack(int currentSpeechPara) {
        TxtPage curPage = mTurningManager.getmCurPage();
        if (curPage == null) {
            return false;
        }
        mPageView.setIsBookSpeeching(currentSpeechPara != -1);
        curPage.setSpeechingPara(currentSpeechPara);
        redrawPage();
        return true;
    }

    public String getHighLightContent() {
        if (mTurningManager.getmCurPage() == null) {
            return "";
        }
        return mTurningManager.getmCurPage().getHighLightContent();
    }

    /**
     * 根据当前状态，决定是否能够翻页
     *
     * @return
     */
    protected boolean canTurnPage() {

        if (!isChapterListPrepare) {
            return false;
        }

        if (mStatus == STATUS_PARSE_ERROR
                || mStatus == STATUS_PARING) {
            return false;
        } else if (mStatus == STATUS_ERROR) {
            mStatus = STATUS_LOADING;
        }
        return true;
    }

    public void setFont(String fontPath) {
        mPageDrawManager.setReadFont(fontPath);
        reloadPage();
    }

    public void reloadPage() {
        saveRecord();
        if (isChapterListPrepare && mStatus == STATUS_FINISH) {
            boolean reloadSuccess = mTurningManager.reLoadPage();
            if (!reloadSuccess) {
                mStatus = STATUS_ERROR;
            }
        }
        if (mAdViewLayoutManager != null) {
            mAdViewLayoutManager.clearPageAdData();
        }
        mPageView.drawCurPage(false);
    }

    public void skipToPage(int pagePos) {
        mTurningManager.skipToPage(pagePos);
    }

    public boolean skipToPrePage() {
        if (mPageView!=null) {
            return mPageView.autoPrevPage();
        }
        return false;
    }

    public boolean skipToNextPage() {
        return mTurningManager.next();
    }

    public void skipToChapter(int chapterPos) {
        mTurningManager.skipToChapter(chapterPos);
    }

    public boolean skipToPreChapter() {
        return mTurningManager.skipPreChapter();
    }

    public boolean skipToNextChapter() {
        return mTurningManager.skipNextChapter();
    }

    public void refreshChapterList() {
        List<TxtChapter> chapterList = (List<TxtChapter>)mExternal.getChapterList();

        mTurningManager.setChapterList(chapterList);

        isChapterListPrepare = true;

        // 目录加载完成，执行回调操作。
        if (mPageChangeListener != null) {
            mPageChangeListener.onCategoryFinish(chapterList);
        }

        // 如果章节未打开
        if (!isChapterOpen()) {
            // 打开章节
            openChapter();
        }
    }

    /**
     * 获取章节目录。
     *
     * @return
     */
    public List<TxtChapter> getChapterCategory() {
        return mTurningManager.getChapterList();
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        return mTurningManager.getPagePos();
    }

    /**
     * 获取当前章节的页数
     * @return
     */
    public int getCurChapterPageCount() {
        return mTurningManager.getCurChapterPageCount();
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mTurningManager.getChapterPos();
    }

    /**
     * 获取距离屏幕的高度
     *
     * @return
     */
    public int getMarginHeight() {
        return mPageDrawManager.getmMarginHeight();
    }


    /**
     * 设置文字相关参数
     *
     * @param textSize
     */
    public void setTextSize(int textSize) {
        // 设置文字相关参数
        mPageDrawManager.setUpTextParams(textSize);
        reloadPage();
    }

    BookRecordBean mBookRecord;
    /**
     * 保存阅读记录
     */
    public void saveRecord() {

        BookRecordBean bookRecordBean = mTurningManager.creatBookRecord();
        if (bookRecordBean != null) {
            mBookRecord = bookRecordBean;
        }
        mExternal.saveBookRecord(mBookRecord);
    }

    public BookRecordBean getmBookRecord() {
        return mBookRecord;
    }

    public int getRecordCharPos() {
        if (mBookRecord != null) {
            return mBookRecord.getCharPos();
        }
        return 0;
    }

    /*
     *  设置书签
     */
    public void  updateBookRecord(BookRecordBean record){
        if (record == null) {
            mBookRecord = mExternal.getBookRecord();
        }else {
            mBookRecord = record;
        }

        if (mBookRecord == null) {
            mBookRecord = new BookRecordBean();
        }

        mTurningManager.loadBookRecord(mBookRecord);
    }

    public void openBook() {
        mPageDrawManager.setPageStyle(mExternal.getPageStyle());
        prepareDisplay(mPageView.getmViewWidth(), mPageView.getmViewHeight());
    }

    public void updatePage(int level) {
        mPageDrawManager.setmBatteryLevel(level);
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    public void updateTime() {
        if (!mPageView.isRunning()) {
            mPageView.drawCurPage(true);
        }
    }

    public boolean isLastChapter() {
        if (mTurningManager != null) {
            return mTurningManager.isLastChapter();
        }
        return false;
    }

    /**
     * 打开指定章节
     */
    public void openChapter() {
        isFirstOpen = false;

        if (!mPageView.isPrepare()) {
            return;
        }

        // 如果章节目录没有准备好
        if (!isChapterListPrepare) {
            mStatus = STATUS_LOADING;
            mPageView.drawCurPage(false);
            return;
        }

        // 如果获取到的章节目录为空
        if (mTurningManager.getChapterList().isEmpty()) {
            mStatus = STATUS_CATEGORY_EMPTY;
            mPageView.drawCurPage(false);
            return;
        }

        mTurningManager.openChapter();

        mPageView.drawCurPage(false);
    }

    public void chapterError() {
        //加载错误
        mStatus = STATUS_ERROR;
        mPageView.drawCurPage(false);
    }

    public void closeBook() {
        isChapterListPrepare = false;
        isClose = true;
        destroy();
    }

    public boolean isClose() {
        return isClose;
    }

    public boolean isChapterOpen() {
        return mTurningManager.isChapterOpen();
    }

    public void cleanUp() {
        isChapterListPrepare = false;
        mTurningManager.clean();
        mBookRecord.setPagePos(0);
        mBookRecord.setCharPos(0);
    }

    public void destroy() {
        mContext = null;
        mPageView = null;
        if (mNativeAdListener!=null) {
            mNativeAdListener = null;
        }
        if (mPageChangeListener!=null) {
            mPageChangeListener = null;
        }
        if (mPageDrawManager!=null) {
            mPageDrawManager.destroy();
            mPageDrawManager = null;
        }
        if (mAdViewLayoutManager !=null) {
            mAdViewLayoutManager.destroy();
            mAdViewLayoutManager = null;
        }
        if (mReadConfigManager!=null) {
            mReadConfigManager.destroy();
            mReadConfigManager = null;
        }
        if (mTurningManager!=null) {
            mTurningManager.clean();
            mTurningManager.destroy();
            mTurningManager = null;
        }
//        mInsertViewManager.destroy();
//        mReadConfigManager.destroy();
//        mTurningManager.clean();
//        mTurningManager.destroy();

        mPageDrawManager = null;
        mAdViewLayoutManager = null;
        mReadConfigManager = null;
        mTurningManager = null;

        mInstance = null;
    }
}
