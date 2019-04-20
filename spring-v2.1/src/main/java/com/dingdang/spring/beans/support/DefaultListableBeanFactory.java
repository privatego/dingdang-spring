package com.dingdang.spring.beans.support;

import com.dingdang.spring.beans.BeanDefinition;
import com.dingdang.spring.context.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractApplicationContext {


    //beanDefinitionMap用来保存配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    protected void refreshBeanFactory() {

    }

}
