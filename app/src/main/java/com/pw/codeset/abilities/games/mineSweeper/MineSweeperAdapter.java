package com.pw.codeset.abilities.games.mineSweeper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.others.recycle.BaseViewHolder;

import java.util.List;

public class MineSweeperAdapter extends BaseRecyclerAdapter<Boolean, MineSweeperAdapter.SweeperViewHolder> {


    public MineSweeperAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<Boolean,SweeperViewHolder>() {
            @Override
            public void BindItemView(Boolean data, SweeperViewHolder holder, int postion) {

            }

            @Override
            public SweeperViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_game_minesweeper, parent, false);
                return new SweeperViewHolder(contentView);
            }

            @Override
            public boolean updateItemView(SweeperViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    public class SweeperViewHolder extends BaseViewHolder{

        Button mBtn;

        public SweeperViewHolder(@NonNull View itemView) {
            super(itemView);
            mBtn = itemView.findViewById(R.id.sweep_btn);
        }
    }

}
