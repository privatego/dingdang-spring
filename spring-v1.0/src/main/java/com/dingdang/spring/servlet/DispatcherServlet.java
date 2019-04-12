package com.dingdang.spring.servlet;

import com.dingdang.spring.annotation.Autowired;
import com.dingdang.spring.annotation.Controller;
import com.dingdang.spring.annotation.Service;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String, Object> beanMap = new ConcurrentHashMap<>(); // <beanName, instance>

    private List<String> classNames = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------- do Post ----------");

    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        // 开始初始化的进程

        // 1.定位
        doLoadConfig(config.getInitParameter("contextConfigLocation"));

        // 2.加载
        doScanner(contextConfig.getProperty("scanPackage"));

        // 3.注册
        doRegistry();

        // 4.自动依赖注入（在spring中，是调用getBean方法才触发注入的。）
        doAutowired();

        // 5.初始化handlerMapping映射关系
        initHandlerMapping();
    }
    private void doLoadConfig(String location) {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(location.replace("classpath:", ""));
        try {
            contextConfig.load(is);
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
    }


    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()){
            if(file.isDirectory()){
                doScanner(packageName + "." +file.getName());
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class",""));
            }
        }

    }

    private void doRegistry() {
        if (classNames.isEmpty()){
            return;
        }
        try {
            for (String className : classNames){
                Class<?> clazz = Class.forName(className);
                //在Spring中用的是多个子方法来处理的（策略模式）
                // parseArray, parseMap
                if(clazz.isAnnotationPresent(Controller.class)){
                    String beanName = lowerFirstCase(clazz.getSimpleName());
                    //在Spring中在这个阶段不是不会直接put instance，这里put的是BeanDefinition
                    beanMap.put(beanName,clazz.newInstance());
                }else if(clazz.isAnnotationPresent(Service.class)){
                    Service service = clazz.getAnnotation(Service.class);


                    //如果自己定义了beanName，那么优先使用自己定义的beanName
                    String beanName = service.value();
                    if("".equals(beanName.trim())){
                        beanName = lowerFirstCase(clazz.getSimpleName());//默认用类名首字母注入
                    }
                    Object instance = clazz.newInstance();
                    beanMap.put(beanName,instance);

                    //如果是一个接口，使用接口的类型去自动注入
                    //在Spring中同样会分别调用不同的方法 autowriedByName autowritedByType
                    Class<?>[] interfaces = clazz.getInterfaces();

                    for (Class<?> i :interfaces){
                        beanMap.put(i.getName(),instance);
                    }

                }else{
                    continue;
                }


            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void doAutowired() {
        if (beanMap.isEmpty())return;

        for (Map.Entry<String, Object> entry : beanMap.entrySet()){
            Field[] fields = entry.getValue().getClass().getDeclaredFields();
            for (Field field : fields){
                if (!field.isAnnotationPresent(Autowired.class)){
                    continue;
                }
                Autowired autowired = field.getAnnotation(Autowired.class);

                String beanName = autowired.value().trim();
                if ("".equals(beanName)){
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), beanMap.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initHandlerMapping() {

    }





    private String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
