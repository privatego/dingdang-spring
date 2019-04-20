package com.dingdang.spring.framework.aop.aspect;

import com.dingdang.spring.framework.aop.intercept.Advice;
import com.dingdang.spring.framework.aop.intercept.MethodInterceptor;
import com.dingdang.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: blessed
 * @Date: 2019/4/17
 */
public class AfterReturningAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {

    private JointPoint jointPoint;

    public AfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.jointPoint = mi;
        this.afterReturning(retVal, mi.getMethod(), mi.getArguments(), mi.getThis());
        return retVal;
    }

    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(jointPoint, returnValue, null);
    }
}
