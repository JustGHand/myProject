package com.pw.codeset.abilities.games.sudoku;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.codeset.R;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.weidgt.MyDialog;

import java.util.ArrayList;
import java.util.List;

public class SudokuActivity extends BaseActivity {


    @Override
    protected int getContentId() {
        return R.layout.activity_sudoku;
    }

    RecyclerView mSudokuContainer;
    SudokuAdapter mAdapter;

    SudokuNumberBean selectedNumber;
    List<SudokuNumberBean> curDataList;

    boolean isEditMode = false;

    @Override
    protected void initView() {
        mSudokuContainer = findViewById(R.id.sudoku_container);
        mSudokuContainer.setLayoutManager(new GridLayoutManager(this, 9));
        mAdapter = new SudokuAdapter(this);
        mAdapter.setItemCLickListener(new BaseRecyclerAdapter.onItemClickListener<SudokuNumberBean>() {
            @Override
            public void onClick(SudokuNumberBean data, int pos) {
                selectedNumber = data;
                mAdapter.updateSelected(data);
            }

            @Override
            public boolean onLongClick(SudokuNumberBean data, int pos) {
                return false;
            }
        });
        mSudokuContainer.setAdapter(mAdapter);
    }

    @Override
    protected void dealWithData() {
    }

    public void play(View view) {
        SudokuCreator creator = new SudokuCreator();
        curDataList= creator.createSudoku(0);
        mAdapter.setData(curDataList);
    }

    public void numberClick(View view) {
        Integer tarValue = Integer.valueOf((String)view.getTag());
        if (tarValue != null) {
            if (selectedNumber != null && selectedNumber.isEditAble()) {
                if (isEditMode){
                    selectedNumber.setProbableValue(tarValue);
                }else {
                    selectedNumber.setShowValue(tarValue);
                    checkValue(tarValue);
                }
                mAdapter.updateSelected(selectedNumber);
            }
        }
    }

    public void clear(View view) {
        if (selectedNumber != null && selectedNumber.isEditAble()) {
            if (selectedNumber.isShowProbableValue()) {
                if (!selectedNumber.getProbableValueList().isEmpty()) {
                    selectedNumber.clearProbableValues();
                    mAdapter.updateSelected(selectedNumber);
                }
            }else {
                Integer curValue = selectedNumber.getShowValue();
                if (curValue != null) {
                    selectedNumber.setShowValue(null);
                    checkValue(curValue);
                    mAdapter.updateSelected(selectedNumber);
                }
            }
        }
    }

    private void checkValue(int tarValue) {

        boolean hasComplete = true;
        for (SudokuNumberBean sudokuNumberBean : curDataList) {
            boolean isValueConflict = false;
            for (SudokuNumberBean numberBean : curDataList) {
                if (sudokuNumberBean == numberBean) {
                    continue;
                }
                if (sudokuNumberBean.getShowValue() != null
                        && numberBean.getShowValue() != null
                        && sudokuNumberBean.getShowValue() == numberBean.getShowValue()) {
                    if (!isValueConflict) {
                        isValueConflict = sudokuNumberBean.getLineIndex() == numberBean.getLineIndex()
                                || sudokuNumberBean.getColumnIndex() == numberBean.getColumnIndex()
                                || sudokuNumberBean.getBlockIndex() == numberBean.getBlockIndex();
                    }
                }
            }
            if (sudokuNumberBean.getShowValue() == null || isValueConflict) {
                hasComplete = false;
            }
            sudokuNumberBean.setValConflict(isValueConflict);
        }
        if (hasComplete) {
            MyDialog myDialog = new MyDialog(this, "完成", "完成", "再来一次", "好的", new MyDialog.DialogListener() {
                @Override
                public boolean onConfirm() {
                    play(null);
                    return true;
                }

                @Override
                public boolean onCancel() {
                    return true;
                }

                @Override
                public void onDismiss() {

                }

                @Override
                public void onShow() {

                }
            }, true);
            myDialog.show();
        }
    }

    public void edit(View view) {
        isEditMode = !isEditMode;
        view.setSelected(isEditMode);
    }

}