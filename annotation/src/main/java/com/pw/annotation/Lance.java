package com.pw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * APT  : Annotation Processor Tools
 * 注解处理器
 *
 * 运行：  .java -> javac -> .class
 *
 * .java -> javac时 ， 采集到所有的注解信息  -> Element -> 注解处理程序
 *
 */


//元注解  ： 注解上的注解
@Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD})


/**
 * 保留级别
 * SOURCE:保留到源码层    这种类型的Annotation只会在源代码级别保留，编译后就会被忽略，所以不会保留在class文件中。
 *                      在编译器能够获取注解与注解声明的类包括类中所有成员信息，
 *                      一般用户生成额外的辅助类
 *
 *                      应用场景：
 *                          1、IDE语法检查  IDE实现、IDE插件实现
 *                              例： @DrawableRes   @IntDef
 *                          2、APT注解处理器
 *
 * CLASS：保留到字节码层   这种类型的Annotation编译时会被保留，在class文件中存在，但是不被JVM读取。
 *                      在编译出Clss后，通过修改Class数据以实现修改代码逻辑目的，对于是否需要修改的区分或者修改为不同逻辑的判断可以使用注解
 *
 * RUNTIME：保留到运行层   这种类型的Annotation将会被JVM保留，所以它们能在运行的时候被JVM或者是其他反射机制的代码进行读取和使用。
 *                      在程序运行期间，通过反射技术动态获取注解与其元素，从而完成不同的逻辑判定
 */
@Retention(RetentionPolicy.SOURCE)
public @interface Lance {
    String value();

    String id();
}
