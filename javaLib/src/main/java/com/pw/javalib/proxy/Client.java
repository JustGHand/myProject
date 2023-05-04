package com.pw.javalib.proxy;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import sun.misc.ProxyGenerator;

/**
 * 代理模式客户端
 */
public class Client {

    /**
     * 1、静态代理
     * 编译期实现
     *      优点：
     *          效率高
     *      缺点：
     *          耦合高
     *          代理类与被代理类均需实现共同方法
     *
     *
     * 2、动态代理  retrofit
     * 运行期实现
     *      优点：
     *          解耦
     *
     *      缺点：
     *          效率低
     *
     */

    public static void main(String[] args) {

        MyRealSubject realSubjectA = new MyRealSubject("realSubjectA");


        //静态代理

        //构造一个代理类
        MySubjectManager managerA = new MySubjectManager("managerA", realSubjectA);

        //访问真实业务
        managerA.doA();



        //动态代理

        /**
         * ClassLoader : 加载类class文件，与加载真是主题类的classloader是同一个
         * Class<?>[] : 需要实现的共同的业务接口
         * InvocationHandler : 调用真实主题类的具体业务
         */


        /**
         * 通过getProxyClass0 获取字节码对象
         * 通过反射 动态构造一个代理返回
         */
        MySubject managerB = (MySubject) Proxy.newProxyInstance(realSubjectA.getClass().getClassLoader(),
                new Class[]{MySubject.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("managerB执行代理业务");
                        realSubjectA.doA();
                        return null;
                    }
                });

        generateProxyFile("subject",realSubjectA.getClass());

        managerB.doA();

    }

    public static void generateProxyFile(String fileName, Class<?> clazz) {
        byte[] classFile = ProxyGenerator.generateProxyClass(fileName, clazz.getInterfaces());
        String path = "./proxyFile/"+fileName+".class";
        try {
            FileOutputStream fos = new FileOutputStream(path);
            fos.write(classFile);
            fos.flush();
            System.out.println("代理类Class文件写入成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("代理类Class写入失败");
        }
    }

}
