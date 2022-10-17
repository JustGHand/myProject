package com.pw.codeset.activity.notes;

import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.utils.Constant;

import java.util.List;

public class NotesActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_notes;
    }

    RecyclerView mRecyclerView;
    NotesAdapter mAdapter;
    List<NotesBean> mDataList;

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.notes_listview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NotesAdapter(this);

        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void dealWithData() {
        refreshList();
    }

    @Override
    protected void onMenuClick() {
        super.onMenuClick();
        toNoteDetal(null);
    }

    private void refreshList() {
        mDataList = NotesManager.getInstance().getNotesList();
        mAdapter.setData(mDataList);
    }

    private void toNoteDetal(NotesBean notesBean) {
        String noteId = "";
        if (notesBean != null) {
            noteId = notesBean.getId();
        }
        Intent intent = new Intent(this, NotesEditActivity.class);
        intent.putExtra(Constant.NOTE_ID, noteId);
        startActivity(intent);
    }
}
