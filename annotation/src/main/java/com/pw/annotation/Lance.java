package com.pw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
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
 * 最终生成的是一个代理类
 *          XX extends Proxy implements Annotation
 *
 */


/**
 * 优点
 * 1、生成文档。
 *      最常见的，也是java最早提供的注解。常用的有@param，@return等
 * 2、跟踪代码依赖性，实现替代配置文件功能。
 *      比如Dagger2 依赖注入，未来java开发，将大量注解配置，具有很大用处
 * 3、在编译时进行格式检查。
 *      如@Override放在方法钱，如果你这个方法并不是覆盖了超类方法，则编译时就能检查出来。
 * 4、代码中包含一些重复性的工作，考虑使用注解来简化与自动化该过程。
 *      例如butterKnife
 * 5、做一些配置参数的业务。
 *
 *
 * 缺点：
 * 分散在各个java文件中，不好维护
 */


/**
 元注解  ： 注解上的注解
 元注解包含：
     @Target - 注解用于什么地方 可用：ElementType.*
     @Documented - 注解是否将包含在javaDoc中
     @Retention - 什么时候使用该注解 生命周期 可用：RUNTIME、CLASS、SOURCE
     @Inherited - 是否允许子类继承该注解
 */

@Target({ElementType.TYPE,ElementType.FIELD,ElementType.METHOD})


/**
 * 注解是否将包含在JavaDoc中
 */
//@Documented

/**
 * 保留级别
 * (默认)SOURCE:保留到源码层    这种类型的Annotation只会在源代码级别保留，编译后就会被忽略，所以不会保留在class文件中。
 *                      在编译器能够获取注解与注解声明的类包括类中所有成员信息，
 *                      一般用户生成额外的辅助类
 *
 *                      应用场景：
 *                          1、IDE语法检查  IDE实现、IDE插件实现
 *                              例： @DrawableRes   @IntDef
 *                          2、APT注解处理器
 *
 * CLASS：保留到字节码层   这种类型的Annotation编译时会被保留，在class文件中存在，但是不被JVM读取。
 *                      在编译出Class后，通过修改Class数据以实现修改代码逻辑目的，对于是否需要修改的区分或者修改为不同逻辑的判断可以使用注解
 *
 * RUNTIME：保留到运行层   这种类型的Annotation将会被JVM保留，所以它们能在运行的时候被JVM或者是其他反射机制的代码进行读取和使用。
 *                      在程序运行期间，通过反射技术动态获取注解与其元素，从而完成不同的逻辑判定
 */
@Retention(RetentionPolicy.SOURCE)

/**
 * 参数成员
 * 1、可使用的类型是有限的，
 *      只能使用基本类型：
 *      byte、short、char、int、long、float、double、boolean
 *      以及
 *      String、Enum、class、annotations等数据类型，
 *      以及
 *      这一些类型的数组
 * 2、只能用public或默认（default）两个访问权修饰
 *
 * 3、要获取类方法和字段的注解信息，必须通过反射
 */

public @interface Lance {
    String value() default "test";

    String id();
}
