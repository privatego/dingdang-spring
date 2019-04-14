package com.dingdang.spring.context.support;

import com.dingdang.spring.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

//对配置文件进行查找，读取、解析
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<>();

    private String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String ...locations) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null != is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));

    }

    //每注册一个className，就返回一个BeanDefinition，
    public BeanDefinition registryBean(String className){
        if (this.registryBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".") + 1)));
            return beanDefinition;
        }

        return null;
    }

    //把配置文件扫描到的所有信息转换为BeanDefinition对象，便于之后IOC操作
    public List<BeanDefinition> loadBeanDefinitions(){
        List<BeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registryBeanClasses){
                Class<?> beanClass = Class.forName(className);
                if (beanClass.isInterface()){
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));
                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces){
                    result.add(doCreateBeanDefinition(i.getName(), beanClass.getName()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private BeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName) {
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName(beanClassName);
        beanDefinition.setFactoryBeanName(factoryBeanName);
        return beanDefinition;
    }

    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig(){
        return this.config;
    }


    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName + "." +file.getName());
            }else {
                if (!file.getName().endsWith(".class")){
                    continue;
                }
                registryBeanClasses.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
    }





    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
