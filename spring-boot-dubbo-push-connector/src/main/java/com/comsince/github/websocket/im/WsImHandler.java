package com.comsince.github.websocket.im;

import com.comsince.github.PushPacket;
import com.comsince.github.utils.Utility;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.json.Json;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 上午11:40
 **/
public abstract class WsImHandler<T,V> {
    protected static final Logger log = LoggerFactory.getLogger(WsImHandler.class);
    private Class dataCls;

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ActionMethod {
    }

    protected static String actionName;

    public WsImHandler() {
        try {
            if (StringUtil.isNullOrEmpty(actionName)) {
                Class cls = getClass();
                while (cls.getSuperclass() != null) {
                    for (Method method : cls.getSuperclass().getDeclaredMethods()) {
                        if (method.getAnnotation(ActionMethod.class) != null) {
                            actionName = method.getName();
                            break;
                        }
                    }
                    if (StringUtil.isNullOrEmpty(actionName)) {
                        cls = cls.getSuperclass();
                    } else {
                        break;
                    }
                }
            }


            for (Method method : getClass().getDeclaredMethods()) {

                if (method.getName() == actionName && method.getParameterCount() == 1) {
                    dataCls = method.getParameterTypes()[0];
                    log.info("im data class init {}",dataCls);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Utility.printExecption(log, e);
        }
    }


    public byte[] processMessage(String content){
        byte[] result = null;
        try {
            T request = (T) Json.toBean(content,dataCls);
            result = convert2ProtoMessage(request);
        } catch (Exception e){
            Utility.printExecption(log, e);
        }
        return result;
    }

    /**
     * 所有的抽象函数字段必须包含泛型,这样才能解析出原始类型
     * */
    @ActionMethod
    abstract public byte[] convert2ProtoMessage(T request);

//    @ActionMethod
//    abstract public String convert2WebsocketMessage(V protoMessage);

}
