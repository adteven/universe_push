package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-8 下午6:16
 **/
public class WsFriendNotificationMessage {
    String head;

    public WsFriendNotificationMessage(String head) {
        this.head = head;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "WsFriendNotificationMessage{" +
                "head='" + head + '\'' +
                '}';
    }
}
