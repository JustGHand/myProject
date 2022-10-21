package com.pw.codeset.activity.read.bookshelf;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.activity.read.BookManager;
import com.pw.codeset.activity.read.leadbook.LeadBookActivity;
import com.pw.codeset.activity.read.read.ReadActivity;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.BookBean;
import com.pw.codeset.utils.Constant;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;

import java.util.List;

public class BookShelfActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_bookshelf;
    }

    RecyclerView mRecyclerView;
    BookShelfAdapter mAdapter;
    private PopupWindow mPopWindow;

    List<BookBean> mBooks;

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.bookshelf_list);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new BookShelfAdapter(this);
        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<BookBean>() {
            @Override
            public void onClick(BookBean data, int pos) {
                if (data!=null) {
                    Intent intent = new Intent(BookShelfActivity.this, ReadActivity.class);
                    intent.putExtra(Constant.BOOK_ID, data.getBookId());
                    startActivity(intent);
                }
            }

            @Override
            public boolean onLongClick(BookBean data, int pos) {
                return false;
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onMenuClick(View view) {
        showMorePopup(view);
    }

    @Override
    protected void dealWithData() {
        mBooks = BookManager.getInstance().getBookList();
        mAdapter.setData(mBooks);
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        mBooks = BookManager.getInstance().getBookList();
        mAdapter.setData(mBooks);
    }

    private void toggleEditMode(boolean editMode) {

    }

    private void addBook() {
        startActivity(new Intent(this, LeadBookActivity.class));
    }


    private void showMorePopup(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.view_bookshelf_popup, null);
        mPopWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        TextView managerButton = contentView.findViewById(R.id.bookshelf_manager);
        TextView historyButton = contentView.findViewById(R.id.bookshelf_history);
        TextView leadBookButton = contentView.findViewById(R.id.bookshelf_leadbook);
        ImageView topTrangle = contentView.findViewById(R.id.bookshelf_pop_top);
        managerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditMode(true);
                mPopWindow.dismiss();
            }
        });

        leadBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBook();
            }
        });

        mPopWindow.setOutsideTouchable(true);
        mPopWindow.setBackgroundDrawable(new BitmapDrawable());
        //显示PopupWindow
        mPopWindow.showAsDropDown(view);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.alpha = 0.7f;
        this.getWindow().setAttributes(lp);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = BookShelfActivity.this.getWindow().getAttributes();
                lp.alpha = 1;
                BookShelfActivity.this.getWindow().setAttributes(lp);
            }
        });
    }

}
