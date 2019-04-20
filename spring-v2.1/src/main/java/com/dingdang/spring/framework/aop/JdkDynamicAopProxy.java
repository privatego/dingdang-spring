package com.dingdang.spring.framework.aop;

import com.dingdang.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;


//默认就用JDK动态代理，代理首先要实现Invocationhandler接口
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler{

    private AdvisedSupport config;

    public JdkDynamicAopProxy(AdvisedSupport config) {
        this.config = config;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader){
        return Proxy.newProxyInstance(classLoader, this.config.getTargetClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> interceptorsAndDynamicMethodMatcher = config.getInterceptorsAndDynamicInterceptionAdvise(method, this.config.getTargetClass());
        MethodInvocation invocation = new MethodInvocation(proxy, this.config.getTarget(), method, args, this.config.getTargetClass(), interceptorsAndDynamicMethodMatcher);
        return invocation.proceed();
    }
}
