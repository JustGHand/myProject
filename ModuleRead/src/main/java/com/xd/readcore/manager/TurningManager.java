package com.xd.readcore.manager;

import static com.xd.readcore.utils.Constant.STATUS_EMPTY;
import static com.xd.readcore.utils.Constant.STATUS_ERROR;
import static com.xd.readcore.utils.Constant.STATUS_FINISH;
import static com.xd.readcore.utils.Constant.STATUS_LOADING;
import static com.xd.readcore.utils.Utils.clearList;

import com.xd.base.utils.NStringUtils;
import com.xd.base.utils.RxUtils;
import com.xd.readcore.bean.LineInfo;
import com.xd.readcore.bean.BookMarkBean;
import com.xd.readcore.bean.BookRecordBean;
import com.xd.readcore.bean.PageMode;
import com.xd.readcore.bean.TxtChapter;
import com.xd.readcore.bean.TxtPage;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;

public class TurningManager {

    public static TurningManager mInstance;

    public static TurningManager getInstance(ReadDispatch readDispatch) {
        if (mInstance == null) {
            synchronized (TurningManager.class) {
                if (mInstance == null) {
                    mInstance = new TurningManager(readDispatch);
                }
            }
        }
        return mInstance;
    }

    private ReadDispatch mReadDispatch;

    public TurningManager(ReadDispatch readDispatch) {
        mReadDispatch = readDispatch;
    }

    // 当前章节列表
    protected List<TxtChapter> mChapterList;
    // 当前显示的页
    public TxtPage mCurPage;  //当数据没加载出来时，有布局，mCurPage为空
    // 上一章的页面列表缓存
    private List<TxtPage> mPrePageList;
    // 当前章节的页面列表
    private List<TxtPage> mCurPageList;
    // 下一章的页面列表缓存
    private List<TxtPage> mNextPageList;
    // 被遮盖的页，或者认为被取消显示的页
    private TxtPage mCancelPage;
    // 当前章
    protected int mCurChapterPos = 0;
    //上一章的记录
    private int mLastChapterPos = 0;

    private Disposable mPreLoadDisp;
    // 是否打开过章节
    private boolean isChapterOpen;

    public void init() {

    }

    public int getCurPageStartCharPos() {
        if ( mCurPage != null) {
            List<LineInfo> lineInfos = mCurPage.lineInfos;
            if (lineInfos != null && !lineInfos.isEmpty()) {
                LineInfo lineInfo = lineInfos.get(0);
                if (lineInfo != null) {
                    return lineInfo.getmStartPos();
                }
            }
        }
        return -1;
    }

    public int getCurPageCharCount() {
        if ( mCurPageList != null) {
            return mCurPage.pageCharCount();
        }
        return -1;
    }

    public int getPageStartPos(int pagePos) {
        if (mCurPageList != null) {
            TxtPage tarPage = mCurPageList.get(pagePos);
            if (tarPage != null) {
                List<LineInfo> lineInfos = tarPage.lineInfos;
                if (lineInfos != null && !lineInfos.isEmpty()) {
                    LineInfo lineInfo = lineInfos.get(0);
                    if (lineInfo != null) {
                        return lineInfo.getmStartPos();
                    }
                }
            }
        }
        return -1;
    }

    public int getPageCharCount(int pagePos) {
        if (mCurPageList != null) {
            TxtPage tarPage = mCurPageList.get(pagePos);
            if (tarPage != null) {
                return tarPage.pageCharCount();
            }
        }
        return -1;
    }

