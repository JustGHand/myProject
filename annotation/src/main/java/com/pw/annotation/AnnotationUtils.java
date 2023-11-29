package com.pw.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class AnnotationUtils {

    public static void getClassAnnotationInfo(Class<?> clazz) {
        if (clazz != null) {
            if(clazz.isAnnotationPresent(TestAnnotation.class)) {
                TestAnnotation annotation = clazz.getAnnotation(TestAnnotation.class);
                String name = annotation.name();
                System.out.println("class : " + clazz.getName()+"-- annotation name : " + name);
            }
            getFieldAnnotationInfo(clazz);
            getMethodAnnotationInfo(clazz);
        }
    }
    public static void getFieldAnnotationInfo(Class<?> clazz) {
        if (clazz != null) {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField != null) {
                    if (declaredField.isAnnotationPresent(TestAnnotation.class)) {
                        TestAnnotation annotation = declaredField.getAnnotation(TestAnnotation.class);
                        String name = annotation.name();
                        System.out.println("field : " + declaredField.getName()+"-- annotation name : "  + name);
                    }
                }
            }
        }
    }


    public static void getMethodAnnotationInfo(Class<?> clazz) {
        if (clazz != null) {
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod != null) {
                    if (declaredMethod.isAnnotationPresent(TestAnnotation.class)) {
                        TestAnnotation annotation = declaredMethod.getAnnotation(TestAnnotation.class);
                        String name = annotation.name();
                        System.out.println("method : " + declaredMethod.getName()+"-- annotation name : "  + name);
                    }
                    getMethodParameterNamesByAnnotation(declaredMethod);
                }
            }
        }
    }

    /**
     * 获取给 "方法参数" 进行注解的值
     *
     * @param method 要获取参数名的方法
     * @return 按参数顺序排列的参数名列表
     */
    public static void getMethodParameterNamesByAnnotation(Method method) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (parameterAnnotations == null || parameterAnnotations.length == 0) {
            return;
        }
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof TestAnnotation) {
                    TestAnnotation param = (TestAnnotation) annotation;
                    String name = param.name();
                    System.out.println("method param : " + "-- annotation name : "  + name);
                }
            }
        }
    }
}
