package com.dingdang.spring.framework.aop.intercept;

import com.dingdang.spring.framework.aop.aspect.JointPoint;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public class MethodInvocation implements JointPoint {
    private Object proxy;
    private Method method;
    private Object target;
    private Class<?> targetClass;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMatchers;

    private int currentInterceptorIndex = -1;

    public MethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class targetClass, List<Object> interceptorsAndDynamicMethodMatcher) {
        this.proxy = proxy;
        this.target = target;
        this.targetClass = targetClass;
        this.method = method;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatcher;
    }

    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1){
            return this.method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof MethodInterceptor){
            MethodInterceptor mi = (MethodInterceptor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else {
            return proceed();
        }
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    @Override
    public Object getThis() {
        return this.target;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
