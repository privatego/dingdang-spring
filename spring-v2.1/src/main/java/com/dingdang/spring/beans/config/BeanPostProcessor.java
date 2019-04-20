package com.dingdang.spring.beans.config;

//用于用事件监听
public class BeanPostProcessor {
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName)  {

        return bean;
    }
}

