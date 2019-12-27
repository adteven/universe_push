package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午4:54
 **/
public class UserResponse implements Serializable {
    private String uid;
    private String name;
    private String displayName;
    private int gender;
    private String portrait;
    private String mobile;
    private String email;
    private String address;
    private String company;
    private String social;
    private String extra;
    private String friendAlias;
    private String groupAlias;
    private long updateDt;
    private int type;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getSocial() {
        return social;
    }

    public void setSocial(String social) {
        this.social = social;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getFriendAlias() {
        return friendAlias;
    }

    public void setFriendAlias(String friendAlias) {
        this.friendAlias = friendAlias;
    }

    public String getGroupAlias() {
        return groupAlias;
    }

    public void setGroupAlias(String groupAlias) {
        this.groupAlias = groupAlias;
    }

    public long getUpdateDt() {
        return updateDt;
    }

    public void setUpdateDt(long updateDt) {
        this.updateDt = updateDt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", gender=" + gender +
                ", portrait='" + portrait + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", company='" + company + '\'' +
                ", social='" + social + '\'' +
                ", extra='" + extra + '\'' +
                ", friendAlias='" + friendAlias + '\'' +
                ", groupAlias='" + groupAlias + '\'' +
                ", updateDt=" + updateDt +
                ", type=" + type +
                '}';
    }

    public static UserResponse convertWFCUser(WFCMessage.User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setUid(user.getUid());
        userResponse.setAddress(user.getAddress());
        userResponse.setCompany(user.getCompany());
        userResponse.setEmail(user.getEmail());
        userResponse.setName(user.getName());
        userResponse.setMobile(user.getMobile());
        userResponse.setDisplayName(user.getDisplayName());
        userResponse.setGender(user.getGender());
        userResponse.setExtra(user.getExtra());
        userResponse.setPortrait(user.getPortrait());
        userResponse.setUpdateDt(user.getUpdateDt());
        userResponse.setType(user.getType());
        return userResponse;
    }
}
