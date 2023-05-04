package com.pw.codeset.abilities.games.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SudokuView extends ConstraintLayout {
    public SudokuView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SudokuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SudokuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    TableLayout mSudokuTable;
    Map<Integer, List<SudokuNumberView>> mLineMap;
    Map<Integer, List<SudokuNumberView>> mColumnMap;
    Map<Integer, List<SudokuNumberView>> mBlockMap;
    List<SudokuNumberView> mNumberViews;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_sudoku, this, true);
        mSudokuTable = findViewById(R.id.sudoku_table);
    }

    public void start(List<SudokuNumberBean> numbers) {
        if (mNumberViews == null || mNumberViews.isEmpty()) {
            generateBaseViews();
        }
        if (numbers != null && numbers.size() == mNumberViews.size()) {
            for (int i = 0; i < mNumberViews.size(); i++) {
                mNumberViews.get(i).setValue(numbers.get(i));
            }
        }
    }


    private void generateBaseViews() {
        mNumberViews = new ArrayList<>();
        mColumnMap = new HashMap<>();
        mLineMap = new HashMap<>();
        mBlockMap = new HashMap<>();

        for (int i = 0; i < mSudokuTable.getChildCount(); i++) {
            View rowChild = mSudokuTable.getChildAt(i);
            if (rowChild instanceof TableRow) {
                for (int j = 0; j < ((TableRow) rowChild).getChildCount(); j++) {
                    View numChild = ((TableRow) rowChild).getChildAt(j);
                    if (numChild instanceof SudokuNumberView) {

                        numChild.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                onItemClick(((SudokuNumberView) v).getNumberValue());
                                return false;
                            }
                        });

                        mNumberViews.add((SudokuNumberView) numChild);
                        if (mColumnMap.get(j) == null) {
                            mColumnMap.put(j, new ArrayList<>());
                        }
                        mColumnMap.get(j).add((SudokuNumberView) numChild);

                        if (mLineMap.get(i) == null) {
                            mLineMap.put(i, new ArrayList<>());
                        }
                        mLineMap.get(i).add((SudokuNumberView) numChild);

                        int blockIndex = (i / 3 * 3) + (j / 3);

                        if (mBlockMap.get(blockIndex) == null) {
                            mBlockMap.put(blockIndex, new ArrayList<>());
                        }
                        mBlockMap.get(blockIndex).add((SudokuNumberView) numChild);
                    }
                }
            }
        }

    }


    private void onItemClick(SudokuNumberBean numberBean) {
        for (SudokuNumberView mNumberView : mNumberViews) {
            mNumberView.onOtherNumberClick(numberBean);
        }
    }

}
