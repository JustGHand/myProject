package com.pw.annotation.inject;

public class InjectViewUtil {
    public static void bind(Object object) {
        String name = object.getClass().getName() + InjectViewConstant.INJECT_CLASS_SUFFIX;
        try {
            Class<?> aClass = Class.forName(name);
            PWBinder pwBinder = (PWBinder) aClass.newInstance();
            pwBinder.bind(object);
        } catch (Exception e) {

        }
    }
}
