package com.pw.codeset.activity.main;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.activity.bezier.BezierCurveActivity;
import com.pw.codeset.activity.games.GamesActivity;
import com.pw.codeset.activity.login_mvp.UserLoginActivity;
import com.pw.codeset.activity.notes.NotesActivity;
import com.pw.codeset.activity.pc_connect.ActivityPcConnect;
import com.pw.codeset.activity.touchview.TouchViewAct;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseActivity;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivityView extends BaseActivity {

    private String FUNC_BAZIER;
    private String FUNC_TOUCHVIEW;
    private String FUNC_MVP_LOGIN;
    private String FUNC_PC_CONNECT;
    private String FUNC_GAMES;
    private String FUNC_NOTES;


    @Override
    protected int getContentId() {
        return R.layout.activity_main;
    }

    RecyclerView mBtnList;
    MainSupportFuncAdapter mAdapter;
    List<String> mSupportFuncList;

    @Override
    protected void initView() {

        mAdapter = new MainSupportFuncAdapter(this);
        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<String>() {
            @Override
            public void onClick(String data, int pos) {
                if (NStringUtils.isNotBlank(data)) {
                    if (data.equals(FUNC_BAZIER)) {
                        toBazier(null);
                    } else if (data.equals(FUNC_TOUCHVIEW)) {
                        toTouchView(null);
                    } else if (data.equals(FUNC_MVP_LOGIN)) {
                        toLoginMvp(null);
                    } else if (data.equals(FUNC_GAMES)) {
                        toGames(null);
                    } else if (data.equals(FUNC_PC_CONNECT)) {
                        toPcConnect(null);
                    } else if (data.equals(FUNC_NOTES)) {
                        toNotes(null);
                    }else {
                        MyApp.getInstance().showToast("未知错误");
                    }
                }
            }

            @Override
            public boolean onLongClick(String data, int pos) {
                return false;
            }
        });

        mBtnList = findViewById(R.id.main_btn_list);
        mBtnList.setLayoutManager(new GridLayoutManager(this, 3));
        mBtnList.setAdapter(mAdapter);
    }

    @Override
    protected void dealWithData() {

        initSupportFunc();
        mAdapter.setData(mSupportFuncList);
    }

    private void initSupportFunc() {
        if (mSupportFuncList == null) {
            mSupportFuncList = new ArrayList<>();
        }
        if (!mSupportFuncList.isEmpty()) {
            mSupportFuncList.clear();
        }

        FUNC_BAZIER = getResources().getString(R.string.bezier_activity);
        mSupportFuncList.add(FUNC_BAZIER);

        FUNC_TOUCHVIEW = getResources().getString(R.string.touch_activity);
        mSupportFuncList.add(FUNC_TOUCHVIEW);

        FUNC_MVP_LOGIN = getResources().getString(R.string.login_mvp_activity);
        mSupportFuncList.add(FUNC_MVP_LOGIN);

        FUNC_PC_CONNECT = getResources().getString(R.string.pc_connect_activity);
        mSupportFuncList.add(FUNC_PC_CONNECT);

        FUNC_GAMES = getResources().getString(R.string.games_activity);
        mSupportFuncList.add(FUNC_GAMES);

        FUNC_NOTES = getResources().getString(R.string.notes_activity);
        mSupportFuncList.add(FUNC_NOTES);
    }

    public void toBazier(View view) {
        startActivity(new Intent(MainActivityView.this, BezierCurveActivity.class));
    }

    public void toTouchView(View view) {
        startActivity(new Intent(MainActivityView.this, TouchViewAct.class));
    }

    public void toLoginMvp(View view) {
        startActivity(new Intent(MainActivityView.this, UserLoginActivity.class));
    }

    public void toGames(View view) {
        startActivity(new Intent(MainActivityView.this, GamesActivity.class));
    }

    public void toPcConnect(View view) {
        startActivity(new Intent(MainActivityView.this, ActivityPcConnect.class));
    }

    public void toNotes(View view) {
        startActivity(new Intent(MainActivityView.this, NotesActivity.class));
    }

}