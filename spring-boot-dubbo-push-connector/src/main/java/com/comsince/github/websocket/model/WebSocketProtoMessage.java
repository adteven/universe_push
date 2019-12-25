package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-24 下午3:08
 **/
public class WebSocketProtoMessage {
    private String signal;
    private String subSignal;
    private int messageId;
    private String content;

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public String getSubSignal() {
        return subSignal;
    }

    public void setSubSignal(String subSignal) {
        this.subSignal = subSignal;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public String toString() {
        return "WebSocketProtoMessage{" +
                "signal='" + signal + '\'' +
                ", subSignal='" + subSignal + '\'' +
                ", messageId=" + messageId +
                ", content='" + content + '\'' +
                '}';
    }
}
