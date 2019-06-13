package com.comsince.github.immessage;


import com.comsince.github.Signal;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午5:24
 **/
public class PublishAckMessagePacket extends ImPacket {
    @Override
    public Signal signal() {
        return  Signal.PUB_ACK;
    }
}
