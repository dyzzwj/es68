package com.dyzwj.es68.service;

import com.dyzwj.es68.event.UserRegisterEvent;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/8 11:39
 * 类说明
 */
@Service
public class UserRegisterService {

    @Autowired
    ApplicationContext applicationContext;


    public void register(){
        System.out.println("用户注册");
        System.out.println("发布用户注册事件");
        applicationContext.publishEvent(new UserRegisterEvent("用户注册"));
    }



}
