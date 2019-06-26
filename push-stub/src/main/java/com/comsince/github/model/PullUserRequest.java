package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PullUserRequest implements Serializable {

    public static class UserRequest implements Serializable{
        String uid;
        long updateDate;

        public static UserRequest convertToUserRequest(WFCMessage.UserRequest wfcUserRequest){
            UserRequest userRequest = new UserRequest();
            userRequest.uid = wfcUserRequest.getUid();
            userRequest.updateDate = wfcUserRequest.getUpdateDt();
            return userRequest;
        }

        public static WFCMessage.UserRequest convertWfcUserRequest(UserRequest userRequest){
            WFCMessage.UserRequest.Builder builder = WFCMessage.UserRequest.newBuilder();
            builder.setUid(userRequest.uid);
            builder.setUpdateDt(userRequest.updateDate);
            return builder.build();
        }
    }

    private List<UserRequest> userRequests;

    public List<UserRequest> getUserRequests() {
        return userRequests;
    }

    public void setUserRequests(List<UserRequest> userRequests) {
        this.userRequests = userRequests;
    }

    public static List<UserRequest> convertToUserRequests(List<WFCMessage.UserRequest> wfcUserRequests){
        List<UserRequest> userRequests = new ArrayList<>();
        if(wfcUserRequests != null){
            for(WFCMessage.UserRequest wfcUserRequest : wfcUserRequests){
                userRequests.add(UserRequest.convertToUserRequest(wfcUserRequest));
            }
        }
        return userRequests;
    }

    public static List<WFCMessage.UserRequest> convert2WfcUserRequests(List<UserRequest> userRequests){
        List<WFCMessage.UserRequest> wfcUserRequests = new ArrayList<>();
        if(wfcUserRequests != null){
            for(UserRequest userRequest : userRequests){
                wfcUserRequests.add(UserRequest.convertWfcUserRequest(userRequest));
            }
        }
        return wfcUserRequests;
    }
}
