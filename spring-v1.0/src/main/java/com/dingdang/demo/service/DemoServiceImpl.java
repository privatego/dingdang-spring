package com.dingdang.demo.service;

import com.dingdang.spring.annotation.Service;

@Service
public class DemoServiceImpl implements DemoService{

    @Override
    public String get(String name) {
        return "my name is " + name;
    }
}
