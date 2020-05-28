package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.ProtoConstants;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.GroupMember;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsCreateGroupRequest;
import io.netty.util.internal.StringUtil;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:21
 **/
@Handler(value = IMTopic.CreateGroupTopic)
public class CreateGroupHandler extends WsImHandler<WsCreateGroupRequest,Byte> {
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsCreateGroupRequest wsCreateGroupRequest) {
        log.info("create group {}",wsCreateGroupRequest);
        String groupId = wsCreateGroupRequest.getGroupInfo().getTarget();
        String groupPortrait = wsCreateGroupRequest.getGroupInfo().getPortrait();
        FSCMessage.GroupInfo groupInfo = FSCMessage.GroupInfo.newBuilder()
                .setName(wsCreateGroupRequest.getGroupInfo().getName())
                .setTargetId(StringUtil.isNullOrEmpty(groupId)? "":groupId)
                .setPortrait(StringUtil.isNullOrEmpty(groupPortrait)? "":groupPortrait)
                .setType(ProtoConstants.GroupType.GroupType_Normal)
                .build();

        FSCMessage.Group.Builder groupBuilder = FSCMessage.Group.newBuilder();
        groupBuilder.setGroupInfo(groupInfo);
        if(wsCreateGroupRequest.getGroupMembers() != null){
            for(GroupMember groupMember : wsCreateGroupRequest.getGroupMembers()){
                FSCMessage.GroupMember member = FSCMessage.GroupMember.newBuilder()
                        .setMemberId(groupMember.memberId)
                        .setType(groupMember.type)
                        .build();
                groupBuilder.addMembers(member);
            }
        }
        FSCMessage.CreateGroupRequest.Builder createGroupRequestBuilder = FSCMessage.CreateGroupRequest.newBuilder();
        createGroupRequestBuilder.setGroup(groupBuilder.build());
        if(wsCreateGroupRequest.getLines() != null){
            createGroupRequestBuilder.addAllToLine(wsCreateGroupRequest.getLines());
        }
        return createGroupRequestBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return byteResult(result);
    }
}
