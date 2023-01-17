package com.pw.codeset.abilities.notes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.codeset.R;
import com.pw.codeset.base.BaseFragment;
import com.pw.codeset.databean.NotesBean;
import com.pw.codeset.manager.NotesManager;
import com.pw.codeset.utils.CommenUseViewUtils;
import com.pw.codeset.utils.Constant;
import com.pw.codeset.utils.ResourceUtils;
import com.pw.codeset.weidgt.SelectDialog;
import com.pw.codeset.weidgt.WarpLinearLayout;
import com.xd.baseutils.others.recycle.BaseRecyclerAdapter;
import com.xd.baseutils.utils.ArrayUtils;
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

    WarpLinearLayout mLabelViewContainer;
    private List<String> mSelectedLabelList;
    private List<String> mEnableLabelList;

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
        mEnableLabelList = NotesManager.getInstance().getLabelList();
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

    private void addTagBtn(String label) {
        CheckBox labelView = CommenUseViewUtils.getNoteLabelView(getContext(), label,false, new CommenUseViewUtils.onLabelCheckListener() {
            @Override
            public void onCheckedChange(String label, boolean isChecked) {
                filterByLabel(label,isChecked);
            }
        });
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
            if (mSelectedLabelList.contains(label)) {
                mSelectedLabelList.remove(label);
            }
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
                            List<String> intersectionLabelList = (List<String>) ArrayUtils.getArrayIntersection(dataLabelList, mSelectedLabelList);
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
