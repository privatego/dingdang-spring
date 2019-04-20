package com.dingdang.spring.framework.aop;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public interface AopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
