package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

import static com.comsince.github.handler.im.IMTopic.QuitGroupTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 下午2:04
 **/
@Handler(value = QuitGroupTopic)
public class QuitGroupHandler extends GroupHandler<WFCMessage.QuitGroupRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.QuitGroupRequest request, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode = ErrorCode.ERROR_CODE_SUCCESS;
        GroupInfo groupInfo = messageService.getGroupInfo(request.getGroupId());
        if (groupInfo == null) {
            errorCode = messageService.quitGroup(fromUser, request.getGroupId());
        } else {
            //send notify message first, then quit group
            if (request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, groupInfo.getTarget(), request.getToLineList(), request.getNotifyContent());
            } else {
                WFCMessage.MessageContent content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, null, "").getQuitGroupNotifyContent();
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
            }
            errorCode = messageService.quitGroup(fromUser, request.getGroupId());
        }
        return errorCode;
    }
}