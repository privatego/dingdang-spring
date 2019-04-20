package com.dingdang.spring.framework.aop.aspect;

import com.dingdang.spring.framework.aop.intercept.Advice;

import java.lang.reflect.Method;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public abstract class AbstractAspectJAdvice implements Advice {
    private Method aspectMethod;
    private Object aspectTarget;

    public AbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(JointPoint jointPoint, Object returnValue, Throwable ex) throws  Throwable{
        Class<?>[] paramsTypes = this.aspectMethod.getParameterTypes();
        if (null == paramsTypes || paramsTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else {
            Object[] args = new Object[paramsTypes.length];
            for (int i=0; i<paramsTypes.length; i++){
                if (paramsTypes[i] == JointPoint.class){
                    args[i] = jointPoint;
                }else if (paramsTypes[i] == Throwable.class){
                    args[i] = ex;
                }else if (paramsTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget, args);
        }

    }

}


