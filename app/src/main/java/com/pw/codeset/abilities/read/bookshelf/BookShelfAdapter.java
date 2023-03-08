package com.pw.codeset.abilities.read.bookshelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.codeset.databean.BookBean;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.others.recycle.BaseViewHolder;

import java.util.List;

public class BookShelfAdapter extends BaseRecyclerAdapter<BookBean, BookShelfAdapter.BookShelfViewHolder> {
    public BookShelfAdapter(Context mContext) {
        super(mContext);
    }

    private boolean isEditMode = false;

    public void startEdit() {
        isEditMode = true;
        notifyDataSetChanged();
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<BookBean,BookShelfAdapter.BookShelfViewHolder>() {
            @Override
            public void BindItemView(BookBean book, BookShelfAdapter.BookShelfViewHolder holder, int postion) {
                holder.mSelector.setEnabled(isEditMode);
                if (book!=null) {
                    holder.mBookName.setText(book.getBookName());
                }else {
                    holder.mBookName.setText(mContext.getResources().getString(R.string.book_default_name));
                }
            }

            @Override
            public BookShelfAdapter.BookShelfViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_bookshelf, parent, false);
                BookShelfViewHolder viewHolder = new BookShelfViewHolder(view);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(BookShelfAdapter.BookShelfViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    public class BookShelfViewHolder extends BaseViewHolder{

        TextView mBookName;
        ImageView mSelector;
        ImageView mBookCover;

        public BookShelfViewHolder(@NonNull View itemView) {
            super(itemView);
            mBookName = itemView.findViewById(R.id.item_bs_book_name);
            mSelector = itemView.findViewById(R.id.item_bs_selector);
            mBookCover = itemView.findViewById(R.id.item_bs_book_cover);
        }
    }
}
