package com.pw.codeset.activity.read.read;

import android.view.View;
import android.widget.FrameLayout;

import com.pw.codeset.R;
import com.pw.codeset.activity.read.BookManager;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.utils.Constant;
import com.xd.baseutils.utils.NStringUtils;

public class ReadActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_read;
    }

    FrameLayout mContainer;

    String mBookId ;
    BookBean mBook;

    @Override
    protected void initView() {
        mContainer = findViewById(R.id.read_container);
        mBookId = getIntent().getStringExtra(Constant.BOOK_ID);

        mContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHeaderVisible();
            }
        });
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
}
