package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.UserResponse;
import com.comsince.github.proto.FSCMessage;
import org.tio.utils.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:15
 **/
@Handler(IMTopic.GetUserInfoTopic)
public class GetUserInfoHandler extends WsImHandler<ArrayList<String>,FSCMessage.PullUserResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, ArrayList<String> userIds) {
        log.info("get user info userIds {}",userIds);
        FSCMessage.PullUserRequest.Builder userRequestBuilder = FSCMessage.PullUserRequest.newBuilder();
        for(String user : userIds){
            FSCMessage.UserRequest userRequest = FSCMessage.UserRequest.newBuilder().setUid(user).build();
            userRequestBuilder.addRequest(userRequest);
        }
        return userRequestBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, FSCMessage.PullUserResult pullUserResult) {
        List<UserResponse> userResponseList = new ArrayList<>();
        for(FSCMessage.UserResult userResult : pullUserResult.getResultList()){
            FSCMessage.User user = userResult.getUser();
            userResponseList.add(UserResponse.convertWFCUser(user));
        }
        return Json.toJson(userResponseList);
    }
}
