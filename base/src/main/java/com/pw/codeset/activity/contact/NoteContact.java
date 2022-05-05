package com.pw.codeset.activity.contact;


import com.xd.base.base.BaseContract;
import com.xd.base.base.RxPresenter;

public class NoteContact implements BaseContract {

    public interface View extends BaseView{

    }

    public interface Presenter extends BasePresenter<NoteContact.View> {
        void getNoteList();

        void saveNote();
    }

}
