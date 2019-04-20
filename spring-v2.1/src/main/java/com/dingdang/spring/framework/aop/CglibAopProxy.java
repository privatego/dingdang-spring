package com.dingdang.spring.framework.aop;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public class CglibAopProxy implements AopProxy {
    private AdvisedSupport config;

    public CglibAopProxy(AdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
