package com.pw.codeset.abilities.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pw.codeset.abilities.games.GamesFragment;
import com.pw.codeset.abilities.notes.NotesFragment;
import com.pw.codeset.abilities.read.bookshelf.BookShelfFragment;
import com.pw.codeset.abilities.tools.ToolsFragment;
import com.pw.codeset.base.BaseFragment;

public class MainPageAdapter extends FragmentPagerAdapter {
    public MainPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        mNoteFragment = new NotesFragment();
        mReadFragment = new BookShelfFragment();
        mGameFragment = new GamesFragment();
        mToolsFragment = new ToolsFragment();
    }

    BaseFragment mNoteFragment;
    BaseFragment mReadFragment;
    BaseFragment mGameFragment;
    BaseFragment mToolsFragment;

    @NonNull
    @Override
    public BaseFragment getItem(int position) {
        BaseFragment fragment = null;
        switch (position) {
            case 0:
                if (mNoteFragment == null) {
                    mNoteFragment = new NotesFragment();
                }
                fragment = mNoteFragment;
                break;
            case 1:
                if (mReadFragment == null) {
                    mReadFragment = new BookShelfFragment();
                }
                fragment = mReadFragment;
                break;
            case 2:
                if (mToolsFragment == null) {
                    mToolsFragment = new ToolsFragment();
                }
                fragment = mToolsFragment;
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
