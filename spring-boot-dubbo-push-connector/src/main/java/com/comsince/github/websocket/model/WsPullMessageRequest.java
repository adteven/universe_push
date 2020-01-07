package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-30 下午3:22
 **/
public class WsPullMessageRequest {
    private String messageId;
    private int type;
    //拉取消息分为用户初始下拉，以及通知下拉
    private int pullType;
    private int sendMessageCount;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPullType() {
        return pullType;
    }

    public void setPullType(int pullType) {
        this.pullType = pullType;
    }

    public int getSendMessageCount() {
        return sendMessageCount;
    }

    public void setSendMessageCount(int sendMessageCount) {
        this.sendMessageCount = sendMessageCount;
    }
}
