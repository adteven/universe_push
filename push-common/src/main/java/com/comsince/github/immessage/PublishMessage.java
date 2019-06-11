package com.comsince.github.immessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午2:42
 **/
public class PublishMessage extends PushPacket {

    public Signal signal = Signal.PUBLISH;

    private String topic;

    public Signal getSignal() {
        return Signal.toEnum(topic);
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
