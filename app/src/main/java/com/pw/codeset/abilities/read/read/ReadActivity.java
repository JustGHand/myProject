package com.pw.codeset.abilities.read.read;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseViewPagerAdapter;
import com.pw.codeset.databean.BookMarkBean;
import com.pw.codeset.manager.BookManager;
import com.pw.codeset.manager.BookMarkManager;
import com.pw.codeset.manager.BookRecordManager;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.databean.RecordBean;
import com.pw.codeset.utils.AnimUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.LogToastUtils;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.pw.codeset.weidgt.CustomScrollView;
import com.pw.codeset.weidgt.MySeekBar;
import com.pw.codeset.weidgt.SelectDialog;
import com.pw.codeset.weidgt.fastscroll.FastScrollRecyclerView;
import com.pw.read.ReadView;
import com.pw.read.bean.ChaptersBean;
import com.pw.read.bean.LineInfo;
import com.pw.read.bean.TxtPage;
import com.pw.read.interfaces.ReadCallBack;
import com.pw.read.interfaces.ReadDataInterface;
import com.pw.read.interfaces.ReadTouchInterface;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.utils.NStringUtils;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    ReadView mReadView;
    BottomNavigationView mBottomView;
    View mMenuBackView;

    CustomScrollView mCustomScrollView;
    TextView mBookMarkTip;
    TextView mQuitTip;

    ImageView mBookMarkIcon;
    ImageView mBookMarkSelIcon;

    ViewPager mViewPager;
    BaseViewPagerAdapter mPageAdapter;

    View mCatelogView;
    View mBookMarkView;

    FastScrollRecyclerView mCatelogRecycleView;
    ReadCatelogAdapter mCatelogAdapter;

    FastScrollRecyclerView mBookMarkRecyleView;
    BookMarkAdapter mBookMarkAdapter;

    ConstraintLayout mMenuStyle;
    MySeekBar mFontSeekBar;
    MySeekBar mMarginSeekBar;
    MySeekBar mLeftPaddingSeekBar;

    String mBookId ;
    BookBean mBook;
    List<ChaptersBean> mChapterList;
    boolean isCatelogShowing = false;
    boolean isStyleShowing = false;
    boolean isMenuShowing = false;

    @Override
    protected void initView() {

        mReadView = findViewById(R.id.read_container);
        mBookId = getIntent().getStringExtra(Constant.BOOK_ID);

        mMenuBackView = findViewById(R.id.read_menu_back);
        mMenuBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideCurMenu();
            }
        });

        mBottomView = findViewById(R.id.read_bottom_view);
        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.read_menu_catelog:
                        if (!isCatelogShowing) {
                            hideAllMenu(false);
                        }
                        toggleCatelog();
                        break;
                    case R.id.read_menu_layout:
                        if (!isStyleShowing) {
                            hideAllMenu(false);
                            toggleStyleMenu();
                        }
                        break;
                    case R.id.read_menu_style:
                        hideAllMenu(false);
                        break;
                }
                return true;
            }
        });

        mViewPager = findViewById(R.id.read_catelog_listview);

        mCatelogView = LayoutInflater.from(this).inflate(R.layout.view_catelog,null,false);

        mCatelogRecycleView = mCatelogView.findViewById(R.id.scroll_recyclerview);
        mCatelogRecycleView.setLayoutManager(new LinearLayoutManager(this));

        mCatelogAdapter = new ReadCatelogAdapter(this);
        mCatelogAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<ChaptersBean>() {
            @Override
            public void onClick(ChaptersBean data, int pos) {
                mReadView.toChapter(data.getIndex());
                hideAllMenu();
            }

            @Override
            public boolean onLongClick(ChaptersBean data, int pos) {
                return false;
            }
        });
        mCatelogRecycleView.setAdapter(mCatelogAdapter);

        mBookMarkView = LayoutInflater.from(this).inflate(R.layout.view_catelog,null,false);

        mBookMarkRecyleView = mBookMarkView.findViewById(R.id.scroll_recyclerview);
        mBookMarkRecyleView.setLayoutManager(new LinearLayoutManager(this));

        mBookMarkAdapter = new BookMarkAdapter(this);
        mBookMarkAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<BookMarkBean>() {
            @Override
            public void onClick(BookMarkBean data, int pos) {
                mReadView.toPage(data.getChapterPos(),data.getCharPos());
                hideAllMenu();
            }

            @Override
            public boolean onLongClick(BookMarkBean data, int pos) {
                showDeleteDialog(data);
                return true;
            }
        });
        mBookMarkRecyleView.setAdapter(mBookMarkAdapter);

        List<View> pages = new ArrayList<>();
        pages.add(mCatelogView);
        pages.add(mBookMarkView);
        mPageAdapter = new BaseViewPagerAdapter(pages);
        mViewPager.setAdapter(mPageAdapter);

        mMenuStyle = findViewById(R.id.read_style_menu);
        mFontSeekBar = findViewById(R.id.rm_font_seek);
        mMarginSeekBar = findViewById(R.id.rm_margin_seek);
        mLeftPaddingSeekBar = findViewById(R.id.rm_left_margin_seek);

        mFontSeekBar.setProgresssTextShift(14);
        mFontSeekBar.setMax(20);
        mFontSeekBar.setProgress(mReadView.getTextSize()-14);
        mFontSeekBar.setOnSeekBarChangeListener(new MySeekBar.MySeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mReadView.setTextSize(14+progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onAnchorClick(int progress) {

            }
        });
        mMarginSeekBar.setProgresssTextShift(0);
        mMarginSeekBar.setMax(40);
        mMarginSeekBar.setProgress(40-mReadView.getTextInterval());
        mMarginSeekBar.setOnSeekBarChangeListener(new MySeekBar.MySeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mReadView.setTextInterval(40-progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onAnchorClick(int progress) {

            }
        });
        mLeftPaddingSeekBar.setProgresssTextShift(0);
        mLeftPaddingSeekBar.setMax(40);
        mLeftPaddingSeekBar.setProgress(40-mReadView.getPagePadding());
        mLeftPaddingSeekBar.setOnSeekBarChangeListener(new MySeekBar.MySeekBarListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (fromUser) {
                    mReadView.setPagePadding(40-progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onAnchorClick(int progress) {

            }
        });

        mBookMarkTip = findViewById(R.id.read_bookmark_tip);
        mQuitTip = findViewById(R.id.read_quit_tip);

        mBookMarkIcon = findViewById(R.id.read_bookmark_icon);
        mBookMarkSelIcon = findViewById(R.id.read_bookmark_sel_icon);

        mCustomScrollView = findViewById(R.id.read_overscroll);
        mCustomScrollView.setSupportOverScroll(true);
        mCustomScrollView.setmOverScrollListener(new CustomScrollView.OverScrollListener() {
            @Override
            public void onScrollingY(int maxOverScrollX, int overScrolledX, int maxOverScrollY, int overScrolledY, int thresholdY) {
                if (overScrolledY>0){
                    if (overScrolledY >= thresholdY) {
                        mQuitTip.setText("松开退出阅读");
                    }else {
                        mQuitTip.setText("上滑退出阅读");
                    }
                }else {
                    boolean isCurPageWithBookMark = isCurPageMarked();
                    if ((-overScrolledY) >= thresholdY) {
                        if (isCurPageWithBookMark) {
                            mBookMarkTip.setText("松开删除书签");
                        } else {
                            mBookMarkTip.setText("松开添加书签");
                        }
                    } else {
                        if (isCurPageWithBookMark) {
                            mBookMarkTip.setText("下拉删除书签");
                        } else {
                            mBookMarkTip.setText("下拉添加书签");
                        }
                    }
                }
            }

            @Override
            public void onTouchEnd(int direction, boolean overThreshold) {

                if (overThreshold) {
                    if (direction > 0) {//超出阈值且向上滑动执行退出逻辑
                        ReadActivity.this.finish();
                    } else if (direction < 0) {
                        boolean curPageMarkedAfterToggle = toggleCurPageBookMark();
                        showBookMarkIcon(curPageMarkedAfterToggle);
                    }
                }else {
                    boolean haveBookMark = isCurPageMarked();
                    showBookMarkIcon(haveBookMark);
                    if (haveBookMark) {
                        showBookMarkIcon(true);
                    } else {
                        showBookMarkIcon(false);
                    }
                }
            }
        });
    }

    @Override
    protected void dealWithData() {
        if (NStringUtils.isNotBlank(mBookId)) {
            mBook = BookManager.getInstance().getBook(mBookId);
            if (mBook != null) {
                setActTitle(mBook.getBookName());
                getChapterList();
            }
        }
    }

    @Override
    public void showHeader() {
        super.showHeader();
        isMenuShowing = true;
        mBottomView.setVisibility(View.VISIBLE);
        AnimUtils.BottomIn(mBottomView);
        mMenuBackView.setVisibility(View.VISIBLE);
        AnimUtils.AlphaIn(mMenuBackView);
        showStatusBar();
    }

    @Override
    public void hideHeader() {
        super.hideHeader();
        isMenuShowing = false;
        mBottomView.setVisibility(View.GONE);
        AnimUtils.BottomOut(mBottomView);
        mMenuBackView.setVisibility(View.GONE);
        AnimUtils.AlphaOut(mMenuBackView);
        hideStatusBar();
        showBookMarkIcon(isCurPageMarked());
    }


    @Override
    protected void finishData() {
        super.finishData();
        hideHeader();
        int tarChapterPos = 0;
        int tarCharPos = 0;
        RecordBean recordBean = BookRecordManager.getInstance().getBookRecord(mBookId);
        if (recordBean != null) {
            tarChapterPos = recordBean.getChapterPos();
            tarCharPos = recordBean.getStartCharPos();
        }
        mReadView.init(getChapterList(), new ReadDataInterface() {
            @Override
            public List<? extends ChaptersBean> getChapterList() {
                return ReadActivity.this.getChapterList();
            }

            @Override
            public BufferedReader getChapterReader(ChaptersBean chapter) {
                return TxtParser.getChapterBuffer(chapter);
            }

            @Override
            public void onRecordSave(int chapterPos, int charPos) {
                BookRecordManager.getInstance().saveBookRecord(mBookId,chapterPos,charPos);
            }
        });
        mReadView.setTouchListener(new ReadTouchInterface() {
            @Override
            public boolean touchAble() {
                return true;
            }

            @Override
            public void onCenterClick() {
                toggleHeaderVisible();
            }
        });
        mReadView.setReadCallBack(new ReadCallBack() {
            @Override
            public void onPageChange() {
                long chapterStartPos = getChapterList().get(getCurChapterPos()).start;
                int pageStartPos = getCurPageStartCharPos();
                long totalCharCount = getChapterList().get(getChapterList().size()-1).end;
                float percent = (chapterStartPos + pageStartPos) * 10000 / totalCharCount;
                LogToastUtils.printLog("page percent : " + percent/100);
                showBookMarkIcon(isCurPageMarked());
            }
        });
        mReadView.toPage(tarChapterPos,tarCharPos);
        updateCatelog();
        updateBookMark();
    }

    private void updateCatelog() {
        mCatelogAdapter.setData(getChapterList());
    }

    private void updateBookMark() {
        mBookMarkAdapter.setData(BookMarkManager.getInstance().getBookMarkList(mBookId));
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        int chapterPos = mReadView.getCurChapterPos();
        int charPos = mReadView.getCurPageCharPos();
        BookRecordManager.getInstance().saveBookRecord(mBookId, chapterPos, charPos);
    }

    @Override
    public void onBackPressed() {
        if (!hideCurMenu()) {
            super.onBackPressed();
        }
    }

    private boolean toggleCurPageBookMark() {
        int chapterPos = getCurChapterPos();
        int charPos = getCurPageStartCharPos();
        int charCount = getCurPageCharCount();
        boolean isCurPageMarked = isCurPageMarked();
        if (isCurPageMarked) {
            BookMarkManager.getInstance().deleteBookMark(mBookId, chapterPos, charPos, charCount);
            return false;
        }else {
            BookMarkManager.getInstance().saveBookMark(mBookId,getCurChapterTitle(),getPageBreviary(), chapterPos, charPos);
            return true;
        }
    }

    private boolean isCurPageMarked() {
        int chapterPos = getCurChapterPos();
        int charPos = getCurPageStartCharPos();
        int charCount = getCurPageCharCount();
        return BookMarkManager.getInstance().isCurPageHasMark(mBookId, chapterPos, charPos, charCount);
    }

    private void showBookMarkIcon(boolean show) {
        mBookMarkIcon.setSelected(show);
        mBookMarkSelIcon.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void toggleCatelog() {
        if (isCatelogShowing) {
            mViewPager.setVisibility(View.GONE);
            AnimUtils.BottomOut(mViewPager);
        }else {
            mViewPager.setVisibility(View.VISIBLE);
            mCatelogAdapter.updateChapterIndex(getCurChapterPos());
            AnimUtils.BottomIn(mViewPager);
        }
        isCatelogShowing = !isCatelogShowing;
    }

    private void toggleStyleMenu() {
        if (isStyleShowing) {
            mMenuStyle.setVisibility(View.GONE);
            AnimUtils.BottomOut(mMenuStyle);
        }else {
            mMenuStyle.setVisibility(View.VISIBLE);
            AnimUtils.BottomIn(mMenuStyle);
        }
        isStyleShowing = !isStyleShowing;
    }

    private boolean hideCurMenu() {
        if (isCatelogShowing) {
            toggleCatelog();
            return true;
        }
        if (isStyleShowing) {
            toggleStyleMenu();
            return true;
        }
        if (isMenuShowing) {
            toggleHeaderVisible();
            return true;
        }
        return false;
    }

    private void hideAllMenu() {
        hideAllMenu(true);
    }

    private void hideAllMenu(boolean hideMainMenu) {
        if (isCatelogShowing) {
            toggleCatelog();
        }
        if (isStyleShowing) {
            toggleStyleMenu();
        }
        if (isMenuShowing && hideMainMenu) {
            hideHeader();
        }
    }

    private List<ChaptersBean> getChapterList() {
        if (mChapterList == null) {
            if (NStringUtils.isNotBlank(mBookId)) {
                mChapterList = BookManager.getBookChapters(mBookId);
            }
        }
        return mChapterList;
    }

    private String getCurChapterTitle() {
        TxtPage curPage = getCurPageData();
        if (curPage != null) {
            return curPage.getTitle();
        }
        return "";
    }

    private String getPageBreviary() {
        TxtPage curPage = getCurPageData();
        if (curPage != null) {
            List<LineInfo> lineInfos = curPage.getStringList();
            if (lineInfos != null && !lineInfos.isEmpty()) {
                for (int i = 0; i < lineInfos.size(); i++) {
                    LineInfo lineInfo = lineInfos.get(i);
                    if (lineInfo != null) {
                        String content = lineInfo.getmLineText();
                        if (NStringUtils.isNotBlank(content)) {
                            return content;
                        }
                    }
                }
            }
        }
        return "";
    }

    private TxtPage getCurPageData() {
        if (mReadView != null) {
            return mReadView.getCurPage();
        }
        return null;
    }

    private int getCurChapterPos() {
        if (mReadView != null) {
            return mReadView.getCurChapterPos();
        }
        return 0;
    }

    private int getCurPageStartCharPos() {
        if (mReadView != null) {
            return mReadView.getCurPageCharPos();
        }
        return 0;
    }

    private int getCurPageCharCount() {
        if (mReadView != null) {
            return mReadView.getCurPageCharCount();
        }
        return 0;
    }

    private void showDeleteDialog(BookMarkBean bookMarkBean) {
        if (bookMarkBean == null) {
            return;
        }
        List<String> items = new ArrayList<>();
        items.add("删除");
        SelectDialog selectDialog = new SelectDialog(this, R.style.transparentFrameWindowStyle, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        BookMarkManager.getInstance().deleteBookMark(bookMarkBean);
                        updateBookMark();
                        break;
                    default:break;
                }
            }
        }, items, -1);
        selectDialog.show();
    }



}
