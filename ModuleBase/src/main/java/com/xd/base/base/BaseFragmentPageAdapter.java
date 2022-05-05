package com.xd.base.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developer on 2019/7/4.
 */

public class BaseFragmentPageAdapter extends FragmentPagerAdapter {


    private List<? extends Fragment> fragmentList;

    public void setFragmentList(List<? extends Fragment> fragmentList) {
        this.fragmentList = fragmentList;
        notifyDataSetChanged();
    }


    public BaseFragmentPageAdapter(FragmentManager fm) {
        super(fm);
        this.fragmentList = new ArrayList<>();
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }



}
