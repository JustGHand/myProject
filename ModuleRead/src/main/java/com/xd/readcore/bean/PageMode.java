package com.xd.readcore.bean;

/**
 * Created by newbiechen on 2018/2/5.
 * 作用：翻页动画的模式
 */

public enum PageMode {
    SIMULATION("仿真",1),
    SLIDE("平移",2),
    COVER("覆盖",3),
    SCROLL("滚动",4),
    NONE("无",0),
    AUTO("自动",-1);


    private String name;
    private int index;

    PageMode(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
