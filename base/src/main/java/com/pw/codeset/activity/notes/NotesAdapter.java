package com.pw.codeset.activity.notes;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.pw.codeset.databean.NotesBean;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;

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

            }

            @Override
            public NotesViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public boolean updateItemView(NotesViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    public class NotesViewHolder extends BaseViewHolder{

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
