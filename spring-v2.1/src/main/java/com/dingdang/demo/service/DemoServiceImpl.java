package com.dingdang.demo.service;

import com.dingdang.spring.framework.annotation.Service;

@Service
public class DemoServiceImpl implements DemoService {


    @Override
    public String add(String title, String content) throws Exception {
        throw new Exception("测试切面通知是否生效的异常");
    }

    @Override
    public String edit(Integer id, String title) {
        return "DemoService.edit(id=" + id + ", title=" + title + ")";
    }

    @Override
    public String delete(Integer id) {
        return "DemoService.delete(id=" + id + ")";
    }
}
