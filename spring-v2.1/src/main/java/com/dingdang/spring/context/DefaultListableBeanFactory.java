package com.dingdang.spring.context;

import com.dingdang.spring.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractApplicationContext {


    //beanDefinitionMap用来保存配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    @Override
    protected void refreshBeanFactory() {

    }

}
