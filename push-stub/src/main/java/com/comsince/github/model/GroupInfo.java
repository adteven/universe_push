package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupInfo implements Serializable {
    public String target;
    public String name;
    public String portrait;
    public String owner;
    public int type;
    public int memberCount;
    public String extra;
    public long updateDt;

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    public static GroupInfo convert2GroupInfo(WFCMessage.GroupInfo groupInfo){
        GroupInfo groupInfoResponse = new GroupInfo();
        groupInfoResponse.setTarget(groupInfo.getTargetId());
        groupInfoResponse.setName(groupInfo.getName());
        groupInfoResponse.setExtra(groupInfo.getExtra());
        groupInfoResponse.setMemberCount(groupInfo.getMemberCount());
        groupInfoResponse.setPortrait(groupInfo.getPortrait());
        groupInfoResponse.setType(groupInfo.getType());
        groupInfoResponse.setUpdateDt(groupInfo.getUpdateDt());
        groupInfoResponse.setOwner(groupInfo.getOwner());
        return groupInfoResponse;
    }


    public static WFCMessage.GroupInfo convertToWfcGroupInfo(GroupInfo groupInfoResponse){
        WFCMessage.GroupInfo.Builder builder = WFCMessage.GroupInfo.newBuilder();
        builder.setTargetId(groupInfoResponse.getTarget());
        builder.setName(groupInfoResponse.getName());
        builder.setExtra(groupInfoResponse.getExtra());
        builder.setMemberCount(groupInfoResponse.getMemberCount());
        builder.setPortrait(groupInfoResponse.getPortrait());
        builder.setType(groupInfoResponse.getType());
        builder.setUpdateDt(groupInfoResponse.getUpdateDt());
        builder.setOwner(groupInfoResponse.getOwner());
        return builder.build();
    }


    public static List<WFCMessage.GroupInfo> convert2WfcGroupInfos(List<GroupInfo> groupInfos){
        List<WFCMessage.GroupInfo> wfcGroupInfos = new ArrayList<>();
        if(groupInfos != null){
            for(GroupInfo groupInfo : groupInfos){
                wfcGroupInfos.add(convertToWfcGroupInfo(groupInfo));
            }
        }
        return wfcGroupInfos;
    }

    public static List<GroupInfo> convert2GroupInfos(List<WFCMessage.GroupInfo> wfcGroupInfos){
        List<GroupInfo> groupInfos = new ArrayList<>();
        if(wfcGroupInfos != null){
            for (WFCMessage.GroupInfo wfcGroupInfo : wfcGroupInfos){
                groupInfos.add(convert2GroupInfo(wfcGroupInfo));
            }
        }
        return groupInfos;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "target='" + target + '\'' +
                ", name='" + name + '\'' +
                ", portrait='" + portrait + '\'' +
                ", owner='" + owner + '\'' +
                ", type=" + type +
                ", memberCount=" + memberCount +
                ", extra='" + extra + '\'' +
                ", updateDt=" + updateDt +
                '}';
    }
}
