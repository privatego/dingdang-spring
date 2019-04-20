package com.dingdang.spring.context;

import com.dingdang.spring.beans.BeanDefinition;
import com.dingdang.spring.beans.config.BeanPostProcessor;
import com.dingdang.spring.beans.BeanWrapper;
import com.dingdang.spring.beans.support.BeanDefinitionReader;
import com.dingdang.spring.beans.support.DefaultListableBeanFactory;
import com.dingdang.spring.core.BeanFactory;
import com.dingdang.spring.framework.annotation.Autowired;
import com.dingdang.spring.framework.annotation.Controller;
import com.dingdang.spring.framework.annotation.Service;
import com.dingdang.spring.framework.aop.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader reader;

    /**
     * 用来保证注册时容器中的单例
     */
    private Map<String, Object> factoryBeanObjectCache = new HashMap<>();

    /**
     * 存放所有被代理过的对象
     */
    private Map<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();


    public ApplicationContext(String ...locations) {
        this.configLocations = locations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }

    // 依赖注入，从这里开始
    //通过读取BeanDefinition中的信息
    //然后，通过反射机制创建一个实例并返回
    //在Spring中，不会把最原始的对象放出去，会用一个BeanWrapper来进行一次包装
    //装饰器模式：
    // 1.保留原来的oop关系；
    // 2.需要对它进行扩展，增加（为以后AOP打下基础）
    @Override
    public Object getBean(String beanName) throws Exception {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        try {
            //生成通知事件
            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();

            Object instance = instantiateBean(beanName, beanDefinition);
            if (null == instance){
                return null;
            }

            // 在实例化以前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            this.factoryBeanInstanceCache.put(beanName, beanWrapper);
//            beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
//            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            // 在实例化以后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            populateBean(beanName, instance);

            //通过这样一调用，相当于给我们自己留有了可操作的空间
            return factoryBeanInstanceCache.get(beanName).getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private AdvisedSupport instantionAopConfig(BeanDefinition beanDefinition) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        AopConfig config = new AopConfig();
        config.setPointCut(this.reader.getConfig().getProperty("pointCut"));
        config.setAspectClass(this.reader.getConfig().getProperty("aspectClass"));
        config.setAspectBefore(this.reader.getConfig().getProperty("aspectBefore"));
        config.setAspectAfter(this.reader.getConfig().getProperty("aspectAfter"));
        config.setAspectAfterThrow(this.reader.getConfig().getProperty("aspectAfterThrow"));
        config.setAspectAfterThrowingName(this.reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new AdvisedSupport(config);
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
    private Object instantiateBean(String beanName, BeanDefinition beanDefinition){
        String className = beanDefinition.getBeanClassName();
        Object instance = null;
        try {
            if (this.factoryBeanObjectCache.containsKey(className)){
                instance = this.factoryBeanObjectCache.get(className);
            }else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();

                //AOP
                AdvisedSupport config = instantionAopConfig(beanDefinition);
                config.setTargetClass(clazz);
                config.setTarget(instance);
                if (config.pointCutMatch()){
                    instance = createProxy(config).getProxy();
                }

                this.factoryBeanObjectCache.put(className, instance);
                this.factoryBeanObjectCache.put(beanDefinition.getFactoryBeanName(), instance);
            }
            return instance;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private AopProxy createProxy(AdvisedSupport config) {
        Class targetClass = config.getTargetClass();
        if (targetClass.getInterfaces().length > 0){
            return new JdkDynamicAopProxy(config);
        }
        return new CglibAopProxy(config);
    }

    //依赖注入
    private void doAutowired(){
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()){
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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

        Field[] fields = clazz.getDeclaredFields();
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
                if (factoryBeanInstanceCache.get(autowiredBeanName) == null){
                    continue;
                }
                field.set(instance, factoryBeanInstanceCache.get(autowiredBeanName).getWrapperInstance());
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