    protected BookMarkBean creatBookMark() {
        if (mCurPage == null || mCurPage.lineInfos == null || mChapterList.isEmpty() || mCurPage.lineInfos.isEmpty()) {
            return null;
        }

        BookMarkBean bookMarkBean = new BookMarkBean();

        bookMarkBean.setChapter(mCurChapterPos);
        bookMarkBean.setCharPos(mCurPage.lineInfos.get(0).getmStartPos());

        if (mCurPage != null) {
            bookMarkBean.setPagePos(mCurPage.position);
        } else {
            bookMarkBean.setPagePos(0);
        }

        if (mCurChapterPos >= 0 && mCurChapterPos < mChapterList.size()){
            String chapterTitle = mChapterList.get(mCurChapterPos).getTitle();
            bookMarkBean.setChapterTitle(chapterTitle);
        }

        bookMarkBean.setMarkSimpleText(mCurPage.getMarkDesc());


        String markCreatTime = NStringUtils.dateConvert(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss");
        bookMarkBean.setMarkCreatTime(markCreatTime);

        return bookMarkBean;
    }
    //跳转到书签页面
    public void loadBookMark(BookMarkBean bookMarkBean) {

        // 设置参数
        mCurChapterPos = bookMarkBean.getChapter();

        // 将上一章的缓存设置为null
        mPrePageList = null;
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // 将下一章缓存设置为null
        mNextPageList = null;


        if (parseCurChapter()) {
            int position = getPagePosWithCharPos(bookMarkBean.getCharPos());
//                int position = mBookRecord.getPagePos();

            if (mCurPageList!=null) {
                // 防止记录页的页号，大于当前最大页号
                if (position >= mCurPageList.size()) {
                    position = mCurPageList.size() - 1;
                }
                mCurPage = getCurPage(position);
                mCancelPage = mCurPage;
                // 切换状态
                isChapterOpen = true;
            }else {
                mCurPage = new TxtPage();
            }
        } else {
            mCurPage = new TxtPage();
        }

    }

    protected BookRecordBean creatBookRecord() {

        if (mChapterList == null || mChapterList.size() <= 0 || mCurPage == null) {
            return null;
        }

        BookRecordBean mBookRecord = new BookRecordBean();
        mBookRecord.setChapter(mCurChapterPos);
        TxtPage page = mCurPage;
        if (mCurPage.position > 0
                && mCurPage.findAdLine() != null
                && mCurPage.findAdLine().getLineAdType() == LineInfo.LineAdType.LineAdTypeFullPage) {
            page = getmCurPageList().get(mCurPage.position - 1);
            if (page == null) {
                page = mCurPage;
            }
        }
        if (page == null || page.lineInfos == null || page.lineInfos.size() <= 0){
            mBookRecord.setCharPos(0);
        }else {
            mBookRecord.setCharPos(page.lineInfos.get(0).getmStartPos());
        }

        if (page != null) {

            mBookRecord.setPagePos(page.position);

        } else {
            mBookRecord.setPagePos(0);
        }
        return mBookRecord;
    }

    protected void loadBookRecord(BookRecordBean bookRecordBean) {
        if (bookRecordBean == null) {
            return;
        }
        mCurChapterPos = bookRecordBean.getChapter();
        mLastChapterPos = mCurChapterPos;
    }

    protected void setChapterList(List<TxtChapter> chapterList) {
        mChapterList = chapterList;
    }

    protected List<TxtChapter> getChapterList() {
        if (mChapterList == null) {
            mChapterList = new ArrayList<>();
        }
        return mChapterList;
    }
    protected List<TxtPage> getmCurPageList() {
        return mCurPageList;
    }

    protected TxtChapter getCurChapter() {
        if (mChapterList == null || mChapterList.size() <= 0) {
            return null;
        }
        if (mCurChapterPos >= mChapterList.size()) {
            int pos = mChapterList.size()-1;
            return mChapterList.get(pos);
        }
        return mChapterList.get(mCurChapterPos);
    }

    protected boolean isLastChapter() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return true;
        }
        return (mChapterList.size() - 1) == mCurChapterPos;
    }

    public String getCurChapterTitle() {

        if (mChapterList != null && mChapterList.size() > mCurChapterPos) {
            TxtChapter chapter = mChapterList.get(mCurChapterPos);
            if (chapter != null) {
                return chapter.getTitle();
            }
        }
        return "";
    }

    /**
     * 获取当前章节的页数
     * @return
     */
    public int getCurChapterPageCount() {
        if (mCurPageList == null) {
            return 0;
        }
        return mCurPageList.size();
    }

    /**
     * 获取当前章节的章节位置
     *
     * @return
     */
    public int getChapterPos() {
        return mCurChapterPos;
    }


    protected boolean isChapterOpen() {
        return isChapterOpen;
    }

    protected void openChapter(){

        if (parseCurChapter()) {
            // 如果章节从未打开
            if (!isChapterOpen) {
                int position = getPagePosWithCharPos(mReadDispatch.getRecordCharPos());
//                int position = mBookRecord.getPagePos();

                if (mCurPageList!=null&&!mCurPageList.isEmpty()) {
                    // 防止记录页的页号，大于当前最大页号
                    if (position >= mCurPageList.size()) {
                        position = mCurPageList.size() - 1;
                    }
                    mCurPage = getCurPage(position);
                    mCancelPage = mCurPage;
                    // 切换状态
                    isChapterOpen = true;
                }else {
                    mCurPage = new TxtPage();
                }
            } else {
                mCurPage = getCurPage(0);
            }
        } else {
            mCurPage = new TxtPage();
        }
    }

    protected boolean reLoadPage() {
        mReadDispatch.saveRecord();
        mPrePageList = null;
        mNextPageList = null;

        dealLoadPageList(mCurChapterPos);
        if (mCurPageList == null){
            //非正常情况
            return false;
        }
        int position = getPagePosWithCharPos(mReadDispatch.getRecordCharPos());
        // 重新设置文章指针的位置
        mCurPage = getCurPage(position);
        return true;
    }

    protected TxtPage getmCurPage() {
        if (mCurPage == null) {
            return null;
        }
        return mCurPage;
    }

    protected int getPagePosWithCharPos(int chapterPos){
        if (chapterPos < 0 || mChapterList == null || mCurPageList == null) {
            return 0;
        }
        for (int i = 0; i < mCurPageList.size(); i++) {
            TxtPage page = mCurPageList.get(i);
            if (page!=null) {
                List<LineInfo> lineInfos = page.lineInfos;
                if (lineInfos!=null) {
                    int lineCount = lineInfos.size();
                    LineInfo lastLine = lineInfos.get(lineCount - 1);
                    if (lastLine!=null) {
                        if (chapterPos < lastLine.getmStartPos() + lastLine.getmCharCount()) {
                            return i;
                        }
                    }
                }
            }
        }
        return mCurPageList.size()-1;
    }
    /**
     * 跳转到上一章
     *
     * @return
     */
    public boolean skipPreChapter() {
        if (!hasPrevChapter()) {
            return false;
        }
        // 载入上一章。
        if (parsePrevChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mReadDispatch.redrawPage();
        return true;
    }

    /**
     * 跳转到下一章
     *
     * @return
     */
    public boolean skipNextChapter() {
        if (!hasNextChapter()) {
            return false;
        }

        //判断是否达到章节的终止点
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
        } else {
            mCurPage = new TxtPage();
        }
        mReadDispatch.redrawPage();
        return true;
    }

    /**
     * 跳转到指定章节
     *
     * @param pos:从 0 开始。
     */
    public void skipToChapter(int pos) {
        // 设置参数
        mCurChapterPos = pos;

        // 将上一章的缓存设置为null
        mPrePageList = null;
        // 如果当前下一章缓存正在执行，则取消
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        // 将下一章缓存设置为null
        mNextPageList = null;

        // 打开指定章节
        openChapter();
        mReadDispatch.redrawPage();
    }

    /**
     * 跳转到指定的页
     *
     * @param pos
     */
    public boolean skipToPage(int pos) {
        if (!mReadDispatch.isChapterListPrepare()) {
            return false;
        }
        mCurPage = getCurPage(pos);
        mReadDispatch.redrawPage();
        return true;
    }

    /**
     * 翻阅上一页
     *
     * @return
     */
    boolean prev() {
        // 以下情况禁止翻页
        if (!mReadDispatch.canTurnPage()) {
            return false;
        }

        if (mReadDispatch.getmStatus() == STATUS_FINISH) {
            // 先查看是否存在上一页
            TxtPage prevPage = getPrevPage();
            if (prevPage != null) {
                mCancelPage = mCurPage;
                mCurPage = prevPage;
                if (mReadDispatch.isSpeeching()) {
                    mCurPage.setSpeechingParaToLastPara();
                    mReadDispatch.onReadParaChanged(mCurPage.getSpeechingPara());
                }
                mReadDispatch.drawNextPage();
                mReadDispatch.onPageChangeFinish(getPagePos(),true);
                return true;
            }
        }

        if (!hasPrevChapter()) {
            mReadDispatch.onPageChangeFinish(getPagePos(),true);
            return false;
        }

        NextChapterByPageChange(false);
        mCancelPage = mCurPage;
        if (parsePrevChapter()) {
            mCurPage = getPrevLastPage();
        } else {
            mCurPage = new TxtPage();
        }

        if (mReadDispatch.isSpeeching()) {
            mCurPage.setSpeechingParaToLastPara();
            mReadDispatch.onReadParaChanged(mCurPage.getSpeechingPara());
        }

        if (mReadDispatch.getPageMode() == PageMode.SCROLL && mReadDispatch.getmStatus() != STATUS_FINISH) {
            mReadDispatch.setStatus(STATUS_FINISH);
            cancelPreChapter();
            return false;
        }
        mReadDispatch.drawNextPage();
        mReadDispatch.onPageChangeFinish(getPagePos(),true);
        return true;
    }

    /**
     * 翻到下一页
     *
     * @return:是否允许翻页
     */
    boolean next() {
        // 以下情况禁止翻页
        if (!mReadDispatch.canTurnPage()) {
            return false;
        }

        if (mReadDispatch.getmStatus() == STATUS_FINISH) {
            // 先查看是否存在下一页
            TxtPage nextPage = getNextPage();
            if (nextPage != null) {
                mCancelPage = mCurPage;
                mCurPage = nextPage;
                if (mReadDispatch.isSpeeching()) {
                    mCurPage.setSpeechingParaToFirstPara();
                    mReadDispatch.onReadParaChanged(0);
                }
                mReadDispatch.drawNextPage();
                mReadDispatch.onPageChangeFinish(getPagePos(),true);
                return true;
            }
        }

        if (!hasNextChapter()) {
            if (mReadDispatch != null) {
                mReadDispatch.onBookReadFinish();
            }
            mReadDispatch.onPageChangeFinish(getPagePos(),true);
            return false;
        }

        NextChapterByPageChange(true);

        mCancelPage = mCurPage;
        // 解析下一章数据
        if (parseNextChapter()) {
            mCurPage = getCurPage(0);
//            mCurPage = mCurPageList.get(0);
        } else {
            mCurPage = new TxtPage();
        }

        if (mReadDispatch.isSpeeching()) {
            mCurPage.setSpeechingParaToFirstPara();
            mReadDispatch.onReadParaChanged(0);
        }
        if (mReadDispatch.getPageMode() == PageMode.SCROLL && mReadDispatch.getmStatus() != STATUS_FINISH) {
            mReadDispatch.setStatus(STATUS_FINISH);
            cancelNextChapter();
            return false;
        }
        mReadDispatch.drawNextPage();
        mReadDispatch.onPageChangeFinish(getPagePos(), true);
        return true;
    }

    // 取消翻页
    void pageCancel() {
        if (mCurPage == null) {
            mCurPage = new TxtPage();
        }
        if (mCurPage.position == 0 //当前页为第一页
                &&(mCurPageList != null &&mCancelPage!=null &&!mCurPageList.contains(mCancelPage)) //取消页不在当前章节
                && mCurChapterPos > mLastChapterPos) { // 加载到下一章取消了
            if (mPrePageList != null) {
                cancelNextChapter();
            } else {
                if (parsePrevChapter()) {
                    mCurPage = getPrevLastPage();
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else if (mCurPageList == null
                || (mCurPage.position == mCurPageList.size() - 1
                && (mCurPageList != null && mCancelPage != null && !mCurPageList.contains(mCancelPage))
                && mCurChapterPos < mLastChapterPos)) {  // 加载上一章取消了

            if (mNextPageList != null) {
                cancelPreChapter();
            } else {
                if (parseNextChapter()) {
                    mCurPage = getCurPage(0);
//                    mCurPage = mCurPageList.get(0);
                } else {
                    mCurPage = new TxtPage();
                }
            }
        } else {
            // 假设加载到下一页，又取消了。那么需要重新装载。
            mCurPage = mCancelPage;
        }
    }

    private void cancelNextChapter() {
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;

        mNextPageList = mCurPageList;
        mCurPageList = mPrePageList;
        mPrePageList = null;

        ChapterChangeCallBack();

        mCurPage = getPrevLastPage();
        mCancelPage = null;
    }

    private void cancelPreChapter() {
        // 重置位置点
        int temp = mLastChapterPos;
        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = temp;
        // 重置页面列表
        mPrePageList = mCurPageList;
        mCurPageList = mNextPageList;
        mNextPageList = null;

        ChapterChangeCallBack();

        mCurPage = getCurPage(0);
        mCancelPage = null;
    }

    private void ChapterChangeCallBack() {
        if (mChapterList != null && mCurChapterPos < mChapterList.size() && mLastChapterPos < mChapterList.size() && mLastChapterPos >= 0 && mCurChapterPos >= 0) {
            try {
                int wordcount = mChapterList.get(mLastChapterPos).getTextCount();
                mReadDispatch.onChapterChange(mCurChapterPos, wordcount);
                mReadDispatch.onPageCountChange(mCurPageList != null ? mCurPageList.size() : 0);
            } catch (Exception e) {

            }
        }
    }

    //通过阅读（翻页）进入上一章\下一章
    //isNext:true 下一章；false 上一章
    private void NextChapterByPageChange(boolean isNext) {
        try {
            if (mChapterList != null && mCurChapterPos < mChapterList.size() && mCurChapterPos < mChapterList.size() && mCurChapterPos >= 0) {
                int wordCount = mChapterList.get(mCurChapterPos).getTextCount();
                String chapterId = mChapterList.get(mCurChapterPos).getChapterId();
                mReadDispatch.onChapterChangeByRead(mCurChapterPos, wordCount, chapterId, isNext);
            }
        } catch (Exception e) {

        }
    }

    /**
     * @return:获取上一个页面
     */
    private TxtPage getPrevPage() {
        if (mCurPage == null) {
            mCurPage = new TxtPage();
        }
        int pos = mCurPage.position - 1;
        if (pos < 0) {
            return null;
        }
        if (mCurPageList == null || mCurPageList.size() <= pos + 1) {
            return null;
        }
        if (mCurPageList.get(pos) != null && mCurPageList.get(pos + 1) != null) {
            mReadDispatch.onPageChange(pos, mCurPageList.get(pos).pageCharCount(), mCurChapterPos, mCurPageList.get(pos + 1).pageCharCount());
        }
        return mCurPageList.get(pos);
    }
    /**
     * @return:获取上一个章节的最后一页
     */
    private TxtPage getPrevLastPage() {
        if (mCurPageList != null && !mCurPageList.isEmpty()) {
            int pos = mCurPageList.size() - 1;
            if (mCurPageList.size() > pos && mCurPageList.get(pos) != null) {
                mReadDispatch.onPageChange(pos, mCurPageList.get(pos).pageCharCount(), mCurChapterPos, mCurPageList.get(pos).pageCharCount());
                return mCurPageList.get(pos);
            }
        }
        return new TxtPage();

    }
    /**
     * @return:获取下一的页面
     */
    private TxtPage getNextPage() {

        if (mCurPageList != null && !mCurPageList.isEmpty()) {
            if (mCurPage == null) {
                mCurPage = new TxtPage();
            }
            int pos = mCurPage.position + 1;
            if (mCurPageList.size() > pos) {

                if (mCurPageList.get(pos) != null && mCurPageList.get(pos - 1) != null) {
                    mReadDispatch.onPageChange(pos, mCurPageList.get(pos).pageCharCount(), mCurChapterPos, mCurPageList.get(pos - 1).pageCharCount());
                    return mCurPageList.get(pos);
                }
            }
        }
        return null;
    }

    protected boolean hasNextPage() {
        if (mCurPage == null) {
            mCurPage = new TxtPage();
        }
        int pos = mCurPage.position + 1;
        if (mCurPageList == null || mCurPageList.isEmpty()) {
            return false;
        }
        if (pos >= mCurPageList.size()) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前页的页码
     *
     * @return
     */
    public int getPagePos() {
        if (mCurPage == null) {
            return 0;
        }
        return mCurPage.position;
    }


    /**
     * @return:获取初始显示的页面
     */
    private TxtPage getCurPage(int pos) {

        if (mCurPageList == null) {
            return new TxtPage();
        }

        if (mCurPageList!=null && pos >= mCurPageList.size()) {
            return mCurPageList.get(mCurPageList.size()-1);
        }

        if (mCurChapterPos < mChapterList.size() && mCurPageList.get(pos) != null) {
            mReadDispatch.onPageChange(pos, mCurPageList.get(pos).pageCharCount(), mCurChapterPos, 0);
//            startPagePos = pos;
        }
        return mCurPageList.get(pos);
    }

    private boolean hasPrevChapter() {
        //判断是否上一章节为空
        if (mCurChapterPos - 1 < 0) {
            return false;
        }
        return true;
    }

    private boolean hasNextChapter() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return false;
        }
        // 判断是否到达目录最后一章
        if (mCurChapterPos + 1 >= mChapterList.size()) {
            return false;
        }
        return true;
    }

    boolean parseCurChapter() {

        if (mChapterList == null || mChapterList.isEmpty()) {
            return false;
        }

        //换源，重新进入阅读界面时判断
        if (mCurChapterPos >= mChapterList.size()) {
            mCurChapterPos = mChapterList.size()-1;
        }
        // 解析数据
        dealLoadPageList(mCurChapterPos);
        // 预加载下一页面
        preLoadNextChapter();

        if (mReadDispatch.getmStatus() == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return mCurPageList != null ? true : false;

    }

    /**
     * 解析上一章数据
     *
     * @return:数据是否解析成功
     */
    boolean parsePrevChapter() {
        // 加载上一章数据
        int prevChapter = mCurChapterPos - 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = prevChapter;

        if (mCurPage != null ) {
            mReadDispatch.onPageChange(mCurPage.position, 0, mCurChapterPos, mCurPage.pageCharCount());
        }
        // 当前章缓存为下一章
        mNextPageList = mCurPageList;

        // 判断是否具有上一章缓存
        if (mPrePageList != null) {
            mCurPageList = mPrePageList;
            mPrePageList = null;
            mReadDispatch.setStatus(STATUS_FINISH);
            // 回调
            ChapterChangeCallBack();
        } else {
            dealLoadPageList(prevChapter);
        }
        loadPrevChapter();
        return mCurPageList != null ? true : false;
    }

    /**
     * 解析下一章数据
     *
     * @return:返回解析成功还是失败
     */
    boolean parseNextChapter() {
        int nextChapter = mCurChapterPos + 1;

        mLastChapterPos = mCurChapterPos;
        mCurChapterPos = nextChapter;

        // 将当前章的页面列表，作为上一章缓存
        mPrePageList = mCurPageList;

        if (mCurPage != null ) {
            mReadDispatch.onPageChange(mCurPage.position, 0, mCurChapterPos, mCurPage.pageCharCount());
        }

        // 是否下一章数据已经预加载了
        if (mNextPageList != null) {
            mCurPageList = mNextPageList;
            mNextPageList = null;
            mReadDispatch.setStatus(STATUS_FINISH);
            // 回调
            ChapterChangeCallBack();
        } else {
            // 处理页面解析
            dealLoadPageList(nextChapter);
        }
        // 预加载下一页面
        preLoadNextChapter();
        loadNextChapter();

        if (mReadDispatch.getmStatus() == STATUS_LOADING) {
            loadCurrentChapter();
        }
        return mCurPageList != null ? true : false;
    }

    /**
     * 加载当前页的前面两个章节
     */
    private void loadPrevChapter() {
            int end = mCurChapterPos;
            int begin = end - 2;
            if (begin < 0) {
                begin = 0;
            }

            requestChapters(begin, end);
    }

    /**
     * 加载前一页，当前页，后一页。
     */
    private void loadCurrentChapter() {

        if (mChapterList == null || mChapterList.isEmpty()) {
            return;
        }
        int begin = mCurChapterPos;
        int end = mCurChapterPos;

        // 是否当前不是最后一章
        if (end < mChapterList.size()) {
            end = end + 1;
            if (end >= mChapterList.size()) {
                end = mChapterList.size() - 1;
            }
        }

        // 如果当前不是第一章
        if (begin != 0) {
            begin = begin - 1;
            if (begin < 0) {
                begin = 0;
            }
        }

        requestChapters(begin, end);
    }

    /**
     * 加载当前页的后两个章节
     */
    private void loadNextChapter() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return;
        }
            // 提示加载后两章
            int begin = mCurChapterPos + 1;
            int end = begin + 1;

            // 判断是否大于最后一章
            if (begin >= mChapterList.size()) {
                // 如果下一章超出目录了，就没有必要加载了
                return;
            }

            if (end > mChapterList.size()) {
                end = mChapterList.size() - 1;
            }

            requestChapters(begin, end);
    }

    private void requestChapters(int start, int end) {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return;
        }
        // 检验输入值
        if (start < 0) {
            start = 0;
        }

        if (end >= mChapterList.size()) {
            end = mChapterList.size() - 1;
        }


        List<TxtChapter> chapters = new ArrayList<>();

        // 过滤，哪些数据已经加载了
        for (int i = start; i <= end; ++i) {
            TxtChapter txtChapter = mChapterList.get(i);
            if (!mReadDispatch.hasChapterData(txtChapter)) {
                chapters.add(txtChapter);
            }
        }
        mReadDispatch.requestChapters(chapters);
    }
    // 预加载下一章
    private void preLoadNextChapter() {
        if (mChapterList == null || mChapterList.isEmpty()) {
            return;
        }
        int nextChapter = mCurChapterPos + 1;

        if (mChapterList.size() <= nextChapter) {
            return;
        }

        // 如果不存在下一章，且下一章没有数据，则不进行加载。
        if (!hasNextChapter()
                || !mReadDispatch.hasChapterData(mChapterList.get(nextChapter))) {
            return;
        }

        //如果之前正在加载则取消
                if (mPreLoadDisp != null) {
                    mPreLoadDisp.dispose();
                }

                //调用异步进行预加载加载
                Single.create(new SingleOnSubscribe<List<TxtPage>>() {
                    @Override
                    public void subscribe(SingleEmitter<List<TxtPage>> e) throws Exception {
                        e.onSuccess(loadPageList(nextChapter));
                    }
                }).compose(RxUtils::toSimpleSingle)
                        .subscribe(new SingleObserver<List<TxtPage>>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                mPreLoadDisp = d;
                            }

                            @Override
                            public void onSuccess(List<TxtPage> pages) {
                                mNextPageList = pages;
                            }

                            @Override
                            public void onError(Throwable e) {
                                //无视错误
                            }
                        });
            }

    private List<TxtPage> loadPageList(int chapterPos) throws Exception {

        if (mChapterList == null || mChapterList.isEmpty()) {
            return null;
        }
        if (mChapterList.size() <= chapterPos) {
            return null;
        }
        TxtChapter chapter = mChapterList.get(chapterPos);
        if (chapter == null) {
            return null;
        }
        // 判断章节是否存在
        if (!mReadDispatch.hasChapterData(chapter)) {
            return null;
        }else {
            // 获取章节的文本流
            BufferedReader reader = null;
            reader = mReadDispatch.getChapterReader(chapter);
            return mReadDispatch.loadPages(chapter,chapterPos,reader);
        }
    }

    private void dealLoadPageList(int chapterPos) {
        try {
            mCurPageList = loadPageList(chapterPos);
            if (mCurPageList != null) {
                if (mCurPageList.isEmpty()) {
                    mReadDispatch.setStatus(STATUS_EMPTY);

                    // 添加一个空数据
                    TxtPage page = new TxtPage();
                    page.lines = new ArrayList<>(1);
                    mCurPageList.add(page);
                } else {
                    mReadDispatch.setStatus(STATUS_FINISH);
                }
            } else {
                mReadDispatch.setStatus(STATUS_LOADING);
            }
        } catch (Exception e) {
            e.printStackTrace();

            mCurPageList = null;
            mReadDispatch.setStatus(STATUS_ERROR);
        }

        // 回调
        ChapterChangeCallBack();
    }

    public LineInfo.LineAdType getInsertViewType() {
        //当数据没加载出来时，有布局，mCurPage为空
        if (mCurPage != null && mCurPage.bHaveAd()) {
            return mCurPage.getAdType();
        }
        return LineInfo.LineAdType.LineAdTypeNone;
    }
    protected void clean() {
        isChapterOpen = false;
        clearList(mChapterList);
        clearList(mCurPageList);
        clearList(mNextPageList);
        mChapterList = null;
        mCurPageList = null;
        mNextPageList = null;
    }

    public void destroy() {
        if (mPreLoadDisp != null) {
            mPreLoadDisp.dispose();
        }
        mInstance = null;
    }
}
