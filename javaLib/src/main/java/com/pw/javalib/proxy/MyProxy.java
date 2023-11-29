package com.pw.javalib.proxy;


/**
 * 代理类（委托类）
 *
 *
 * 实现共同的业务接口，
 * 为了让客户端能够通过代理类的业务接口间接的访问真实主题类的业务
 *
 * 持有一个真实主题类的引用
 */
public class MyProxy implements MySubject{

    private String managerName;

    private MyRealSubject realSubject;//真实主题类的引用

    public MyProxy(String managerName, MyRealSubject realSubject) {
        this.managerName = managerName;
        this.realSubject = realSubject;
    }

    @Override
    public void doA() {
        System.out.println(managerName+"执行代理业务");
        //客户端间接访问真实业务
        realSubject.doA();
    }
}
