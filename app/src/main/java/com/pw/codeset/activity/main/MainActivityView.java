package com.pw.codeset.activity.main;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.activity.bezier.BezierCurveActivity;
import com.pw.codeset.activity.games.GamesActivity;
import com.pw.codeset.activity.login_mvp.UserLoginActivity;
import com.pw.codeset.activity.notes.NotesActivity;
import com.pw.codeset.activity.pc_connect.ActivityPcConnect;
import com.pw.codeset.activity.read.bookshelf.BookShelfActivity;
import com.pw.codeset.activity.touchview.TouchViewAct;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseActivity;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivityView extends BaseActivity {

    public static MainActivityView mInstance;

    public static MainActivityView getInstance() {
        return mInstance;
    }

    @Override
    protected int getContentId() {
        mInstance = this;
        return R.layout.activity_main;
    }

    ViewPager mViewPager;
    FragmentPagerAdapter mViewPagerAdapter;
    BottomNavigationView mBottomView;

    @Override
    protected void initView() {

        mViewPagerAdapter = new MainPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
        mViewPager = findViewById(R.id.main_viewpager);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mBottomView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBottomView = findViewById(R.id.main_bottom_menu);
        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_menu_notes:
                        mViewPager.setCurrentItem(0);
                        break;

                    case R.id.main_menu_read:
                        mViewPager.setCurrentItem(1);
                        break;

                    case R.id.main_menu_games:
                        mViewPager.setCurrentItem(2);
                        break;

                }
                return true;
            }
        });
        mBottomView.setItemIconTintList(null);
    }

    @Override
    protected void dealWithData() {

    }

}