package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

import static cn.wildfirechat.proto.ProtoConstants.ModifyGroupInfoType.Modify_Group_Name;
import static cn.wildfirechat.proto.ProtoConstants.ModifyGroupInfoType.Modify_Group_Portrait;
import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;
import static com.comsince.github.handler.im.IMTopic.ModifyGroupInfoTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 下午3:33
 **/
@Handler(value = ModifyGroupInfoTopic)
public class ModifyGroupInfoHandler extends GroupHandler<WFCMessage.ModifyGroupInfoRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.ModifyGroupInfoRequest request, ImMessageProcessor.IMCallback callback) {
        LOG.info("groupId {} type {} value ",request.getGroupId(),request.getType(),request.getValue());
        ErrorCode errorCode= messageService.modifyGroupInfo(fromUser, request.getGroupId(), request.getType(), request.getValue());
        if (errorCode == ERROR_CODE_SUCCESS) {
            if(request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), request.getNotifyContent());
            } else {
                WFCMessage.MessageContent content = null;
                if (request.getType() == Modify_Group_Name) {
                    content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, request.getValue(), "").getChangeGroupNameNotifyContent();
                } else if(request.getType() == Modify_Group_Portrait) {
                    content = new GroupNotificationBinaryContent(request.getGroupId(), fromUser, null, "").getChangeGroupPortraitNotifyContent();
                }

                if (content != null) {
                    sendGroupNotification(fromUser, request.getGroupId(), request.getToLineList(), content);
                }
            }
        }
        return errorCode;
    }
}
