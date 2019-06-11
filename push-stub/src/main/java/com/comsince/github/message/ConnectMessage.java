package com.comsince.github.message;

import io.netty.util.internal.StringUtil;

import java.io.Serializable;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 下午4:36
 **/
public class ConnectMessage implements Serializable{
    private String clientIdentifier;
    private String willTopic;
    private String willMessage;
    private String userName;
    private byte[] password;


    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    public String getWillTopic() {
        return willTopic;
    }

    public void setWillTopic(String willTopic) {
        this.willTopic = willTopic;
    }

    public String getWillMessage() {
        return willMessage;
    }

    public void setWillMessage(String willMessage) {
        this.willMessage = willMessage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return new StringBuilder(StringUtil.simpleClassName(this))
                .append('[')
                .append("clientIdentifier=").append(clientIdentifier)
                .append(", willTopic=").append(willTopic)
                .append(", willMessage=").append(willMessage)
                .append(", userName=").append(userName)
                .append(", password=").append(password)
                .append(']')
                .toString();
    }
}
