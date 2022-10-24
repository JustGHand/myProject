package com.pw.codeset.activity.read.read;

import android.view.View;

import com.pw.codeset.R;
import com.pw.codeset.activity.read.BookManager;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.fileParase.TxtParser;
import com.pw.read.ReadView;
import com.pw.read.bean.ChaptersBean;
import com.pw.read.manager.ReadDataInterface;
import com.pw.read.manager.ReadTouchInterface;
import com.xd.baseutils.utils.NStringUtils;

import java.io.BufferedReader;
import java.util.List;

public class ReadActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    ReadView mReadView;

    String mBookId ;
    BookBean mBook;

    @Override
    protected void initView() {
        mReadView = findViewById(R.id.read_container);
        mBookId = getIntent().getStringExtra(Constant.BOOK_ID);

    }

    @Override
    protected void dealWithData() {
        if (NStringUtils.isNotBlank(mBookId)) {
            mBook = BookManager.getInstance().getBook(mBookId);
            if (mBook != null) {
                setActTitle(mBook.getBookName());
                List<ChaptersBean> chaptersBeans = BookManager.getBookChapters(mBookId);
                mReadView.init(chaptersBeans, new ReadDataInterface() {
                    @Override
                    public List<? extends ChaptersBean> getChapterList() {
                        return BookManager.getBookChapters(mBookId);
                    }

                    @Override
                    public BufferedReader getChapterReader(ChaptersBean chapter) {
                        return TxtParser.getChapterBuffer(chapter);
                    }
                });
            }
        }
        hideHeader();
    }

    @Override
    protected void finishData() {
        super.finishData();
        mReadView.toChapter(0);
    }
}
