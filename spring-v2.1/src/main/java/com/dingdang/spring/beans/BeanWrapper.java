package com.dingdang.spring.beans;

import com.dingdang.spring.core.FactoryBean;

public class BeanWrapper extends FactoryBean {


    private Object wrapperInstance;//包装的对象
    private Class<?> wrappedClass;//原生对象，通过反射new出来的


    public BeanWrapper(Object wrapperInstance) {
        // 从这里开始，把动态的代码添加进来
        this.wrapperInstance = wrapperInstance;
    }

    public Class<?> getWrappedClass() {
        return wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }
}
