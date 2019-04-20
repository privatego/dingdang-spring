package com.dingdang.spring.framework.aop.aspect;

import com.dingdang.spring.framework.aop.intercept.Advice;
import com.dingdang.spring.framework.aop.intercept.MethodInterceptor;
import com.dingdang.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: blessed
 * @Date: 2019/4/17
 */
public class MethodBeforeAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {
    private JointPoint jointPoint;

    public MethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        invokeAdviceMethod(this.jointPoint, null, null);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.jointPoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
