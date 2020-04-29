package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-29 上午9:42
 **/
public class WsModifyGroupInfoRequest {
    private String groupId;
    private int type;
    private String value;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WsModifyGroupInfoRequest{" +
                "groupId='" + groupId + '\'' +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
