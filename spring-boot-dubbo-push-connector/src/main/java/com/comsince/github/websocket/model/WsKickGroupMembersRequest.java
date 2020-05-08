package com.comsince.github.websocket.model;

import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-8 上午9:56
 **/
public class WsKickGroupMembersRequest {
    String groupId;
    List<String> memberIds;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(List<String> memberIds) {
        this.memberIds = memberIds;
    }

    @Override
    public String toString() {
        return "WsKickGroupMembersRequest{" +
                "groupId='" + groupId + '\'' +
                ", memberIds=" + memberIds +
                '}';
    }
}
