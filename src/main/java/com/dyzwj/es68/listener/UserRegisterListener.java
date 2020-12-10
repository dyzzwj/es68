package com.dyzwj.es68.listener;

import com.dyzwj.es68.event.UserRegisterEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import sun.applet.AppletEvent;
import sun.applet.AppletListener;

/**
 * @author 作者 : ZhengWenjie
 * @version 创建时间：2020/12/8 11:36
 * 类说明
 */
@Component
public class UserRegisterListener implements ApplicationListener<UserRegisterEvent> {
    @Override
    public void onApplicationEvent(UserRegisterEvent userRegisterEvent) {
        System.out.println("接受到用户注册信息："+userRegisterEvent.getSource());;
    }
}
