package com.dingdang.demo.action;


import com.dingdang.demo.service.DemoService;
import com.dingdang.spring.annotation.Autowired;
import com.dingdang.spring.annotation.Controller;
import com.dingdang.spring.annotation.RequestMapping;
import com.dingdang.spring.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class DemoAction {


    @Autowired private DemoService demoService;

    @RequestMapping("/get")
    public String get(@RequestParam String name){
        String content = demoService.get(name);
        System.out.println(content);
        return "get";
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
