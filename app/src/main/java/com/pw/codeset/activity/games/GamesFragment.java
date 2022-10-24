package com.pw.codeset.activity.games;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.activity.games.block.BlockGameActivity;
import com.pw.codeset.base.BaseFragment;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class GamesFragment extends BaseFragment {
    private String GAME_BLOCK;


    @Override
    protected void dealWithData() {
        GAME_BLOCK = this.getString(R.string.game_block);
        mGameList = new ArrayList<>();
        mGameList.add(GAME_BLOCK);
    }

    @Override
    protected void finishData() {
        super.finishData();

        mAdapter = new GameListAdapter(getContext());
        gameListView.setLayoutManager(new LinearLayoutManager(getContext()));
        gameListView.setAdapter(mAdapter);

        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<String>() {
            @Override
            public void onClick(String data,int pos) {
                if (NStringUtils.isNotBlank(data)) {
                    if (data.equals(GAME_BLOCK)) {
                        toBlockGame();
                    }
                }
            }

            @Override
            public boolean onLongClick(String data, int pos) {
                return false;
            }
        });

        mAdapter.setData(mGameList);
    }

    @Override
    protected int getContentId() {
        return R.layout.activity_games;
    }

    RecyclerView gameListView;
    GameListAdapter mAdapter;

    List<String> mGameList;

    @Override
    protected void initView(View view) {

        gameListView = view.findViewById(R.id.games_list);

    }

    private void toBlockGame() {
        startActivity(new Intent(getContext(), BlockGameActivity.class));
    }

}
