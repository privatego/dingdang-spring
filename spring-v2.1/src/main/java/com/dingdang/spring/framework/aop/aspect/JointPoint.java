package com.dingdang.spring.framework.aop.aspect;

import java.lang.reflect.Method;

/**
 * @author: blessed
 * @Date: 2019/4/16
 */
public interface JointPoint {
    Method getMethod();
    Object[] getArguments();
    Object getThis();
}
