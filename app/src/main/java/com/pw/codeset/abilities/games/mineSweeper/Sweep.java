package com.pw.codeset.abilities.games.mineSweeper;

import java.util.Observable;
import java.util.Observer;

public class Sweep implements Observer {
    boolean isSweep;
    int curStatus; //1：点开 ； 2：标记 ； 3：boom

    public void setSweep(boolean isSweep) {
        this.isSweep = isSweep;
    }

    public void updateStatus(int status) {
        curStatus = status;
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}
