package com.comsince.github.push;

import io.netty.util.internal.StringUtil;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午4:07
 **/
public enum SubSignal {
    NONE,
    FAR;

    public static SubSignal toEnum(int ordinal) {
        byte o = (byte) ordinal;
        if (o > NONE.ordinal() &&
                o < SubSignal.values().length) {
            for (SubSignal signal : SubSignal.values()) {
                if (signal.ordinal() == o) {
                    return signal;
                }
            }
        }
        return NONE;

    }

    public static SubSignal toEnum(String topic){
        if(!StringUtil.isNullOrEmpty(topic)){
            for(SubSignal signal : SubSignal.values()){
                if(signal.name().equals(topic)){
                    return signal;
                }
            }
        }
        return NONE;
    }
}
