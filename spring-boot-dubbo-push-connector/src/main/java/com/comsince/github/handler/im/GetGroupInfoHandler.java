package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.GroupInfo;
import com.comsince.github.model.PullUserRequest;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

import java.util.List;

@Handler(IMTopic.GetGroupInfoTopic)
public class GetGroupInfoHandler extends IMHandler<WFCMessage.PullUserRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.PullUserRequest request, ImMessageProcessor.IMCallback callback) {
        List<GroupInfo> infos = messageService.getGroupInfos(PullUserRequest.convertToUserRequests(request.getRequestList()));

        WFCMessage.PullGroupInfoResult result = WFCMessage.PullGroupInfoResult.newBuilder().addAllInfo(GroupInfo.convert2WfcGroupInfos(infos)).build();
        byte[] data = result.toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
