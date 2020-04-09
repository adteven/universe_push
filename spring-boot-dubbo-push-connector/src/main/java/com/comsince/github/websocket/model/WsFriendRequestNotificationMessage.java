package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-8 上午10:44
 **/
public class WsFriendRequestNotificationMessage {
    String version;

    public WsFriendRequestNotificationMessage(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "WsFriendRequestNotificationMessage{" +
                "version='" + version + '\'' +
                '}';
    }
}
