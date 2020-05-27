package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsGetGroupMemberRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:20
 **/
@Handler(IMTopic.GetGroupMemberTopic)
public class GetGroupMemberHandler extends WsImHandler<WsGetGroupMemberRequest,WFCMessage.PullGroupMemberResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsGetGroupMemberRequest getGroupMemberRequest) {
        log.info("get group member {}",getGroupMemberRequest);
        WFCMessage.PullGroupMemberRequest.Builder groupMemberBuilder = WFCMessage.PullGroupMemberRequest.newBuilder();
        groupMemberBuilder.setTarget(getGroupMemberRequest.getGroupId());
        groupMemberBuilder.setHead(getGroupMemberRequest.getVersion());
        return groupMemberBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.PullGroupMemberResult result) {
        return null;
    }
}
