package com.pw.codeset.abilities.read.read;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.abilities.read.manager.BookManager;
import com.pw.codeset.abilities.read.manager.BookRecordManager;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.databean.RecordBean;
import com.pw.codeset.utils.AnimUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.pw.read.ReadView;
import com.pw.read.bean.ChaptersBean;
import com.pw.read.manager.ReadDataInterface;
import com.pw.read.manager.ReadTouchInterface;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.io.BufferedReader;
import java.util.List;

public class ReadActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    ReadView mReadView;
    BottomNavigationView mBottomView;
    View mMenuBackView;
    RecyclerView mCatelogView;
    ReadCatelogAdapter mCatelogAdapter;

    String mBookId ;
    BookBean mBook;
    List<ChaptersBean> mChapterList;
    boolean isCatelogShowing = false;
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
                        toggleCatelog();
                        break;
                    case R.id.read_menu_layout:
                        mReadView.setTextSize(20);
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

    }

    @Override
    protected void dealWithData() {
        if (NStringUtils.isNotBlank(mBookId)) {
            mBook = BookManager.getInstance().getBook(mBookId);
            if (mBook != null) {
                setActTitle(mBook.getBookName());
            }
        }
        hideHeader();
    }

    @Override
    public void showHeader() {
        super.showHeader();
        isMenuShowing = true;
        mBottomView.setVisibility(View.VISIBLE);
        AnimUtils.BottomIn(mBottomView);
        mMenuBackView.setVisibility(View.VISIBLE);
        AnimUtils.AlphaIn(mMenuBackView);
    }

    @Override
    public void hideHeader() {
        super.hideHeader();
        isMenuShowing = false;
        mBottomView.setVisibility(View.GONE);
        AnimUtils.BottomOut(mBottomView);
        mMenuBackView.setVisibility(View.GONE);
        AnimUtils.AlphaOut(mMenuBackView);
    }


    @Override
    protected void finishData() {
        super.finishData();
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

    private boolean hideCurMenu() {
        if (isCatelogShowing) {
            toggleCatelog();
            return true;
        }
        if (isMenuShowing) {
            toggleHeaderVisible();
            return true;
        }
        return false;
    }

    private void hideAllMenu() {
        if (isCatelogShowing) {
            toggleCatelog();
        }
        if (isMenuShowing) {
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
}
