package com.pw.codeset.abilities.notes;

import android.view.View;
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


        mTitleEdit = findViewById(R.id.notes_edit_title_edit);
        mDateEdit = findViewById(R.id.notes_edit_date_edit);
        mContentEdit = findViewById(R.id.notes_edit_content_edit);

        String noteId = getIntent().getStringExtra(Constant.NOTE_ID);
        if (NStringUtils.isNotBlank(noteId)) {
            mNoteBean = NotesManager.getInstance().getNote(noteId);
        }
        if (mNoteBean == null) {
            mNoteBean = new NotesBean();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveNote();
    }

    @Override
    protected void onMenuClick(View view) {
        saveNote();
        finish();
    }

    @Override
    protected void dealWithData() {
        if (mNoteBean != null) {
            String noteTitle = mNoteBean.getTitle();
            if (NStringUtils.isNotBlank(noteTitle)) {
                mTitleEdit.setText(noteTitle);
            }
            Long noteTime = mNoteBean.getDate();
            String noteDate = "";
            if (noteTime != null && noteTime > 0) {
                noteDate = NStringUtils.dateConvert(noteTime, Constant.DATA_PARTNER_WITH_LINE);
            }
            if (NStringUtils.isBlank(noteDate)) {
                noteDate = getResources().getString(R.string.notes_edit_date);
            }
            mDateEdit.setText(noteDate);

            mContentEdit.setText(mNoteBean.getContent());
        }
    }

    private void saveNote() {
        if (mNoteBean == null) {
            mNoteBean = new NotesBean();
        }
        String title = mTitleEdit.getText().toString();
        String content = mContentEdit.getText().toString();
        if (NStringUtils.isBlank(title)) {
            title = getResources().getString(R.string.notes_edit_title_hint);
        }
        if (NStringUtils.isBlank(content)) {
            content = getResources().getString(R.string.notes_edit_content_hint);
        }
        mNoteBean.setTitle(title);
        mNoteBean.setContent(content);
        NotesManager.getInstance().updateNotes(mNoteBean);
    }
}
