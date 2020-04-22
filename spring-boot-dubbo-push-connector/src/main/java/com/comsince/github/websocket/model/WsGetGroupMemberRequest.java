package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-22 上午10:21
 **/
public class WsGetGroupMemberRequest {
    String groupId;
    long version;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "WsGetGroupMemberRequest{" +
                "groupId='" + groupId + '\'' +
                ", version=" + version +
                '}';
    }
}
