package com.pw.codeset.abilities.main;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
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
        mBottomView = findViewById(R.id.main_bottom_menu);

        mViewPagerAdapter = new MainPageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT);
        mViewPagerAdapter.setMenu(mBottomView.getMenu());
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

        mBottomView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Menu menu = mBottomView.getMenu();
                int index = -1;

                // 遍历查找当前 item 的索引
                for (int i = 0; i < menu.size(); i++) {
                    if (menu.getItem(i).getItemId() == item.getItemId()) {
                        index = i;
                        break;
                    }
                }
                mViewPager.setCurrentItem(index);
//                if (item.getItemId() == R.id.main_menu_notes) {
//                    mViewPager.setCurrentItem(0);
//                } else if (item.getItemId() == R.id.main_menu_schedule) {
//                    mViewPager.setCurrentItem(1);
//                }
//                else if (item.getItemId() == R.id.main_menu_read) {
//                    mViewPager.setCurrentItem(1);
//                }else if (item.getItemId() == R.id.main_menu_games) {
//                    mViewPager.setCurrentItem(2);
//                }
                return true;
            }
        });
        mBottomView.setItemIconTintList(null);
    }

    @Override
    protected void dealWithData() {

    }

}