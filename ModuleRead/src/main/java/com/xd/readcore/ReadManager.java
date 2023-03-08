package com.pw.readcore;

import static com.pw.readcore.utils.Constant.STATUS_FINISH;

import android.content.Context;

import androidx.annotation.Nullable;

import com.pw.base.ad.YYInsertView;
import com.pw.readcore.ReadInterface.NativeAdListener;
import com.pw.readcore.ReadInterface.PageChangeListener;
import com.pw.readcore.bean.BookMarkBean;
import com.pw.readcore.bean.BookRecordBean;
import com.pw.readcore.bean.LayoutMode;
import com.pw.readcore.bean.LineInfo;
import com.pw.readcore.bean.PageMode;
import com.pw.readcore.bean.PageStyle;
import com.pw.readcore.bean.TxtChapter;
import com.pw.readcore.bean.TxtPage;
import com.pw.readcore.manager.ExternalManager;
import com.pw.readcore.manager.PageView;
import com.pw.readcore.manager.ReadDispatch;

import java.util.ArrayList;
import java.util.List;

public class ReadManager {

    public static ReadManager mInstance;

    public static ReadManager getInstance() {
        if (mInstance == null) {
            synchronized (ReadManager.class) {
                if (mInstance == null) {
                    mInstance = new ReadManager();
                }
            }
        }
        return mInstance;
    }

    ReadDispatch mReadDispatch;


    public void init(Context context, ExternalManager externalManager, PageView pageView) {
        mReadDispatch = ReadDispatch.getInstance();
        mReadDispatch.init(context, pageView, externalManager);
    }

    public YYInsertView getCurPageAdView() {
        return mReadDispatch.getCurPageAdView();
    }

    public void openBook() {
        mReadDispatch.openBook();
    }

    public int getCurChapterPos() {
        return mReadDispatch.getChapterPos();
    }

    public int getCurPagePos() {
        return mReadDispatch.getPagePos();
    }

    public @Nullable TxtPage getCurPage() {
        return mReadDispatch.getCurPage();
    }

    public @Nullable TxtChapter getCurChapter() {
        return mReadDispatch.getCurChapter();
    }

    public @Nullable TxtPage getPage(int pagePos) {

        return mReadDispatch.getPage(pagePos);
    }

    public @Nullable TxtChapter getChapter(int chapterPos) {
        return mReadDispatch.getChapter(chapterPos);
    }


    public @Nullable List<TxtChapter> getChapterList() {
        return mReadDispatch.getChapterCategory();
    }

    public List<TxtPage> getCurChapterPageList() {
        List<TxtPage> pageList = mReadDispatch.getCurPageList();
        if (pageList == null) {
            pageList = new ArrayList<>();
        }
        return pageList;
    }

    public void openChapter() {
        mReadDispatch.openChapter();
    }

    public void updateBattrey(int level) {
        mReadDispatch.updatePage(level);
    }
    public void updateTime() {
        mReadDispatch.updateTime();
    }

    public @Nullable BookRecordBean getBookRecord(){
        mReadDispatch.saveRecord();
        return mReadDispatch.getmBookRecord();
    }

    public void updateBookRecord(BookRecordBean record){
        mReadDispatch.updateBookRecord(record);
    }


    public void setAutoReadListener(PageView.AutoReadListener listener) {
        mReadDispatch.setAutoReadListener(listener);
    }

    public boolean isAutoRead() {
        return mReadDispatch.isAutoRead();
    }
    public boolean isAutoReadRunning() {
        return mReadDispatch.isAutoReadRunning();
    }

    public void startAutoRead() {
        mReadDispatch.startAutoRead();
    }

    public void pauseAutoRead() {
        mReadDispatch.pauseAutoRead();
    }

    public void continueAutoRead() {
        mReadDispatch.continueAutoRead();
    }

    public void endAutoRead() {
        mReadDispatch.endAutoRead();
    }

    public void setPageChangeListner(PageChangeListener pageChangeListener) {
        mReadDispatch.setOnPageChangeListener(pageChangeListener);
    }

    public void setInsertViewListner(NativeAdListener nativeAdListener) {
        mReadDispatch.setmNativeAdListener(nativeAdListener);
    }

