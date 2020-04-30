package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-30 下午3:51
 **/
public class WsRecallNotifyMessage {
    String fromUser;
    String messageUid;

    public WsRecallNotifyMessage(String fromUser, String messageUid) {
        this.fromUser = fromUser;
        this.messageUid = messageUid;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(String messageUid) {
        this.messageUid = messageUid;
    }

    @Override
    public String toString() {
        return "WsRecallNotifyMessage{" +
                "fromUser='" + fromUser + '\'' +
                ", messageUid=" + messageUid +
                '}';
    }
}
