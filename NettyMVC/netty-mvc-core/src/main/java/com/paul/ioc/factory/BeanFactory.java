package com.paul.ioc.factory;

public abstract class BeanFactory {

    public Object getBean(String beanName){
        return doGetBean(beanName);
    }

    //交给子类，容器实现类去完成
    protected abstract Object doGetBean(String beanName);

}
