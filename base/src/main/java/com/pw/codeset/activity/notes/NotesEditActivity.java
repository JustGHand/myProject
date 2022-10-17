package com.pw.codeset.activity.notes;

import android.widget.EditText;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.utils.Constant;
import com.xd.baseutils.utils.NStringUtils;

public class NotesEditActivity extends BaseActivity {

    NotesBean mNoteBean;

    EditText mTitleEdit;
    EditText mDateEdit;
    EditText mContentEdit;

    @Override
    protected int getContentId() {
        return R.layout.activity_notes_edit;
    }

    @Override
    protected void initView() {
        String noteId = getIntent().getStringExtra(Constant.NOTE_ID);
        if (NStringUtils.isNotBlank(noteId)) {
            mNoteBean = NotesManager.getInstance().getNote(noteId);
        }
        if (mNoteBean == null) {
            mNoteBean = new NotesBean();
        }
    }

    @Override
    protected void dealWithData() {
        if (mNoteBean != null) {
            String noteTitle = mNoteBean.getTitle();
            if (NStringUtils.isBlank(noteTitle)) {
                noteTitle = getResources().getString(R.string.notes_edit_title);
            }
            mTitleEdit.setText(noteTitle);

            Long noteTime = mNoteBean.getDate();
            String noteDate = "";
            if (noteTime != null && noteTime > 0) {
                noteDate = NStringUtils.dateConvert(noteTime, Constant.DATA_PARTNER_WITH_LINE);
            }
            if (NStringUtils.isBlank(noteDate)) {
                noteDate = getResources().getString(R.string.notes_edit_date);
            }
            mDateEdit.setText(noteDate);

        }
    }
}
