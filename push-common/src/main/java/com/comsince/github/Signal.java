package com.comsince.github;

import io.netty.util.internal.StringUtil;

/***
 * 基本信令定义
 **/
public enum Signal {
    NONE,
    SUB, //订阅信令
    AUTH,//鉴权信令
    PING,//心跳指令
    PUSH,//推送指令
    CONTACT,//聊天信令
    CONNECT,//IM链接信令
    PUBLISH;

    public static Signal toEnum(int ordinal) {
        byte o = (byte) ordinal;
        if (o > NONE.ordinal() &&
                o < Signal.values().length) {
            for (Signal signal : Signal.values()) {
                if (signal.ordinal() == o) {
                    return signal;
                }
            }
        }
        return NONE;
    }

    public static Signal toEnum(String topic){
        if(!StringUtil.isNullOrEmpty(topic)){
            for(Signal signal : Signal.values()){
                if(signal.name().equals(topic)){
                    return signal;
                }
            }
        }
        return NONE;
    }
}
