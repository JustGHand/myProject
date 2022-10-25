package com.pw.codeset.abilities.games.block;

import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pw.codeset.R;
import com.pw.codeset.application.MyApp;
import com.pw.codeset.base.BaseActivity;
import com.pw.codeset.weidgt.FlipCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BlockGameActivity extends BaseActivity {
    @Override
    protected int getContentId() {
        return R.layout.activity_game_block;
    }


    EditText mXInput;
//    EditText mYInput;

    Button mStartBtn;

    int mXCount;
//    int mYCount;

    TableLayout mTable;


    @Override
    protected void initView() {

        mXInput = findViewById(R.id.block_config_input_x);
        mXInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                startGame();
                return false;
            }
        });

        mTable = findViewById(R.id.block_table);

    }

    @Override
    protected void dealWithData() {
        mTable.post(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        });
    }

    @Override
    protected void onMenuClick(View view) {
        roundAllBlock(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roundAllBlock(false);
                    }
                });
            }
        }).start();
    }

    private void roundAllBlock(boolean start) {

        if (mCellList != null && !mCellList.isEmpty()) {
            Random random = new Random();
            for (int i = 0; i < mCellList.size(); i++) {
                FlipCard flipCard = mCellList.get(i);
                if (start) {
                    if (flipCard != null) {
                        boolean showFront = random.nextBoolean();
                        flipCard.showView(showFront);
                        flipCard.startRound();
                    }
                }else {
                    flipCard.endRound();
                }
            }
        }
    }

    private void startGame() {

        mXCount = Integer.parseInt(mXInput.getText().toString());
        if (!isCountEnable(mXCount)) {
            MyApp.getInstance().showToast("生成空间过小，请重新设置");
            return;
        }
        mTable.removeAllViews();
        if (mCellList != null) {
            mCellList.clear();
        }
        for (int i = 0; i < mXCount; i++) {
            TableRow tableRow = generateTableRow(i);
            mTable.addView(tableRow);
        }
    }

    private boolean isCountEnable(int count) {

        int cellWidth = mTable.getWidth() / count;
        int minSize = getResources().getDimensionPixelSize(R.dimen.clickable_minsize);
        if (cellWidth < minSize) {
            return false;
        }
        return true;
    }

    private TableRow generateTableRow(int rowIndex) {
        TableRow tableRow = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(layoutParams);
        int cellWidth = mTable.getWidth() / mXCount;
        Random random = new Random();
        for (int i = 0; i < mXCount; i++) {
            boolean showFront = random.nextBoolean();
            FlipCard cell = generateTableCell(rowIndex, i, cellWidth,showFront);
            tableRow.addView(cell);
        }
        return tableRow;
    }

    private List<FlipCard> mCellList;

    private FlipCard generateTableCell(int rowIndex, int cellIndex, int width,boolean showFront) {
        FlipCard cell = new FlipCard(this);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(width, width);
        cell.setLayoutParams(layoutParams);
        cell.setSelected(false);
        cell.setSpeed(500);
        cell.showView(showFront);
        cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCell(rowIndex, cellIndex);
            }
        });
        cell.setOnAnimListener(new FlipCard.onAnimListener() {
            @Override
            public void onStart() {
                cellAnimStart();
            }

            @Override
            public void onFinish() {
                cellAnimFinish();
            }
        });
        if (mCellList == null) {
            mCellList = new ArrayList<>();
        }
        mCellList.add(cell);
        return cell;
    }

    private void checkCell(int rowIndex, int cellIndex) {

        if (!isCellClickAble()) {
            return;
        }

        toggleCell(rowIndex, cellIndex);

        if (rowIndex > 0) {//上
            toggleCell(rowIndex-1,cellIndex);
        }
        if (rowIndex < mXCount - 1) {//下
            toggleCell(rowIndex + 1, cellIndex);
        }
        if (cellIndex > 0) {//左
            toggleCell(rowIndex,cellIndex-1);
        }
        if (cellIndex < mXCount - 1) {//右
            toggleCell(rowIndex, cellIndex + 1);
        }

    }

    private void toggleCell(int tarRow, int tarCell) {
        int realCellIndex = tarRow * mXCount + tarCell;
        if (mCellList != null && mCellList.size() > realCellIndex) {
            FlipCard cell = mCellList.get(realCellIndex);
            cell.flip();
        }
    }

    int runningAnimCount = 0;

    private void cellAnimStart() {
        runningAnimCount++;
    }

    private void cellAnimFinish() {
        runningAnimCount--;
        if (runningAnimCount <= 0) {
            checkResult();
        }
    }

    private boolean isCellClickAble() {
        if (runningAnimCount > 0) {
            return false;
        }
        return true;
    }

    private void checkResult() {
        if (mCellList != null && !mCellList.isEmpty()) {
            boolean firstStauts = true;
            for (int i = 0; i < mCellList.size(); i++) {
                if (i == 0) {
                    firstStauts = mCellList.get(i).isFrontShowing();
                }else {
                    if (mCellList.get(i).isFrontShowing() != firstStauts) {
                        return;
                    }
                }
            }
        }
        String toast = "完成";
        if (isCountEnable(mXCount + 1)) {
            toast = "升级";
            mXCount++;
            mXInput.setText(mXCount+"");
        }else {
            toast = "已是最高级别";
        }
        MyApp.getInstance().showToast(toast);
        startGame();
    }

}
