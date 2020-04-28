package com.comsince.github.websocket.model;

import com.comsince.github.model.GroupMember;

import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-27 上午11:06
 **/
public class WsAddGroupMemberRequest {
    String groupId;
    List<GroupMember> groupMembers;

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "WsAddGroupMemberRequest{" +
                "groupId='" + groupId + '\'' +
                ", groupMembers=" + groupMembers +
                '}';
    }
}
