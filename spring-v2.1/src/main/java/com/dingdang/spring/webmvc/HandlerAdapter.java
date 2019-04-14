package com.dingdang.spring.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class HandlerAdapter {

    private Map<String,Integer> paramMapping;

    public HandlerAdapter(Map<String, Integer> paramMapping) {
        this.paramMapping = paramMapping;
    }

    /**
     *
     * @param req
     * @param resp 为什么要把resp传进来?
     *             只是为了将其赋值给方法的参数，因为这个是不能直接new的，传与不传是根据用户的请求决定的。
     *
     * @param handler 为什么要把handler传进来?
     *                因为handler中包含了controller, method, url信息
     *                根据用户请求的参数信息，跟method中的参数信息进行动态匹配
     * @return  只有当用户传过来的ModelAndView为空的时候，才会new一个默认的
     */
    public ModelAndView handler(HttpServletRequest req, HttpServletResponse resp, HandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        // 1.要准备好这个方法的形参列表
        Class<?>[] paramType = handler.getMethod().getParameterTypes();

        // 2.拿到自定义命名参数所在的位置
        // 用户通过URL传过来的参数列表
        Map<String, String[]>  reqParamMap = req.getParameterMap();


        // 3.构造实参列表
        Object[] paramValues = new Object[paramType.length];
        for (Map.Entry<String, String[]> param : reqParamMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s","");
            if (!this.paramMapping.containsKey(param.getKey())){
                continue;
            }

            int index = this.paramMapping.get(param.getKey());
            // 因为页面上传过来的值都是String类型的，而在方法中定义的类型是多样的，要针对我们传过来的参数进行类型转换

            paramValues[index] = this.paramMapping.get(HttpServletRequest.class.getName());
        }

        if(this.paramMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = this.paramMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if(this.paramMapping.containsKey(HttpServletResponse.class.getName())) {
            int respIndex = this.paramMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = resp;
        }


        // 4.从handler中取出controller, method, 然后利用反射机制进行调用
        Object result = handler.getMethod().invoke(handler.getController(), paramValues);

        boolean isModelAndView = handler.getMethod().getReturnType() == ModelAndView.class;
        if (isModelAndView){
            return (ModelAndView)result;
        }
        return null;
    }


    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            return  Integer.valueOf(value);
        }else if(clazz == int.class){
            return Integer.valueOf(value).intValue();
        }else {
            return null;
        }
    }
}
