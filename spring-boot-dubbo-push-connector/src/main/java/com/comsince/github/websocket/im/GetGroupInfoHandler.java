package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.proto.FSCMessage;
import org.tio.utils.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:18
 **/
@Handler(IMTopic.GetGroupInfoTopic)
public class GetGroupInfoHandler extends WsImHandler<ArrayList<String>,FSCMessage.PullGroupInfoResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, ArrayList<String> groupIds) {
        log.info("get group info group ids {}",groupIds);
        FSCMessage.PullUserRequest.Builder userRequestBuilder = FSCMessage.PullUserRequest.newBuilder();
        for(String groupId : groupIds){
            FSCMessage.UserRequest userRequest = FSCMessage.UserRequest.newBuilder().setUid(groupId).build();
            userRequestBuilder.addRequest(userRequest);
        }
        return userRequestBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, FSCMessage.PullGroupInfoResult groupInfoResult) {
        List<GroupInfo> groupInfos = GroupInfo.convert2GroupInfos(groupInfoResult.getInfoList());
        return Json.toJson(groupInfos);
    }
}
