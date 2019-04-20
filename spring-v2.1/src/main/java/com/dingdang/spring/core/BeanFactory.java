package com.dingdang.spring.core;

public interface BeanFactory {

    Object getBean(String beanName) throws Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
