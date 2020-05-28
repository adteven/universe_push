package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.PullUserRequest;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

@Handler(IMTopic.GetGroupInfoTopic)
public class GetGroupInfoHandler extends IMHandler<FSCMessage.PullUserRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.PullUserRequest request, ImMessageProcessor.IMCallback callback) {
        List<GroupInfo> infos = messageService.getGroupInfos(PullUserRequest.convertToUserRequests(request.getRequestList()));

        FSCMessage.PullGroupInfoResult result = FSCMessage.PullGroupInfoResult.newBuilder().addAllInfo(GroupInfo.convert2WfcGroupInfos(infos)).build();
        byte[] data = result.toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
