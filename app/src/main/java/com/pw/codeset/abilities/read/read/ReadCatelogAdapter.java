package com.pw.codeset.abilities.read.read;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.read.bean.ChaptersBean;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.others.recycle.BaseViewHolder;

import java.util.List;

public class ReadCatelogAdapter extends BaseRecyclerAdapter<ChaptersBean, ReadCatelogAdapter.ReadCatelogViewHolder> {

    public ReadCatelogAdapter(Context mContext) {
        super(mContext);
    }

    private int mCurChapterIndex = 0;

    public void updateChapterIndex(int chapterIndex) {
        mCurChapterIndex = chapterIndex;
        notifyDataSetChanged();
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<ChaptersBean, ReadCatelogAdapter.ReadCatelogViewHolder>() {
            @Override
            public void BindItemView(ChaptersBean data, ReadCatelogAdapter.ReadCatelogViewHolder holder, int postion) {
                if (data != null) {
                    holder.mCatelogTitle.setText(data.getTitle());
                }
                holder.mCatelogTitle.setSelected(data.getIndex()==mCurChapterIndex);
                holder.mContentTextView.setVisibility(View.GONE);
            }

            @Override
            public ReadCatelogAdapter.ReadCatelogViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_read_catelog, parent, false);
                ReadCatelogViewHolder viewHolder = new ReadCatelogViewHolder(view);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(ReadCatelogAdapter.ReadCatelogViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    public class ReadCatelogViewHolder extends BaseViewHolder{
        TextView mCatelogTitle;
        TextView mContentTextView;
        public ReadCatelogViewHolder(@NonNull View itemView) {
            super(itemView);
            mCatelogTitle = itemView.findViewById(R.id.item_read_catelog_name);
            mContentTextView = itemView.findViewById(R.id.item_read_catelog_content);
        }
    }

}
