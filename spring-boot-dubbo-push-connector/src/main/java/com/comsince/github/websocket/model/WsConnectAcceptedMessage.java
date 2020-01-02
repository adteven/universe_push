package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-25 下午5:51
 **/
public class WsConnectAcceptedMessage {
    private long messageHead;
    private long friendHead;

    public long getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(long messageHead) {
        this.messageHead = messageHead;
    }

    public long getFriendHead() {
        return friendHead;
    }

    public void setFriendHead(long friendHead) {
        this.friendHead = friendHead;
    }
}
