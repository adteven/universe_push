package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.GroupMember;
import com.comsince.github.model.GroupNotificationBinaryContent;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.StringUtil;

import java.util.List;


@Handler(value = IMTopic.CreateGroupTopic)
public class CreateGroupHandler extends GroupHandler<WFCMessage.CreateGroupRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.CreateGroupRequest request, ImMessageProcessor.IMCallback callback) {
        if (!StringUtil.isNullOrEmpty(request.getGroup().getGroupInfo().getTargetId())) {
            GroupInfo existGroupInfo = messageService.getGroupInfo(request.getGroup().getGroupInfo().getTargetId());
            if (existGroupInfo != null) {
                return ErrorCode.ERROR_CODE_GROUP_ALREADY_EXIST;
            }
        }
        GroupInfo groupInfoRequest = GroupInfo.convert2GroupInfo(request.getGroup().getGroupInfo());
        List<GroupMember> groupMemberListRequest = GroupMember.convertToGroupMember(request.getGroup().getMembersList());

        if(StringUtil.isNullOrEmpty(groupInfoRequest.getPortrait())){
            String groupPortrait = createGroupPortrait(groupMemberListRequest);
            if(!StringUtil.isNullOrEmpty(groupPortrait)){
                groupInfoRequest.setPortrait(groupPortrait);
            }
        }
        GroupInfo groupInfo = messageService.createGroup(fromUser, groupInfoRequest ,groupMemberListRequest);
        LOG.info("create groupInfo {}",groupInfo);
        if (groupInfo != null) {
            LOG.info("hasNotifyContent {}",request.hasNotifyContent());
            if(request.hasNotifyContent() && request.getNotifyContent().getType() > 0) {
                sendGroupNotification(fromUser, groupInfo.getTarget(), request.getToLineList(), request.getNotifyContent());
            } else {
                WFCMessage.MessageContent content = new GroupNotificationBinaryContent(groupInfo.getTarget(), fromUser, groupInfo.getName(), "").getCreateGroupNotifyContent();
                sendGroupNotification(fromUser, groupInfo.getTarget(), request.getToLineList(), content);
            }
        }
        byte[] data = groupInfo.getTarget().getBytes();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }



}
