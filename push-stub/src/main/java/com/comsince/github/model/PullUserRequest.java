package com.comsince.github.model;

import com.comsince.github.proto.FSCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PullUserRequest implements Serializable {

    public static class UserRequest implements Serializable{
        String uid;
        long updateDate;

        public static UserRequest convertToUserRequest(FSCMessage.UserRequest wfcUserRequest){
            UserRequest userRequest = new UserRequest();
            userRequest.uid = wfcUserRequest.getUid();
            userRequest.updateDate = wfcUserRequest.getUpdateDt();
            return userRequest;
        }

        public static FSCMessage.UserRequest convertWfcUserRequest(UserRequest userRequest){
            FSCMessage.UserRequest.Builder builder = FSCMessage.UserRequest.newBuilder();
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

    public static List<UserRequest> convertToUserRequests(List<FSCMessage.UserRequest> wfcUserRequests){
        List<UserRequest> userRequests = new ArrayList<>();
        if(wfcUserRequests != null){
            for(FSCMessage.UserRequest wfcUserRequest : wfcUserRequests){
                userRequests.add(UserRequest.convertToUserRequest(wfcUserRequest));
            }
        }
        return userRequests;
    }

    public static List<FSCMessage.UserRequest> convert2WfcUserRequests(List<UserRequest> userRequests){
        List<FSCMessage.UserRequest> wfcUserRequests = new ArrayList<>();
        if(wfcUserRequests != null){
            for(UserRequest userRequest : userRequests){
                wfcUserRequests.add(UserRequest.convertWfcUserRequest(userRequest));
            }
        }
        return wfcUserRequests;
    }
}
