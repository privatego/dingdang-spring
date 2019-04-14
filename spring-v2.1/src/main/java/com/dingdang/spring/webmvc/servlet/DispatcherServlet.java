package com.dingdang.spring.webmvc.servlet;


import com.dingdang.spring.context.ApplicationContext;
import com.dingdang.spring.framework.annotation.Controller;
import com.dingdang.spring.framework.annotation.RequestMapping;
import com.dingdang.spring.framework.annotation.RequestParam;
import com.dingdang.spring.framework.aop.AopProxyUtils;
import com.dingdang.spring.webmvc.HandlerAdapter;
import com.dingdang.spring.webmvc.HandlerMapping;
import com.dingdang.spring.webmvc.ModelAndView;
import com.dingdang.spring.webmvc.ViewResolver;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    private final String LOCATION = "contextConfigLocation";


    // HandlerMapping是Spring最核心的设计，也是最经典的设计。
    // 为什么要这样设计？
    private List<HandlerMapping> handlerMappings = new ArrayList<>();

    // 为什么要这样设计？
    private Map<HandlerMapping, HandlerAdapter> handlerAdapters = new ConcurrentHashMap<>();

    private List<ViewResolver> viewResolvers = new ArrayList<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("------- do Post ----------");

        try{
            doDispatch(req, resp);
        }catch (Exception e){
            try {
                resp.getWriter().write("500, Exception \\n " + Arrays.toString(e.getStackTrace()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        HandlerMapping handler = getHandler(req);
        if (handler == null){
            resp.getWriter().write("404 Not Found .");
        }

        HandlerAdapter ha = getHandlerAdapter(handler);
        ModelAndView mv = ha.handler(req, resp, handler);

        //这一步才是真正的输出
        processDispatchResult(resp, mv);
    }

    private HandlerMapping getHandler(HttpServletRequest req) {
        //根据用户请求的URL来获得一个HandlerMapping
        if (this.handlerMappings.isEmpty()) return null;

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");


        for (HandlerMapping handlerMapping : handlerMappings){
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if (!matcher.matches()){
                continue;
            }
            return handlerMapping;
        }
        return null;
    }

    private HandlerAdapter getHandlerAdapter(HandlerMapping handler) {
        if (this.handlerAdapters.isEmpty())
            return null;
        return this.handlerAdapters.get(handler);
    }

    private void processDispatchResult(HttpServletResponse resp, ModelAndView mv) throws Exception {
        //调用viewResolver的resove方法

        if (null == mv){
            return;
        }
        if (this.viewResolvers.isEmpty()){
            return;
        }

        for (ViewResolver viewResolver : viewResolvers){
            if (!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }

            String out = viewResolver.viewResolver(mv);
            if(out != null){
                resp.getWriter().write(out);
                break;
            }
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        //初始化IOC容器
        ApplicationContext context = new ApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);
    }

    private void initStrategies(ApplicationContext context) {



        //文件上传解析
        //initMultipartResolver(context);
        //本地化解析
        //initLocaleResolver(context);
        //主题解析
        //initThemeResolver(context);
        //通过HandlerMapping，将请求映射到处理器
        initHandlerMappings(context);
        //通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerAdapters(context);
        //如果执行过程中遇到异常，将交给HandlerExceptionResouvlers
        //initHandlerExceptionResolvers(context);
        //直接解析请求到视图名
        //initRequestToViewNameTranslator(context);
        //通过viewResolvers解析逻辑视图到具体视图实现
        initViewResolvers(context);
        // flash映射管理器
        //initFlashMapManager(context);
    }


    // HandlerMapping是用来保存Controller中配置的RequestMapping和Method的一个对应关系。
    private void initHandlerMappings(ApplicationContext context)  {
        //应该是一个 Map<String, Method>

        //首先从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames){
            Object instance = context.getBean(beanName);
            //到了MVC层，对外提供的方法只有一个getBean方法
            //返回的对象不是BeanWrapper，怎么办？
            Object proxy = context.getBean(beanName);
            Object controller = null;
            try {
                controller = AopProxyUtils.getTargetObject(proxy);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Class<?> clazz = instance.getClass();
            String baseUrl = "";
            if (!clazz.isAnnotationPresent(Controller.class)){
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            //扫描所有的public方法
            Method[] methods = clazz.getMethods();
            for (Method method : methods){
                if (!method.isAnnotationPresent(RequestMapping.class)){
                    continue;
                }
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*", ".*")).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new HandlerMapping(pattern, controller, method));
                System.out.println("Mapping: " + regex + " , " + method);
            }

        }


    }

    // 用来动态匹配
    private void initHandlerAdapters(ApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置index，挨个从数组中填值，这样的话，就和参数的顺序无关了
        for (HandlerMapping handlerMapping : this.handlerMappings){
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            Map<String, Integer> paramMapping = new HashMap<>();
            //一个参数可以有多个Annotation
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for (int i=0; i<pa.length; i++){
                for (Annotation a : pa[i]){
                    if (a instanceof RequestParam){
                        String paramName = ((RequestParam)a).value();
                        if (!"".equals(paramName.trim())){
                            paramMapping.put(paramName, i);
                        }
                    }
                }
            }

            //接下来处理非命名参数
            //只处理Request和Response
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for (int i=0; i<paramTypes.length; i++){
                Class<?> type = paramTypes[i];
                if (type == HttpServletRequest.class ||
                        type == HttpServletResponse.class){
                    paramMapping.put(type.getName(), i);
                }
            }

            this.handlerAdapters.put(handlerMapping, new HandlerAdapter(paramMapping));
        }




    }


    private void initViewResolvers(ApplicationContext context) {
        //解决页面名字和模板文件关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File templateRootDir = new File(templateRootPath);

        for (File template : templateRootDir.listFiles()) {
            this.viewResolvers.add(new ViewResolver(template.getName(),template));
        }
    }
}
