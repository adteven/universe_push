package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-31 下午6:00
 **/
public class WsSendMessageResponse {
    //js 大数丢失，全部换成String 类型
    private String messageId;
    private long timestamp;

    public WsSendMessageResponse(String messageId, long timestamp) {
        this.messageId = messageId;
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
