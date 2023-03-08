package com.pw.codeset.abilities.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pw.codeset.R;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.others.recycle.BaseViewHolder;
import com.pw.baseutils.utils.NStringUtils;

import java.util.List;

public class GameListAdapter extends BaseRecyclerAdapter<String, GameListAdapter.GameViewHolder> {

    public GameListAdapter(Context mContext) {
        super(mContext);
    }


    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<String,GameViewHolder>() {
            @Override
            public void BindItemView(String data, GameViewHolder holder, int postion) {
                bindView(data, holder);
            }

            @Override
            public GameViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_gamelist, parent, false);
                GameViewHolder holder = new GameViewHolder(itemView);
                return holder;
            }

            @Override
            public boolean updateItemView(GameViewHolder holder, int position, List payloads) {
                return false;
            }
        };
    }

    private void bindView(String data, GameViewHolder holder) {
        if (holder != null && NStringUtils.isNotBlank(data)) {
            holder.gameNameView.setText(data);
        }
    }

    public class GameViewHolder extends BaseViewHolder{

        public ImageView gameIconView;
        public TextView gameNameView;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameIconView = itemView.findViewById(R.id.game_icon);
            gameNameView = itemView.findViewById(R.id.game_name);
        }
    }
}
