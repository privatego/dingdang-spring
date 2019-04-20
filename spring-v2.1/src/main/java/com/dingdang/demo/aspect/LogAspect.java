package com.dingdang.demo.aspect;

import com.dingdang.spring.framework.aop.aspect.JointPoint;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {


    public void before(JointPoint jointPoint){
        log.info("Invoker Before Method . TargetObject:" + jointPoint.getThis() + " , Args:" + Arrays.toString(jointPoint.getArguments()));

    }


    public void after(JointPoint jointPoint){
        log.info("Invoker After Method . TargetObject:" + jointPoint.getThis() + " , Args:" + Arrays.toString(jointPoint.getArguments()));
    }

    public void afterThrowing(JointPoint jointPoint, Throwable ex){
        log.info("Exception . TargetObject:" + jointPoint.getThis() + " , Args:" + Arrays.toString(jointPoint.getArguments()) + " , Throws:" + ex.getMessage());
    }
}
