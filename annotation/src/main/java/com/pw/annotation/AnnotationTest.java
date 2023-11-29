package com.pw.annotation;

public class AnnotationTest {

    @TestAnnotation(name = "ads")
    String value;

    @TestAnnotation(name = "method")
    public static void testMethod(@TestAnnotation(name = "methodParam") String param){

    }

    @TestAnnotation
    public static void main(@TestAnnotation String[] args) {

        @TestAnnotation(name = "asd")
        String a = new String("test");
        AnnotationUtils.getClassAnnotationInfo(AnnotationTest.class);

    }

}
