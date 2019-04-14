package com.dingdang.spring.beans;

import com.dingdang.spring.core.FactoryBean;
import com.dingdang.spring.framework.aop.AopConfig;
import com.dingdang.spring.framework.aop.AopProxy;

public class BeanWrapper extends FactoryBean {

    private AopProxy aopProxy = new AopProxy();

    //还会用到 观察者模式
    private BeanPostProcessor beanPostProcessor;

    private Object wrapperInstance;//包装的对象
    private Object originalInstance;//原生对象，通过反射new出来的
    private AopConfig config;

    public BeanWrapper(Object instance) {
        // 从这里开始，把动态的代码添加进来
        this.wrapperInstance = aopProxy.getProxy(instance);
        this.originalInstance = instance;
    }


    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public void setWrapperInstance(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public void setOriginalInstance(Object originalInstance) {
        this.originalInstance = originalInstance;
    }

    public BeanPostProcessor getBeanPostProcessor() {
        return beanPostProcessor;
    }

    public void setBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessor = beanPostProcessor;
    }

    // 返回代理以后的class
    //可能会是这个$Proxy0
    public Class<?> getWrapperClass(){
        return this.wrapperInstance.getClass();
    }

    public AopConfig getAopConfig() {
        return config;
    }

    public void setAopConfig(AopConfig config) {
        this.config = config;
    }
}
