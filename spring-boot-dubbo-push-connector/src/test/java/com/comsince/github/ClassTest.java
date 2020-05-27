package com.comsince.github;

import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.utils.ClassUtil;
import com.comsince.github.websocket.im.WsImHandler;
import org.junit.Test;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午4:29
 **/
public class ClassTest {
    @Test
    public void testGetMethod(){
        try {
            for (Class cls : ClassUtil.getAllAssignedClass(WsImHandler.class)) {
                Handler annotation = (Handler)cls.getAnnotation(Handler.class);
                if(annotation != null && annotation.value().equals(IMTopic.AddFriendRequestTopic)) {
                    WsImHandler handler = (WsImHandler) cls.newInstance();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testHandler(){
        try {
            for (Class cls : ClassUtil.getAllAssignedClass(WsImHandler.class)) {
                Handler annotation = (Handler)cls.getAnnotation(Handler.class);
                if(annotation != null && annotation.value().equals(IMTopic.GetUserInfoTopic)) {
                    WsImHandler handler = (WsImHandler) cls.newInstance();
                }
            }
        } catch (Exception e) {
        }
    }


}
