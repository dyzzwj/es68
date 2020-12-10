package com.dyzwj.es68.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/8 11:22
 * 类说明
 */
public class UserRegisterEvent extends ApplicationEvent {

    public UserRegisterEvent(Object source){
        super(source);
    }


}
