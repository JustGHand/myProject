package com.pw.codeset.abilities.read.read;

import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.manager.BookManager;
import com.pw.codeset.manager.BookRecordManager;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.databean.RecordBean;
import com.pw.codeset.utils.AnimUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.pw.codeset.weidgt.MySeekBar;
import com.pw.read.ReadViewPager;
import com.pw.read.bean.ChaptersBean;
import com.pw.read.interfaces.ReadDataInterface;
import com.pw.read.interfaces.ReadTouchInterface;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.io.BufferedReader;
import java.util.List;

public class ReadActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    ReadViewPager mReadView;
    BottomNavigationView mBottomView;
    View mMenuBackView;

    RecyclerView mCatelogView;
    ReadCatelogAdapter mCatelogAdapter;

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
                            toggleCatelog();
                        }
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

        mCatelogView = findViewById(R.id.read_catelog_listview);
        mCatelogView.setLayoutManager(new LinearLayoutManager(this));

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
        mCatelogView.setAdapter(mCatelogAdapter);

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
        mReadView.toPage(tarChapterPos,tarCharPos);
        mCatelogAdapter.setData(getChapterList());
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

    private void toggleCatelog() {
        if (isCatelogShowing) {
            mCatelogView.setVisibility(View.GONE);
            AnimUtils.BottomOut(mCatelogView);
        }else {
            mCatelogView.setVisibility(View.VISIBLE);
            mCatelogAdapter.updateChapterIndex(getCurChapterPos());
            AnimUtils.BottomIn(mCatelogView);
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

    private int getCurChapterPos() {
        if (mReadView != null) {
            return mReadView.getCurChapterPos();
        }
        return 0;
    }

    private void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void showStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}
