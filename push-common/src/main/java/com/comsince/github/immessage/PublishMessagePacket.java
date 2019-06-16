package com.comsince.github.immessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午2:42
 **/
public class PublishMessagePacket extends ImPacket {

    @Override
    public Signal signal() {
        return Signal.PUBLISH;
    }
}
