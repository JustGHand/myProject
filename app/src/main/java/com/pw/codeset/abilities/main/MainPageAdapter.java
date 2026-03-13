package com.pw.codeset.abilities.main;

import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pw.codeset.R;
import com.pw.codeset.abilities.games.GamesFragment;
import com.pw.codeset.abilities.notes.NotesFragment;
import com.pw.codeset.abilities.read.bookshelf.BookShelfFragment;
import com.pw.codeset.abilities.schedule.ScheduleFragment;
import com.pw.codeset.abilities.schedule.ScheduleFragmentCompose;
import com.pw.codeset.abilities.tools.ToolsFragment;
import com.pw.codeset.base.BaseFragment;

public class MainPageAdapter extends FragmentPagerAdapter {
    public MainPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        mNoteFragment = new NotesFragment();
        mScheduleFragment = new ScheduleFragmentCompose();
        mReadFragment = new BookShelfFragment();
        mGameFragment = new GamesFragment();
        mToolsFragment = new ToolsFragment();
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    BaseFragment mNoteFragment;
    Fragment mScheduleFragment;
    BaseFragment mReadFragment;
    BaseFragment mGameFragment;
    BaseFragment mToolsFragment;

    Menu mMenu;



    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        int menuIdByPosition = getMenuIdByPosition(position);
        if (menuIdByPosition == R.id.main_menu_notes) {
            if (mNoteFragment == null) {
                mNoteFragment = new NotesFragment();
            }
            fragment = mNoteFragment;
        }else if (menuIdByPosition == R.id.main_menu_schedule) {
                if (mScheduleFragment == null) {
                    mScheduleFragment = new ScheduleFragmentCompose();
                }
                fragment = mScheduleFragment;
        }
        return fragment;
    }

    private int getMenuIdByPosition(int position) {
        // 安全检查：确保索引不越界
        if (position >= 0 && position < mMenu.size()) {
            // getItem(index) 返回的是 MenuItem 对象
            return mMenu.getItem(position).getItemId();
        }
        return -1;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
