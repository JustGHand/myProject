package com.pw.codeset.activity.games.block;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;

import java.util.List;

public class BlockGameAdapter extends BaseRecyclerAdapter<Boolean, BlockGameAdapter.BlockGameViewHolder> {

    public BlockGameAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<Boolean,BlockGameViewHolder>(){
            @Override
            public void BindItemView(Boolean data, BlockGameViewHolder holder, int postion) {
                bindView(data, holder);
            }

            @Override
            public BlockGameViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_game_block, parent, false);
                BlockGameViewHolder holder = new BlockGameViewHolder(itemView);
                return holder;
            }

            @Override
            public boolean updateItemView(BlockGameViewHolder holder, int position, List<Object> payloads) {
                return false;
            }
        };
    }

    private void bindView(Boolean checked, BlockGameViewHolder holder) {
        if (holder != null && checked != null) {
            holder.mBlockCheck.setChecked(checked);
        }
    }

    public class BlockGameViewHolder extends BaseViewHolder{
        public CheckBox mBlockCheck;
        public BlockGameViewHolder(@NonNull View itemView) {
            super(itemView);
            mBlockCheck = itemView.findViewById(R.id.block_blocks);
        }
    }
}
