package com.comsince.github.message;

import java.io.Serializable;

public class AddFriendMessage implements Serializable {
    String reason;
    String targetUid;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getTargetUid() {
        return targetUid;
    }

    public void setTargetUid(String targetUid) {
        this.targetUid = targetUid;
    }


}
