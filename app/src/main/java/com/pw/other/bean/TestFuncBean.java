package com.pw.other.bean;

public class TestFuncBean {
    String title;
    Class tarAct;

    public TestFuncBean(String title, Class tarAct) {
        this.title = title;
        this.tarAct = tarAct;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Class getTarAct() {
        return tarAct;
    }

    public void setTarAct(Class tarAct) {
        this.tarAct = tarAct;
    }
}
