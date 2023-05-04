package com.pw.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER})
public @interface TestAnnotation {



    String name() default "test";
}
