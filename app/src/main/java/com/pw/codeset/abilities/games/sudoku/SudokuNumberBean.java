package com.pw.codeset.abilities.games.sudoku;

import java.util.ArrayList;
import java.util.List;

public class SudokuNumberBean {
    int value;
    int pos;
    int lineIndex;
    int columnIndex;
    int blockIndex;
    Integer showValue;
    DataChangeListener mListener;
    boolean editAble;
    boolean isValConflict;

    boolean needBack;
    boolean needBorder;

    List<Integer> probableValueList;
    boolean showProbableValue;

    public SudokuNumberBean(int value, int pos) {
        this.value = value;
        this.pos = pos;
        this.lineIndex = pos / 9;
        this.columnIndex = pos % 9;
        this.blockIndex = lineIndex / 3 * 3 + (columnIndex / 3);
        editAble = true;
        showProbableValue = false;
        probableValueList = new ArrayList<>();
    }

    public void onOtherNumberSelected(SudokuNumberBean otherNum) {
        if (otherNum != null) {
            Integer selectedValue = otherNum.getShowValue();


            boolean border = otherNum.pos == this.pos;
            if (selectedValue != null) {
                if (this.showValue != null && this.showValue == selectedValue) {
                    border = true;
                }
                if (this.probableValueList != null && this.probableValueList.contains(selectedValue)) {
                    border = true;
                }
            }
            boolean haveChangeBorder = setNeedBorder(border);

            boolean back = otherNum.getLineIndex() == getLineIndex() || otherNum.getColumnIndex() == getColumnIndex() || otherNum.getBlockIndex() == getBlockIndex();
            boolean haveChangeBack = setNeedBack(back);
            if (haveChangeBack || haveChangeBorder || otherNum.pos == this.pos) {
                if (mListener != null) {
                    mListener.onChange(pos, this);
                }
            }
        }
    }

    public List<Integer> getProbableValueList() {
        if (probableValueList == null) {
            probableValueList = new ArrayList<>();
        }
        return probableValueList;
    }

    public void setProbableValue(Integer value) {
        this.showProbableValue = true;
        if (this.probableValueList == null) {
            this.probableValueList = new ArrayList<>();
        }
        int index = this.probableValueList.indexOf(value);
        if (index != -1) {
            this.probableValueList.remove(index);
        } else {
            this.probableValueList.add(value);
        }
    }

    public void clearProbableValues() {
        if (this.probableValueList != null && !this.probableValueList.isEmpty()) {
            this.probableValueList.clear();
        }
    }

    public boolean isShowProbableValue() {
        return showProbableValue;
    }

    public void setListener(DataChangeListener listener) {
        this.mListener = listener;
    }

    public void show() {
        editAble = false;
        showValue = value;
    }

    public boolean isEditAble() {
        return editAble;
    }

    public boolean isValConflict() {
        return isValConflict;
    }

    public void setValConflict(boolean valConflict) {
        isValConflict = valConflict;
    }

    public Integer getShowValue() {
        return showValue;
    }

    public void setShowValue(Integer showValue) {
        this.showProbableValue = false;
        this.showValue = showValue;
    }

    public boolean isNeedBack() {
        return needBack;
    }

    public boolean setNeedBack(boolean needBack) {
        if (this.needBack != needBack) {
            this.needBack = needBack;
            return true;
        }
        return false;
    }

    public boolean isNeedBorder() {
        return needBorder;
    }

    public boolean setNeedBorder(boolean needBorder) {
        if (this.needBorder != needBorder) {
            this.needBorder = needBorder;
            return true;
        }
        return false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public int getBlockIndex() {
        return blockIndex;
    }

    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    public interface DataChangeListener {
        void onChange(int pos, SudokuNumberBean data);
    }

}
