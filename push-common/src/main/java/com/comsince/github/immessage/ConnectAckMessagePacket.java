package com.comsince.github.immessage;

import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-12 上午11:28
 **/
public class ConnectAckMessagePacket extends PushPacket {
    SubSignal subSignal = SubSignal.NONE;
    @Override
    public Signal signal() {
        return Signal.CONNECT_ACK;
    }

    @Override
    public SubSignal subSignal() {
        return subSignal;
    }

    public void setSubSignal(SubSignal subSignal){
        this.subSignal = subSignal;
    }
}
