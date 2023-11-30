package com.pw.other.annotation.inject;

import android.app.Activity;
import android.view.View;

import com.pw.annotation.inject.InjectView;

import java.lang.reflect.Field;

public class InjectUtils {

    public static void injectView(Activity activity) {

        Class<? extends Activity> clz = activity.getClass();

        //获得此类所有的成员
        Field[] declaredFields = clz.getDeclaredFields();
        /*
        clz.getDeclaredFields()     :只能获取自己的成员（不包括父类，可获取所有作用域）
        clz.getField()              :获取自己+父类的成员（不包括private私有对象，只能获取public）
         */


        for (Field field : declaredFields) {

            //判断field是否被注解InjectView声明
            if (field.isAnnotationPresent(InjectView.class)) {
                InjectView injectView = field.getAnnotation(InjectView.class);
                int id = injectView.value();
                View view = activity.findViewById(id);
                //反射设置属性值
                field.setAccessible(true);//设置作用域的访问权限，允许操作private属性
                try {
                    field.set(activity,view);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
