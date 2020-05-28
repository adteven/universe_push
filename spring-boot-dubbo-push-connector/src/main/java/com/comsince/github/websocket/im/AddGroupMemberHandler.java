package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.GroupMember;
import com.comsince.github.websocket.model.WsAddGroupMemberRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:24
 **/
@Handler(value = IMTopic.AddGroupMemberTopic)
public class AddGroupMemberHandler extends WsImHandler<WsAddGroupMemberRequest,Byte>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsAddGroupMemberRequest wsAddGroupMemberRequest) {
        log.info("add group member {}",wsAddGroupMemberRequest);
        WFCMessage.AddGroupMemberRequest.Builder memberRequestBuilder = WFCMessage.AddGroupMemberRequest.newBuilder();
        memberRequestBuilder.setGroupId(wsAddGroupMemberRequest.getGroupId());
        if(wsAddGroupMemberRequest.getGroupMembers() != null){
            for(GroupMember gm : wsAddGroupMemberRequest.getGroupMembers()){
                WFCMessage.GroupMember groupMember = WFCMessage.GroupMember.newBuilder()
                        .setMemberId(gm.getMemberId())
                        .setType(gm.type)
                        .build();
                memberRequestBuilder.addAddedMember(groupMember);
            }
        }
        return memberRequestBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return byteResult(result);
    }
}
