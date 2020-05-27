package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import java.util.ArrayList;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:18
 **/
@Handler(IMTopic.GetGroupInfoTopic)
public class GetGroupInfoHandler extends WsImHandler<ArrayList<String>,WFCMessage.PullGroupInfoResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, ArrayList<String> groupIds) {
        log.info("get group info group ids {}",groupIds);
        WFCMessage.PullUserRequest.Builder userRequestBuilder = WFCMessage.PullUserRequest.newBuilder();
        for(String groupId : groupIds){
            WFCMessage.UserRequest userRequest = WFCMessage.UserRequest.newBuilder().setUid(groupId).build();
            userRequestBuilder.addRequest(userRequest);
        }
        return userRequestBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.PullGroupInfoResult result) {
        return null;
    }
}
