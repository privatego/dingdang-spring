package com.dingdang.spring.context;

import com.dingdang.spring.beans.BeanDefinition;
import com.dingdang.spring.beans.BeanPostProcessor;
import com.dingdang.spring.beans.BeanWrapper;
import com.dingdang.spring.context.support.BeanDefinitionReader;
import com.dingdang.spring.core.BeanFactory;
import com.dingdang.spring.framework.annotation.Autowired;
import com.dingdang.spring.framework.annotation.Controller;
import com.dingdang.spring.framework.annotation.Service;
import com.dingdang.spring.framework.aop.AopConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    /**
     * 保存配置时的Bean
     */
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    /**
     * 用来保证注册时容器中的单例
     */
    private Map<String, Object> beanCacheMap = new HashMap<>();

    /**
     * 存放所有被代理过的对象
     */
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<>();


    public ApplicationContext(String ...locations) {
        this.configLocations = locations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 依赖注入，从这里开始
    //通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //在Spring中，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    // 1.保留原来的oop关系；
    // 2.需要对它进行扩展，增加（为以后AOP打下基础）
    @Override
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        String className = beanDefinition.getBeanClassName();
        try {
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantionBean(beanDefinition);
            if (null == instance)return null;
            // 在初始化以前通知一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            BeanWrapper beanWrapper = new BeanWrapper(beanDefinition);
            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName, beanWrapper);
            // 在初始化以后通知一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return beanWrapperMap.get(beanName).getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private AopConfig instantionAopConfig(BeanDefinition beanDefinition) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        AopConfig config = new AopConfig();
        String expression = reader.getConfig().getProperty("pointCut");
        String[] before = reader.getConfig().getProperty("aspectBefore").split("\\s");
        String[] after = reader.getConfig().getProperty("aspectAfter").split("\\s");

        String className = beanDefinition.getBeanClassName();
        Class<?> clazz = Class.forName(className);

        Pattern pattern = Pattern.compile(expression);
        Class aspectClass = Class.forName(before[0]);
        for (Method m : clazz.getMethods()){
            Matcher matcher = pattern.matcher(m.toString());
            if (matcher.matches()){
                config.put(m, aspectClass.newInstance(), new Method[]{aspectClass.getMethod(before[1]),aspectClass.getMethod(after[1])});
            }
        }

        return config;
    }


    //初始化IOC容器
    public void refresh() throws Exception {
        //1.定位，定位配置文件
        this.reader = new BeanDefinitionReader(configLocations);
        //2.加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<BeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //注册
        doRegitryBeanDefinition(beanDefinitions);

        //4.把不是延时加载的类，提前初始化
        // 依赖注入（lazy-init=false），要执行依赖注入
        //在这里自动调用getBean方法
        doAutowired();

    }

    //真正的将BeanDefinitions注册到beanDefinitionMap中
    private void doRegitryBeanDefinition(List<BeanDefinition> beanDefinitions) throws Exception {

        for (BeanDefinition beanDefinition : beanDefinitions){
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())){
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
        //至此，容器初始化完毕
    }

    // 传一个BeanDefinition，就返回一个实例Bean
    private Object instantionBean(BeanDefinition beanDefinition){
        String className = beanDefinition.getBeanClassName();
        try {
            Object instance = null;
            if (!this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className, instance);
            }

            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    //依赖注入
    private void doAutowired(){
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
                Object obj = getBean(beanName);
                System.out.println(obj);
            }
        }
        for (Map.Entry<String, BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){
            populateBean(beanWrapperEntry.getKey(), beanWrapperEntry.getValue().getOriginalInstance());
        }
    }

    //真正注入
    public void populateBean(String beanName, Object instance){
        Class clazz = instance.getClass();
        if (!(clazz.isAnnotationPresent(Controller.class)
        || clazz.isAnnotationPresent(Service.class)
        )){
            return;
        }

        Field[] fields = clazz.getFields();
        for (Field field : fields){
            if (!field.isAnnotationPresent(Autowired.class)){
                continue;
            }

            Autowired autowired = field.getAnnotation(Autowired.class);

            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                field.set(instance, beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }






    public String[] getBeanDefinitionNames(){
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount(){
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig(){
        return this.reader.getConfig();
    }

}
