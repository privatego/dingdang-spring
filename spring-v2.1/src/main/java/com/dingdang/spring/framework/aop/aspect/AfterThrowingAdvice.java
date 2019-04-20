package com.dingdang.spring.framework.aop.aspect;

import com.dingdang.spring.framework.aop.intercept.Advice;
import com.dingdang.spring.framework.aop.intercept.MethodInterceptor;
import com.dingdang.spring.framework.aop.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author: blessed
 * @Date: 2019/4/17
 */
public class AfterThrowingAdvice extends AbstractAspectJAdvice implements Advice, MethodInterceptor {

    private String throwingName;
    private JointPoint jointPoint;


    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    public AfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }catch (Throwable ex){
            invokeAdviceMethod(mi, null, ex.getCause());
            throw ex;
        }
    }

}
