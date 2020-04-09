package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-8 上午10:20
 **/
public class WsFriendRequestPullRequest {
    String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "WsFriendRequestPullRequest{" +
                "version='" + version + '\'' +
                '}';
    }
}
