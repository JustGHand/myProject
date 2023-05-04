package com.pw.codeset.abilities.games.sudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.pw.baseutils.others.recycle.BaseRecyclerAdapter;
import com.pw.baseutils.others.recycle.BaseViewHolder;
import com.pw.codeset.R;
import com.pw.codeset.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

public class SudokuAdapter extends BaseRecyclerAdapter<SudokuNumberBean, SudokuAdapter.SudokuViewHolder> {

    public SudokuAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public void setData(List<SudokuNumberBean> dataList) {
        super.setData(dataList);
        for (SudokuNumberBean sudokuNumberBean : mDataList) {
            sudokuNumberBean.setListener(new SudokuNumberBean.DataChangeListener() {
                @Override
                public void onChange(int pos,SudokuNumberBean data) {
                    notifyItemChanged(pos,new Gson().toJson(data));
                }
            });
        }
    }


    public void updateSelected(SudokuNumberBean numberBean) {
        for (SudokuNumberBean sudokuNumberBean : mDataList) {
            sudokuNumberBean.onOtherNumberSelected(numberBean);
        }
    }

    @Override
    public ItemGenerate GenerateItem() {
        return new ItemGenerate<SudokuNumberBean, SudokuViewHolder>() {
            @Override
            public void BindItemView(SudokuNumberBean data, SudokuViewHolder holder, int postion) {
                if (data.getValue() > 0) {
                    Integer showValue = data.getShowValue();
                    if (showValue != null) {
                        holder.numberText.setText(String.valueOf(showValue));
                    }else {
                        holder.numberText.setText("");
                    }
                }
                holder.topBorder.setVisibility(data.getLineIndex() % 3 == 0 ? View.VISIBLE : View.GONE);
                holder.bottomBorder.setVisibility(data.getLineIndex() % 3 == 2 ? View.VISIBLE : View.GONE);
                holder.leftBorder.setVisibility(data.getColumnIndex() % 3 == 0 ? View.VISIBLE : View.GONE);
                holder.rightBorder.setVisibility(data.getColumnIndex() % 3 == 2 ? View.VISIBLE : View.GONE);
                if (!data.isEditAble()) {
                    holder.numberText.setTextColor(ResourceUtils.getResColor(R.color.normal_text_color));
                }
            }

            @Override
            public SudokuViewHolder creatItemHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.item_sudoku_number, parent, false);
                SudokuViewHolder viewHolder = new SudokuViewHolder(view);
                return viewHolder;
            }

            @Override
            public boolean updateItemView(SudokuViewHolder holder, int position, List payloads) {
                if (payloads != null && !payloads.isEmpty()) {
//                    boolean[] target = (boolean[]) payloads.get(payloads.size()-1);
                    SudokuNumberBean tarData = mDataList.get(position);
                    holder.numberText.setSelected(tarData.isNeedBack());
                    holder.probableContainer.setSelected(tarData.isNeedBack());
                    holder.backView.setVisibility(tarData.isNeedBorder() ? View.VISIBLE : View.INVISIBLE);
                    holder.numberText.setText(String.valueOf(tarData.getShowValue()));
                    if (tarData.isEditAble()) {
                        if (tarData.isValConflict()) {
                            holder.numberText.setTextColor(ResourceUtils.getResColor(R.color.normal_import_color));
                        }else {
                            holder.numberText.setTextColor(ResourceUtils.getResColor(R.color.normal_blue_color));
                        }
                        holder.numberText.setVisibility(tarData.isShowProbableValue()?View.INVISIBLE:View.VISIBLE);
                        List<Integer> probableValueList = tarData.getProbableValueList();
                        for (int i = 0; i < 9; i++) {
                            holder.probableContainer.getChildAt(i).setVisibility(probableValueList.contains(i+1)?View.VISIBLE:View.INVISIBLE);
                        }
                    }
                }
                return true;
            }
        };
    }

    public static class SudokuViewHolder extends BaseViewHolder {

        TextView numberText;
        View topBorder;
        View bottomBorder;
        View leftBorder;
        View rightBorder;
        View backView;
        ConstraintLayout probableContainer;

        public SudokuViewHolder(@NonNull View itemView) {
            super(itemView);
            numberText = itemView.findViewById(R.id.sudoku_number);
            topBorder = itemView.findViewById(R.id.top_border);
            bottomBorder = itemView.findViewById(R.id.bottom_border);
            leftBorder = itemView.findViewById(R.id.left_border);
            rightBorder = itemView.findViewById(R.id.right_border);
            backView = itemView.findViewById(R.id.sudoku_back);
            probableContainer = itemView.findViewById(R.id.sudoku_number_probable_container);
        }
    }
}
