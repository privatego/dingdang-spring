package com.dingdang.demo.action;


import com.dingdang.demo.service.DemoService;
import com.dingdang.spring.framework.annotation.Autowired;
import com.dingdang.spring.framework.annotation.Controller;
import com.dingdang.spring.framework.annotation.RequestMapping;
import com.dingdang.spring.framework.annotation.RequestParam;
import com.dingdang.spring.webmvc.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DemoAction {


    @Autowired
    private DemoService demoService;

    @RequestMapping("/get")
    public ModelAndView get(@RequestParam String name){
        String content = demoService.get(name);
        System.out.println(content);
        return new ModelAndView();
    }

    @RequestMapping("query")
    public void query(HttpServletRequest request, HttpServletResponse resp,
                      @RequestParam String name){
        String result = demoService.get(name);
        System.out.println(result);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
