package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.GroupMember;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsGetGroupMemberRequest;
import org.tio.utils.json.Json;

import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:20
 **/
@Handler(IMTopic.GetGroupMemberTopic)
public class GetGroupMemberHandler extends WsImHandler<WsGetGroupMemberRequest,FSCMessage.PullGroupMemberResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsGetGroupMemberRequest getGroupMemberRequest) {
        log.info("get group member {}",getGroupMemberRequest);
        FSCMessage.PullGroupMemberRequest.Builder groupMemberBuilder = FSCMessage.PullGroupMemberRequest.newBuilder();
        groupMemberBuilder.setTarget(getGroupMemberRequest.getGroupId());
        groupMemberBuilder.setHead(getGroupMemberRequest.getVersion());
        return groupMemberBuilder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, FSCMessage.PullGroupMemberResult pullGroupMemberResult) {
        List<GroupMember> groupMemberList = GroupMember.convertToGroupMember(pullGroupMemberResult.getMemberList());
        return Json.toJson(groupMemberList);
    }
}
