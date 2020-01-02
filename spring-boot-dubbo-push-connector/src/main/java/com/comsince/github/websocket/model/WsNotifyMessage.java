package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-1-2 上午10:57
 **/
public class WsNotifyMessage {
    //long全部转为string，防止前端大数精度丢失
    private String messageHead;
    private int type;

    public WsNotifyMessage(String messageHead, int type) {
        this.messageHead = messageHead;
        this.type = type;
    }

    public String getMessageHead() {
        return messageHead;
    }

    public void setMessageHead(String messageHead) {
        this.messageHead = messageHead;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
