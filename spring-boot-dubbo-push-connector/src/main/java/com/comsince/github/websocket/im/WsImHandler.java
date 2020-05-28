package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.utils.Utility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.json.Json;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 上午11:40
 **/
public abstract class WsImHandler<T,V> {
    protected static final Logger log = LoggerFactory.getLogger(WsImHandler.class);
    private Class requestCls;
    private Class resultCls;


    public WsImHandler() {
        try {

            Type superclass = this.getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            log.info("request type {}",parameterizedType.getActualTypeArguments()[0]);
            Type requestType = parameterizedType.getActualTypeArguments()[0];
            if(requestType.getTypeName().equals("java.util.ArrayList<java.lang.String>")){
                requestCls = Class.forName("java.util.ArrayList");
            } else {
                requestCls = (Class) requestType;
            }
            resultCls = (Class) parameterizedType.getActualTypeArguments()[1];
        } catch (Exception e) {
            e.printStackTrace();
            Utility.printExecption(log, e);
        }
    }

    public byte[] handleRequest(Signal signal, SubSignal subSignal, String content){
        byte[] result = null;
        try {
            T request = null;
            if (requestCls != String.class) {
                request = (T) Json.toBean(content,requestCls);
            } else {
                request = (T) content;
            }
            result = request(signal,subSignal,request);
        } catch (Exception e){
            Utility.printExecption(log, e);
        }
        return result;
    }

    /**
     * 所有的抽象函数字段必须包含泛型,这样才能解析出原始类型
     * */
    abstract public byte[] request(Signal signal, SubSignal subSignal, T request);



    abstract public String result(Signal signal, SubSignal subSignal, V result);




}
