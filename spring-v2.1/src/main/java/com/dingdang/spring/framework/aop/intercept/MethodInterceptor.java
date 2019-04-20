package com.dingdang.spring.framework.aop.intercept;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public interface MethodInterceptor {
    Object invoke(MethodInvocation methodInvocation) throws Throwable;
}
