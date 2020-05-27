package com.comsince.github.websocket.im;

import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.utils.ClassUtil;
import com.comsince.github.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-26 下午5:57
 **/
public class WsMessageHandler {
    Logger logger = LoggerFactory.getLogger(WsMessageHandler.class);

    private Map<String,WsImHandler> wsHandlers = new HashMap();

    private static WsMessageHandler wsMessageHandler;

    public static WsMessageHandler getInstance(){
        if(wsMessageHandler == null){
            synchronized (WsMessageHandler.class){
                if(wsMessageHandler == null){
                    wsMessageHandler = new WsMessageHandler();
                }
            }
        }
        return wsMessageHandler;
    }

    public WsMessageHandler(){
        registerAllAction();
    }

    public byte[] handlePublishMessage(SubSignal subSignal,String content){
        WsImHandler wsImHandler = wsHandlers.get(subSignal.name());
        if(wsImHandler != null){
            return wsImHandler.processMessage(content);
//            return null;
        } else {
            return null;
        }
    }

    private void registerAllAction(){
        try {
            for (Class cls : ClassUtil.getAllAssignedClass(WsImHandler.class)) {
                Handler annotation = (Handler)cls.getAnnotation(Handler.class);
                if(annotation != null) {
                    WsImHandler handler = (WsImHandler) cls.newInstance();
                    wsHandlers.put(annotation.value(), handler);
                }
            }
        } catch (Exception e) {
            Utility.printExecption(logger, e);
        }
    }
}
