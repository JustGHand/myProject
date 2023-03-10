package com.pw.javalib.reflect;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeTest {

    static class TestData<T>{
        T data;

        public TestData(T data) {
            this.data = data;
        }
    }

    static class MyData{
        String title;

        public MyData(String title) {
            this.title = title;
        }
    }


    class TypeRefrerence<T>{
        Type type;

        public TypeRefrerence() {
            Type genericSuperclass = getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            type = actualTypeArguments[0];
        }
    }


    public static void main(String[] args) {
        TestData<MyData> data = new TestData<>(new MyData("test"));
        String json = new Gson().toJson(data);
        print(json);

        Type type = new TypeToken<TestData<MyData>>() {
        }.getType();

        print(type);

    }


    public static void print(Object object) {
        System.out.println("==================");
        System.out.println(object);
    }

}