    public void setPageTouchListener(PageView.TouchListener touchListener) {
        mReadDispatch.setPageTouchListner(touchListener);
    }

    public void setPageTurnPageListener(PageView.TurnPageListener turnPageListener) {
        mReadDispatch.setPageTurningListner(turnPageListener);
    }

    public void setPageMode(PageMode pageMode, boolean isTemporary) {
        mReadDispatch.setPageMode(pageMode, isTemporary);
    }

    public void setPageStyle(PageStyle pageStyle) {
        mReadDispatch.setPageStyle(pageStyle);
    }

    public void setLayoutMode(LayoutMode layoutMode) {
        mReadDispatch.setLayoutMode(layoutMode);
    }

    public void skipToPage(int pagePos) {
        mReadDispatch.skipToPage(pagePos);
    }

    public boolean skipToPrePage() {
        return mReadDispatch.skipToPrePage();
    }

    public boolean skipToNextPage() {
        return mReadDispatch.skipToNextPage();
    }

    public void skipToChapter(int chapterPos) {
        mReadDispatch.skipToChapter(chapterPos);
    }

    public boolean skipToPreChapter() {
        return mReadDispatch.skipToPreChapter();
    }

    public boolean skipToNextChapter() {
        return mReadDispatch.skipToNextChapter();
    }

    public LineInfo.LineAdType getCurPageInsertViewType() {
        return mReadDispatch.getInsertViewType();
    }

    public float getTextHeight() {
        return mReadDispatch.getTextHeight();
    }

    public boolean isSpeeching() {
        return mReadDispatch.isSpeeching();
    }

    public boolean hightlightWithYPos(float guideLineY) {

        return mReadDispatch.hightlightWithYPos(guideLineY);
    }

    public void cancelHighLight() {
        mReadDispatch.cancelHighlight();
    }

    public boolean hightlightWithParaIndex(int paraIndex) {
        return mReadDispatch.drawHighLightBack(paraIndex);
    }

    public String getHighLightContent() {
        return mReadDispatch.getHighLightContent();
    }

    public boolean isContentSpeechAble() {
        int status = getStatus();
        if (status != STATUS_FINISH) {
            return false;
        }
        return true;
    }

    public int getStatus() {
        return mReadDispatch.getmStatus();
    }

    public @Nullable BookMarkBean creatBookMark() {
        return mReadDispatch.creatBookMark();
    }

    public void skipToBookMark(BookMarkBean bookMarkBean) {
        mReadDispatch.jumpToBookMarkPage(bookMarkBean);
    }
    public void refreshChapterList() {
        mReadDispatch.refreshChapterList();
    }

    public void refreshPage() {
        if (mReadDispatch.getChapterCategory() == null
                || mReadDispatch.getChapterCategory().size() <= 0
                || mReadDispatch.getmStatus() != STATUS_FINISH) {
            return;
        }
        mReadDispatch.reloadPage();
    }

    public void chapterError() {
        mReadDispatch.chapterError();
    }

    public void setReadFont(String filePath) {
        mReadDispatch.setFont(filePath);
    }
    public void saveRecord() {
        mReadDispatch.saveRecord();
    }
    public void closeBook() {
        mReadDispatch.closeBook();
    }

    public void clean() {
        mReadDispatch.cleanUp();
    }

    public boolean isLastChapter() {
        if (mReadDispatch != null) {
            return mReadDispatch.isLastChapter();
        }
        return false;
    }

    public void setTextSize(int fontSize) {
        mReadDispatch.setTextSize(fontSize);
    }

    public void updateAdMode(boolean removeAd, boolean needTailInter, boolean chapterMode, int nativeAdCount, int nativeAdMode) {
        if (mReadDispatch != null) {
            mReadDispatch.setAdMode(removeAd, needTailInter, chapterMode, nativeAdCount, nativeAdMode);
        }
    }

    public void setPageBtnExtraPercent(int percent) {
        if (mReadDispatch != null) {
            mReadDispatch.setPageBtnExtraPercent(percent);
        }
    }

    public void setPageBtnNeedAnim(boolean needAnim) {
        if (mReadDispatch != null) {
            mReadDispatch.setPageBtnNeedAnim(needAnim);
        }
    }
}
