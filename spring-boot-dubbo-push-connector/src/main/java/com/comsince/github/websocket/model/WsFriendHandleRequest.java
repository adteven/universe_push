package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-8 上午10:53
 **/
public class WsFriendHandleRequest {
    String targetUid;
    int status;

    public String getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WsFriendHandleRequest{" +
                "targetUid='" + targetUid + '\'' +
                ", status=" + status +
                '}';
    }
}
