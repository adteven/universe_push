package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-21 下午4:10
 **/
@Handler(IMTopic.DismissGroupTopic)
public class DismissGroupHandler extends GroupHandler<FSCMessage.DismissGroupRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.DismissGroupRequest request, ImMessageProcessor.IMCallback callback) {
        GroupInfo groupInfo = messageService.getGroupInfo(request.getGroupId());
        ErrorCode errorCode;
        if (groupInfo == null) {
            errorCode = messageService.dismissGroup(fromUser, request.getGroupId(), isAdmin);

        } else if (isAdmin || (groupInfo.getType() == ProtoConstants.GroupType.GroupType_Normal || groupInfo.getType() == ProtoConstants.GroupType.GroupType_Restricted)
                && groupInfo.getOwner() != null && groupInfo.getOwner().equals(fromUser)) {

            //send notify message first, then dismiss group
            if (request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, groupInfo.getTarget(), request.getToLineList(), request.getNotifyContent());
            } else {
                FSCMessage.MessageContent content = new GroupNotificationBinaryContent(groupInfo.getTarget(), fromUser, null, "").getDismissGroupNotifyContent();
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
            }
            errorCode = messageService.dismissGroup(fromUser, request.getGroupId(), isAdmin);
        } else {
            errorCode = ErrorCode.ERROR_CODE_NOT_RIGHT;
        }
        return errorCode;
    }
}
