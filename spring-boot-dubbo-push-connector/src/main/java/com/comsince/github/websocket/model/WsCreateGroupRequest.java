package com.comsince.github.websocket.model;

import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.GroupMember;

import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-27 上午10:16
 **/
public class WsCreateGroupRequest {
    List<Integer> lines;
    GroupInfo groupInfo;
    List<GroupMember> groupMembers;

    public List<Integer> getLines() {
        return lines;
    }

    public void setLines(List<Integer> lines) {
        this.lines = lines;
    }

    public GroupInfo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    @Override
    public String toString() {
        return "WsCreateGroupRequest{" +
                "lines=" + lines +
                ", groupInfo=" + groupInfo +
                ", groupMembers=" + groupMembers +
                '}';
    }
}
