package com.dingdang.spring.context;

/**
 * 后面将通过一个监听器去扫描所有的类，只要实现了此接口，
 * 将自动调用setApplicationContext()方法，从而将IOC容器注入到目标类中
 */
public interface ApplicationContextAware {
    void setApplicationContext(ApplicationContext applicationContext);
}
