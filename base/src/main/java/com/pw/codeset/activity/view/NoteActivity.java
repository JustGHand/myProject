package com.pw.codeset.activity.view;

import com.pw.codeset.activity.contact.NoteContact;
import com.pw.codeset.activity.presenter.NotePresenter;
import com.pw.codeset.base.BaseActivity;
import com.xd.base.base.BaseMVPActivity;

public class NoteActivity extends BaseMVPActivity<NoteContact.Presenter> implements NoteContact.View {
    @Override
    protected int getContentId() {
        return 0;
    }


    @Override
    protected NoteContact.Presenter bindPresenter() {
        return new NotePresenter();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {

    }
}
