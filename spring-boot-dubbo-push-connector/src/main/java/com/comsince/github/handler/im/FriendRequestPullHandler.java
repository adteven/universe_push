package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.FriendRequestResponse;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

@Handler(IMTopic.FriendRequestPullTopic)
public class FriendRequestPullHandler extends IMHandler<WFCMessage.Version>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        List<FriendRequestResponse> friendDatas = messageService.getFriendRequestList(fromUser, request.getVersion());
        LOG.info("pull friend request head {} friendDatas {}",request.getVersion(),friendDatas);
        List<WFCMessage.FriendRequest> friendRequests = new ArrayList<>();
        for(FriendRequestResponse friendRequestResponse : friendDatas){
            friendRequests.add(FriendRequestResponse.convertFriendRequestResponse(friendRequestResponse));
        }
        WFCMessage.GetFriendRequestResult.Builder builder = WFCMessage.GetFriendRequestResult.newBuilder();
        builder.addAllEntry(friendRequests);
        byte[] data = builder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
