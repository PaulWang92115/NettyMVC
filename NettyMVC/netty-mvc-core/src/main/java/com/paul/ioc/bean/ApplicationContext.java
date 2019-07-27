package com.paul.ioc.bean;

import com.paul.ioc.factory.BeanFactory;
import com.paul.ioc.xml.XmlUtil;

public abstract class ApplicationContext extends BeanFactory {

    protected String configuration;
    protected XmlUtil xmlUtil;

    public ApplicationContext(String configuration){
        this.configuration = configuration;
        this.xmlUtil = new XmlUtil();
    }
}

