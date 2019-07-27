package com.test.java;

import com.paul.ioc.bean.AnnotationApplicationContext;

public class Test {

    @org.junit.Test
    public void test(){
        AnnotationApplicationContext annotationApplicationContext = new AnnotationApplicationContext("applicationContext.xml");



    }
}
