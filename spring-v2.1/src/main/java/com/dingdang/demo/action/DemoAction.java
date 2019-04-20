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

    @RequestMapping("/add")
    public ModelAndView add(@RequestParam String title, @RequestParam String content){
        try {
            String result = demoService.add(title, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(content);
        return new ModelAndView();
    }

    @RequestMapping("edit")
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse resp,
                      @RequestParam Integer id, @RequestParam String title){
        String result = demoService.edit(id, title);
        System.out.println(result);
        try {
            resp.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView();
    }

    @RequestMapping("delete")
    public ModelAndView delete(HttpServletRequest request, HttpServletResponse resp, @RequestParam Integer id){
        String result = demoService.delete(id);
        System.out.println(result);
        return new ModelAndView();
    }
}
