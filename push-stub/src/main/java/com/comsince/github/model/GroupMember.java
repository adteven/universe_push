package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupMember implements Serializable {
    public String groupId;
    public String memberId;
    public String alias;
    public int type;
    public long updateDt;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }


    public static WFCMessage.GroupMember convertToWFCGroupMember(GroupMember groupMember){
        WFCMessage.GroupMember.Builder wfcGroupMemberBuilder = WFCMessage.GroupMember.newBuilder();
        wfcGroupMemberBuilder.setAlias(groupMember.getAlias());
        wfcGroupMemberBuilder.setMemberId(groupMember.getMemberId());
        wfcGroupMemberBuilder.setType(groupMember.getType());
        wfcGroupMemberBuilder.setUpdateDt(groupMember.getUpdateDt());
        return wfcGroupMemberBuilder.build();
    }


    public static GroupMember convertToGroupMember(WFCMessage.GroupMember wfcGroupMember){
        GroupMember groupMember = new GroupMember();
        groupMember.setAlias(wfcGroupMember.getAlias());
        groupMember.setMemberId(wfcGroupMember.getMemberId());
        groupMember.setType(wfcGroupMember.getType());
        groupMember.setUpdateDt(wfcGroupMember.getUpdateDt());
        return groupMember;
    }


    public static List<WFCMessage.GroupMember> convertToWfcMembers(List<GroupMember> memberList){
        List<WFCMessage.GroupMember> wfcGroupMembers = new ArrayList<>();
        if(memberList != null){
            for (GroupMember groupMember : memberList){
                WFCMessage.GroupMember wfcGroupMember = GroupMember.convertToWFCGroupMember(groupMember);
                wfcGroupMembers.add(wfcGroupMember);
            }
        }
        return wfcGroupMembers;
    }


    public static List<GroupMember> convertToGroupMember(List<WFCMessage.GroupMember> wfcGroupMembers){
        List<GroupMember> groupMembers = new ArrayList<>();
        if(wfcGroupMembers != null){
            for (WFCMessage.GroupMember wfcGroupMember : wfcGroupMembers){
                GroupMember groupMember = convertToGroupMember(wfcGroupMember);
                groupMembers.add(groupMember);
            }
        }
        return groupMembers;
    }

    @Override
    public String toString() {
        return "GroupMember{" +
                "groupId='" + groupId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", alias='" + alias + '\'' +
                ", type=" + type +
                ", updateDt=" + updateDt +
                '}';
    }
}
