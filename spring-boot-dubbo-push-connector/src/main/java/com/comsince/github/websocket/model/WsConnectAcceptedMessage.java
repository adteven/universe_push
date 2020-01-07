package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-25 下午5:51
 **/
public class WsConnectAcceptedMessage {
    private String messageHead;
    private long friendHead;

    public String getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(String messageHead) {
        this.messageHead = messageHead;
    }

    public long getFriendHead() {
        return friendHead;
    }

    public void setFriendHead(long friendHead) {
        this.friendHead = friendHead;
    }
}
