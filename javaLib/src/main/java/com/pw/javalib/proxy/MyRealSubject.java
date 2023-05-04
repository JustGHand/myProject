package com.pw.javalib.proxy;


/**
 * 真实主题类
 *
 *
 * 实现具体的业务
 */

public class MyRealSubject implements MySubject{

    private String subjectName;

    public MyRealSubject(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public void doA() {
        System.out.println(subjectName+"执行真实业务");
    }
}
