package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-21 下午4:21
 **/
public class WsGroupDismissRequest {
    String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "WsGroupDismissRequest{" +
                "groupId='" + groupId + '\'' +
                '}';
    }
}
