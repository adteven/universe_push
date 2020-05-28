package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import static com.comsince.github.handler.im.IMTopic.KickoffGroupMemberTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 上午10:32
 **/
@Handler(value = KickoffGroupMemberTopic)
public class KickoffGroupMember extends GroupHandler<FSCMessage.RemoveGroupMemberRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.RemoveGroupMemberRequest request, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode;
        GroupInfo groupInfo = messageService.getGroupInfo(request.getGroupId());
        LOG.info("groupInfo {}",groupInfo);
        if (groupInfo == null) {
            errorCode = ErrorCode.ERROR_CODE_NOT_EXIST;

        } else if ((groupInfo.getType() == ProtoConstants.GroupType.GroupType_Normal || groupInfo.getType() == ProtoConstants.GroupType.GroupType_Restricted)
                && groupInfo.getOwner() != null && groupInfo.getOwner().equals(fromUser)) {

            //send notify message first, then kickoff the member
            if (request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, groupInfo.getTarget(), request.getToLineList(), request.getNotifyContent());
            } else {
                FSCMessage.MessageContent content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, null, request.getRemovedMemberList()).getKickokfMemberGroupNotifyContent();
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
            }
            errorCode = messageService.kickoffGroupMembers(fromUser, request.getGroupId(), request.getRemovedMemberList());
        } else {
            errorCode = ErrorCode.ERROR_CODE_NOT_RIGHT;
        }
        return errorCode;
    }
}
