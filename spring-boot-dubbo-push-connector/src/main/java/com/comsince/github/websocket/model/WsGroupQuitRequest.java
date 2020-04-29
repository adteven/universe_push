package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-29 上午10:21
 **/
public class WsGroupQuitRequest {
    String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "WsGroupQuitRequest{" +
                "groupId='" + groupId + '\'' +
                '}';
    }
}
