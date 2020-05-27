package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.websocket.model.WsKickGroupMembersRequest;

import static com.comsince.github.handler.im.IMTopic.KickoffGroupMemberTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:25
 **/
@Handler(value = KickoffGroupMemberTopic)
public class KickOffGroupMemberHandler extends WsImHandler<WsKickGroupMembersRequest,Byte>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsKickGroupMembersRequest kickGroupMembersRequest) {
        log.info("kick group members {}",kickGroupMembersRequest);
        WFCMessage.RemoveGroupMemberRequest.Builder removeGroupMemberRequest = WFCMessage.RemoveGroupMemberRequest.newBuilder();
        removeGroupMemberRequest.setGroupId(kickGroupMembersRequest.getGroupId());
        for(String memberId : kickGroupMembersRequest.getMemberIds()){
            removeGroupMemberRequest.addRemovedMember(memberId);
        }
        return removeGroupMemberRequest.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return null;
    }
}
