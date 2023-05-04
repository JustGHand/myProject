package com.pw.javalib.proxy;


/**
 * 抽象主题
 *
 * 定义了真实主题类（被委托类、被代理类）与代理类（委托类）的共同的业务接口
 */
public interface MySubject {
    void doA();//共同业务
}
