package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-3 下午5:30
 **/
public class WsFriendAddRequest {
    String reason;
    String targetUserId;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    @Override
    public String toString() {
        return "WsFriendAddRequest{" +
                "reason='" + reason + '\'' +
                ", targetUserId='" + targetUserId + '\'' +
                '}';
    }
}
