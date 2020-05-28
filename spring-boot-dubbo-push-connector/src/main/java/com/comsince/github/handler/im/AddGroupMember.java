package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupMember;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 上午9:53
 **/
@Handler(value = IMTopic.AddGroupMemberTopic)
public class AddGroupMember extends GroupHandler<FSCMessage.AddGroupMemberRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.AddGroupMemberRequest request, ImMessageProcessor.IMCallback callback) {
        List<GroupMember> groupMembers = GroupMember.convertToGroupMember(request.getAddedMemberList());
        LOG.info("add group members "+groupMembers);
        ErrorCode errorCode = messageService.addGroupMembers(fromUser, request.getGroupId(), groupMembers);
        if (errorCode == ERROR_CODE_SUCCESS) {
            if (request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), request.getNotifyContent());
            } else {
                FSCMessage.MessageContent content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, null, getMemberIdList(request.getAddedMemberList())).getAddGroupNotifyContent();
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
            }
        }

        return errorCode;
    }
}
