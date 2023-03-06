package com.pw.codeset.abilities.main;

import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;

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
    MainPageAdapter mViewPagerAdapter;
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