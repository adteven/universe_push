package com.comsince.github.persistence.session;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttQoS;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:19
 **/
public class StoredMessage {
    final byte[] m_payload;
    final String m_topic;
    private boolean m_retained;
    private String m_clientID;
    private MessageGUID m_guid;

    public StoredMessage(byte[] message, MqttQoS qos, String topic) {
        m_payload = message;
        m_topic = topic;
    }


    public String getTopic() {
        return m_topic;
    }

    public void setGuid(MessageGUID guid) {
        this.m_guid = guid;
    }

    public MessageGUID getGuid() {
        return m_guid;
    }

    public String getClientID() {
        return m_clientID;
    }

    public void setClientID(String m_clientID) {
        this.m_clientID = m_clientID;
    }

    public ByteBuf getPayload() {
        return Unpooled.copiedBuffer(m_payload);
    }

    public void setRetained(boolean retained) {
        this.m_retained = retained;
    }

    public boolean isRetained() {
        return m_retained;
    }

    @Override
    public String toString() {
        return "PublishEvent{clientID='" + m_clientID + '\'' + ", m_retain="
                + m_retained + ", m_topic='" + m_topic + '\'' + '}';
    }
}
