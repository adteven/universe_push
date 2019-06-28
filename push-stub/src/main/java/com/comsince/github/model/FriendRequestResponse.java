package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;

public class FriendRequestResponse implements Serializable {
    private int direction;
    private String target;
    private String reason;
    private int status;
    private int readStatus;
    private long timestamp;

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(int readStatus) {
        this.readStatus = readStatus;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "FriendRequestResponse{" +
                "direction=" + direction +
                ", target='" + target + '\'' +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", readStatus=" + readStatus +
                ", timestamp=" + timestamp +
                '}';
    }

    public static FriendRequestResponse convertFriendRequest(WFCMessage.FriendRequest friendRequest){
        FriendRequestResponse friendRequestResponse = new FriendRequestResponse();
        friendRequestResponse.setReason(friendRequest.getReason());
        friendRequestResponse.setTimestamp(friendRequest.getUpdateDt());
        friendRequestResponse.setTarget(friendRequest.getFromUid());
        friendRequestResponse.setStatus(friendRequest.getStatus());
        return friendRequestResponse;
    }

    public static WFCMessage.FriendRequest convertFriendRequestResponse(FriendRequestResponse friendRequestResponse){
        WFCMessage.FriendRequest friendRequest = WFCMessage.FriendRequest.newBuilder()
                .setToUid(friendRequestResponse.getTarget())
                .setReason(friendRequestResponse.getReason())
                .setStatus(friendRequestResponse.getStatus())
                .build();
        return friendRequest;
    }


}
