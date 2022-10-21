package com.pw.codeset.activity.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.utils.Constant;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;
import com.xd.baseutils.utils.NStringUtils;

import java.util.List;

public class NotesAdapter extends BaseRecyclerAdapter<NotesBean, NotesAdapter.NotesViewHolder> {


    public NotesAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<NotesBean,NotesViewHolder>() {
            @Override
            public void BindItemView(NotesBean data, NotesViewHolder holder, int postion) {
                notifyView(data, holder, postion);
            }

            @Override
            public NotesViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_notes, parent, false);
                NotesViewHolder viewHolder = new NotesViewHolder(view);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(NotesViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    private void notifyView(NotesBean data, NotesViewHolder holder, int pos) {
        if (data != null) {
            holder.titleText.setText(data.getTitle());
            holder.dateText.setText(NStringUtils.dateConvert(data.getDate(), Constant.DATA_PARTNER_WITH_LINE));
            holder.contentText.setText(data.getContent());
            holder.itemView.setAlpha(data.haveDone() ? 0.3f : 1f);
        }
    }

    public class NotesViewHolder extends BaseViewHolder{
        TextView titleText;
        TextView dateText;
        TextView contentText;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.item_notes_title);
            dateText = itemView.findViewById(R.id.item_notes_date);
            contentText = itemView.findViewById(R.id.item_notes_content);
        }
    }

}
