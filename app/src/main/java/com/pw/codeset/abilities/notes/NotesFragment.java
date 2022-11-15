package com.pw.codeset.abilities.notes;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.weidgt.SelectDialog;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.NStringUtils;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends BaseFragment {
    @Override
    protected int getContentId() {
        return R.layout.activity_notes;
    }

    RecyclerView mRecyclerView;
    NotesAdapter mAdapter;
    List<NotesBean> mDataList;

    @Override
    protected void initView(View view) {
        mRecyclerView = view.findViewById(R.id.notes_listview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new NotesAdapter(getContext());

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<NotesBean>() {
            @Override
            public void onClick(NotesBean data, int pos) {
                toNoteDetal(data);
            }

            @Override
            public boolean onLongClick(NotesBean data, int pos) {
                showNoteDialog(data);
                return true;
            }
        });

    }

    @Override
    protected void dealWithData() {
        refreshList();
    }

    @Override
    protected void onNormalResume() {
        super.onNormalResume();
        refreshList();
    }

    @Override
    protected void onMenuClick(View view) {
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
        Intent intent = new Intent(getContext(), NotesEditActivity.class);
        intent.putExtra(Constant.NOTE_ID, noteId);
        startActivity(intent);
    }

    private void showNoteDialog(NotesBean notesBean) {
        if (notesBean == null) {
            return;
        }
        String title = notesBean.getTitle();
        if (NStringUtils.isBlank(title)) {
            title = getResources().getString(R.string.notes_activity);
        }
        boolean haveDone = notesBean.haveDone();
        String content = haveDone ? "确认切换为未完成？" : "确认完成？";
        List<String> items = new ArrayList<>();
        items.add(content);
        items.add("删除");
        SelectDialog selectDialog = new SelectDialog(getActivity(), R.style.transparentFrameWindowStyle, new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        notesBean.setState(haveDone ? NotesBean.NOTE_STATE_TODO : NotesBean.NOTE_STATE_DONE);
                        NotesManager.getInstance().updateNotes(notesBean);
                        break;
                    case 1:
                        NotesManager.getInstance().deleteNotes(notesBean);
                        break;
                    default:break;
                }
                refreshList();
            }
        }, items, -1);
        selectDialog.show();
    }
}
