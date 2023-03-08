package com.pw.codeset.abilities.notes;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.utils.CommenUseViewUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.weidgt.SelectDialog;
import com.pw.codeset.weidgt.WarpLinearLayout;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.utils.ArrayUtils;
import com.pw.baseutils.utils.NStringUtils;

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

    WarpLinearLayout mLabelViewContainer;
    private List<String> mSelectedLabelList;

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

        mLabelViewContainer = view.findViewById(R.id.notes_tag_container);

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
        if (mDataList == null) {
            mDataList = new ArrayList<>();
        }
        List<String> mEnableLabelList = NotesManager.getInstance().getLabelList();
        mSelectedLabelList = new ArrayList<>();
        mLabelViewContainer.removeAllViews();
        if (mEnableLabelList != null && !mEnableLabelList.isEmpty()) {
            mLabelViewContainer.setVisibility(View.VISIBLE);
            for (int i = 0; i < mEnableLabelList.size(); i++) {
                String label = mEnableLabelList.get(i);
                if (NStringUtils.isNotBlank(label)) {
                    addTagBtn(label);
                }
            }
        }else {
            mLabelViewContainer.setVisibility(View.GONE);
        }
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
        boolean haveDone = notesBean.haveDone();
        String content = haveDone ? "确认切换为未完成？" : "确认完成？";
        List<String> items = new ArrayList<>();
        items.add(content);
        items.add("删除");
        SelectDialog selectDialog = new SelectDialog(getActivity(), R.style.transparentFrameWindowStyle, (parent, view, position, id) -> {
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
        }, items, -1);
        selectDialog.show();
    }

    private void addTagBtn(String label) {
        CheckBox labelView = CommenUseViewUtils.getNoteLabelView(getContext(), label,false, (label1, isChecked) -> filterByLabel(label1,isChecked));
        mLabelViewContainer.addView(labelView);
    }

    private void filterByLabel(String label,boolean isChecked) {
        if (NStringUtils.isBlank(label)) {
            return;
        }

        if (mSelectedLabelList == null) {
            mSelectedLabelList = new ArrayList<>();
        }

        if (isChecked) {
            mSelectedLabelList.add(label);
        } else {
            mSelectedLabelList.remove(label);
        }

        if (NStringUtils.isBlank(label)) {
            mAdapter.setData(mDataList);
        }else {
            if (mDataList != null) {
                List<NotesBean> filterList = new ArrayList<>();
                if (mSelectedLabelList.isEmpty()) {
                    filterList = mDataList;
                } else {
                    for (int i = 0; i < mDataList.size(); i++) {
                        NotesBean data = mDataList.get(i);
                        if (data != null) {
                            List<String> dataLabelList = data.getLabel();
                            List<?> intersectionLabelList = ArrayUtils.getArrayIntersection(dataLabelList, mSelectedLabelList);
                            if (ArrayUtils.isArrayEnable(intersectionLabelList)) {
                                filterList.add(data);
                            }
                        }
                    }
                }
                mAdapter.setData(filterList);
            }
        }
    }
}
