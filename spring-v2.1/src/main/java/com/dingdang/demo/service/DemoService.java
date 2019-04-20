package com.dingdang.demo.service;

public interface DemoService {
    String add(String title, String content) throws Exception;

    String edit(Integer id, String title);

    String delete(Integer id);
}
