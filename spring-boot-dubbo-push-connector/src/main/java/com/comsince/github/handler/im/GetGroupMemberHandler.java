package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupMember;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;
import java.util.List;

import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;

@Handler(IMTopic.GetGroupMemberTopic)
public class GetGroupMemberHandler extends IMHandler<FSCMessage.PullGroupMemberRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.PullGroupMemberRequest request, ImMessageProcessor.IMCallback callback) {
        List<GroupMember> groupMembers = messageService.getGroupMembers(request.getTarget(), request.getHead());

        if (groupMembers != null) {
            List<FSCMessage.GroupMember> wfcGroupMembers = GroupMember.convertToWfcMembers(groupMembers);
            FSCMessage.PullGroupMemberResult result = FSCMessage.PullGroupMemberResult.newBuilder().addAllMember(wfcGroupMembers).build();
            byte[] data = result.toByteArray();
            ackPayload.ensureWritable(data.length).writeBytes(data);
        }
        return ERROR_CODE_SUCCESS;
    }
}
