package com.pw.codeset.activity.games;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.activity.games.block.BlockGameActivity;
import com.pw.codeset.base.BaseActivity;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class GamesActivity extends BaseActivity {

    private String GAME_BLOCK;


    @Override
    protected void dealWithData() {

        GAME_BLOCK = this.getString(R.string.game_block);

        mGameList = new ArrayList<>();
        mGameList.add(GAME_BLOCK);

        mAdapter = new GameListAdapter(this);
        gameListView.setLayoutManager(new LinearLayoutManager(this));
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
    protected void initView() {

        gameListView = findViewById(R.id.games_list);

    }

    private void toBlockGame() {
        startActivity(new Intent(GamesActivity.this, BlockGameActivity.class));
    }
}
