package com.pw.codeset.abilities.games.sudoku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.pw.codeset.R;

public class SudokuNumberView extends ConstraintLayout {
    public SudokuNumberView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SudokuNumberView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SudokuNumberView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    TextView mNumberText;
    View mBorder;

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_sudoku_number, this, true);
        mNumberText = findViewById(R.id.sudoku_number);
        mBorder = findViewById(R.id.sudoku_number_border);
    }

    SudokuNumberBean mNumberValue;

    public void setValue(SudokuNumberBean numberBean) {
        mNumberValue = numberBean;
        mNumberText.setText(String.valueOf(mNumberValue.getValue()));
    }

    public SudokuNumberBean getNumberValue() {
        return mNumberValue;
    }

    public void onOtherNumberClick(SudokuNumberBean sudokuNumberBean) {
        if (sudokuNumberBean != null) {
            if (sudokuNumberBean.getBlockIndex() != mNumberValue.getBlockIndex()
                    && sudokuNumberBean.getLineIndex() != mNumberValue.getLineIndex()
                    && sudokuNumberBean.getColumnIndex() != mNumberValue.getColumnIndex()) {
                mBorder.setVisibility(INVISIBLE);
            }else {
                mBorder.setVisibility(VISIBLE);
            }
        }
    }
}
