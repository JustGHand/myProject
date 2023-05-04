package com.pw.codeset.abilities.games.sudoku;


import java.util.ArrayList;
import java.util.List;

public class SudokuCreator {


    public SudokuCreator() {
    }

    int[] resultArray = new int[81];


    public List<SudokuNumberBean> createSudoku(int level){
        List<SudokuNumberBean> sudokuList = new ArrayList<>();
        createBase();
        randomChangeLine();
        randomChangeColumn();

        List<Integer> showList = new ArrayList<>();

//        int lastLineCount = -1;
//
//        for (int i = 0; i < 9; i++) {
//            int leftCount = 17 - showList.size();
//            int maxCount = leftCount > 3 ? 3 : leftCount;
//            int showCountForNumber = (int) (Math.random() * maxCount);
//            if (lastLineCount >= 0 && lastLineCount <= 1) {
//                showCountForNumber+=1;
//            }
//            lastLineCount = showCountForNumber;
//            if (i == 8) {
//                showCountForNumber = leftCount;
//            }
//            for (int i1 = 0; i1 < showCountForNumber; i1++) {
//                int showPos = 0;
//                do {
//                    int randomIndexInNumber = (int) (Math.random() * 9);
//                    showPos = i * 9 + randomIndexInNumber;
//                } while (showList.contains(showPos));
//                showList.add(showPos);
//            }
//        }

//        List<Integer> showList = new ArrayList<>();
//        for (int i = 0; i < 17; i++) {
//            int showPos = 0;
//            do {
//                showPos = (int) (Math.random() * 81);
//            } while (showList.contains(showPos));
//            showList.add(showPos);
//        }

        for (int i = 0; i < resultArray.length; i++) {
            SudokuNumberBean sudokuNumberBean = new SudokuNumberBean(resultArray[i], i);
//            if (showList.contains(i)) {
//                sudokuNumberBean.show();
//            }
            sudokuList.add(sudokuNumberBean);
        }
        return sudokuList;
    }

    private void createBase() {
        int value = 0;
        for (int i = 0; i < 81; i++) {
            List<Integer> posEliminateList = getPosEliminateList(i);
            do {
                value++;
                if (value > 9) {
                    value = 1;
                }
            } while (posEliminateList.contains(value));
            resultArray[i] = value;
        }
    }

    private void randomChangeLine() {
        int changeTime = 1;
        do {
            int randomLineIndex = (int) (Math.random() * 9);
            int tarLineIndex = 0;
            if (randomLineIndex % 3 == 0) {
                tarLineIndex = randomLineIndex + 2;
            }else if (randomLineIndex % 3 == 1){
                tarLineIndex = randomLineIndex + 1;
            }else {
                tarLineIndex = randomLineIndex - 2;
            }
            for (int i = 0; i < 9; i++) {
                int firstLineValue = resultArray[tarLineIndex * 9 + i];
                resultArray[tarLineIndex * 9 + i] = resultArray[randomLineIndex * 9 + i];
                resultArray[randomLineIndex * 9 + i] = firstLineValue;
            }
            changeTime++;
        } while (changeTime < 10);
    }

    private void randomChangeColumn() {
        int changeTime = 1;
        do {
            int randomColumnIndex = (int) (Math.random() * 9);
            int tarColumnIndex = 0;
            if (randomColumnIndex % 3 == 0) {
                tarColumnIndex = randomColumnIndex + 2;
            }else if (randomColumnIndex % 3 == 1){
                tarColumnIndex = randomColumnIndex + 1;
            }else {
                tarColumnIndex = randomColumnIndex - 2;
            }
            for (int i = 0; i < 9; i++) {
                int firstLineValue = resultArray[i * 9 + tarColumnIndex];
                resultArray[i * 9 + tarColumnIndex] = resultArray[i * 9 + randomColumnIndex];
                resultArray[i * 9 + randomColumnIndex] = firstLineValue;
            }
            changeTime++;
        } while (changeTime < 10);
    }


    /**
     * 获取当前位置不可用的数字
     * @param pos
     * @return
     */
    private List<Integer> getPosEliminateList(int pos) {
        List<Integer> eliminateList = new ArrayList<>();
        int[] sameLineNumbers = getSameLineNumbers(pos);
        int[] sameColumnNumbers = getSameColumnNumbers(pos);
        int[] sameBlockNumbers = getSameBlockNumbers(pos);

        for (int eliminateNumber : sameBlockNumbers) {
            if (!eliminateList.contains(eliminateNumber) && eliminateNumber != 0) {
                eliminateList.add(eliminateNumber);
            }
        }
        for (int eliminateNumber : sameLineNumbers) {
            if (!eliminateList.contains(eliminateNumber) && eliminateNumber != 0) {
                eliminateList.add(eliminateNumber);
            }
        }

        for (int eliminateNumber : sameColumnNumbers) {
            if (!eliminateList.contains(eliminateNumber) && eliminateNumber != 0) {
                eliminateList.add(eliminateNumber);
            }
        }

        return eliminateList;
    }

    /**
     * 获取指定位置的同一行的数据
     * @param pos
     * @return 长度为9的数组，未填充时为0
     */
    private int[] getSameLineNumbers(int pos){
        int[] sameLineNumbers = new int[9];
        int tarLineIndex = pos / 9;

        for (int i = 0; i < 9; i++) {
            sameLineNumbers[i] = getNumber(tarLineIndex, i);
        }

        return sameLineNumbers;
    }

    /**
     * 获取指定位置的同一列的数据
     * @param pos
     * @return 长度为9的数组，未填充时为0
     */
    private int[] getSameColumnNumbers(int pos) {
        int[] sameColumnNumbers = new int[9];
        int tarColumnIndex = pos % 9;
        for (int i = 0; i < 9; i++) {
            sameColumnNumbers[i] = getNumber(i, tarColumnIndex);
        }

        return sameColumnNumbers;
    }

    /**
     * 获取同一九宫格内的数据
     * @param pos
     * @return
     */
    private int[] getSameBlockNumbers(int pos){
        int[] sameBlockNumbers = new int[9];
        int tarColumnIndex = pos % 9;
        int tarLineIndex = pos / 9;
        int tarBlockStartLineIndex = tarLineIndex / 3 * 3;
        int tarBlockStartColumnIndex = tarColumnIndex /3 * 3;
        for (int i = 0; i < 3; i++) {//行
            for (int j = 0; j < 3; j++) {//列
                int tarLine = tarBlockStartLineIndex + i;
                int tarColumn = tarBlockStartColumnIndex + j;
                sameBlockNumbers[i * 3 + j] = getNumber(tarLine, tarColumn);
            }
        }
        return sameBlockNumbers;
    }


    /**
     * 获取指定行列的内容数字
     * @param line
     * @param column
     * @return
     */
    private int getNumber(int line, int column) {
        return resultArray[line * 9 + column];
    }

}
