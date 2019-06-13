package com.comsince.github.immessage;

import com.comsince.github.PushPacket;
import com.comsince.github.SubSignal;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午5:33
 **/
public class ImPacket extends PushPacket {
    SubSignal subSignal = SubSignal.NONE;
    int messageId;

    @Override
    final public SubSignal subSignal() {
        return subSignal;
    }

    @Override
    final public int messageId() {
        return messageId;
    }

    public void setSubSignal(SubSignal subSignal){
        this.subSignal = subSignal;
    }

    public void setMessageId(int messageId){
        this.messageId = messageId;
    }
}
