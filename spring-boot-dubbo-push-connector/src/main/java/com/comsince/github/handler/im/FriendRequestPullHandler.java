package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.FriendRequestResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

@Handler(IMTopic.FriendRequestPullTopic)
public class FriendRequestPullHandler extends IMHandler<FSCMessage.Version>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        List<FriendRequestResponse> friendDatas = messageService.getFriendRequestList(fromUser, request.getVersion());
        LOG.info("pull friend request head {} friendDatas {}",request.getVersion(),friendDatas);
        List<FSCMessage.FriendRequest> friendRequests = new ArrayList<>();
        for(FriendRequestResponse friendRequestResponse : friendDatas){
            friendRequests.add(FriendRequestResponse.convertFriendRequestResponse(friendRequestResponse));
        }
        FSCMessage.GetFriendRequestResult.Builder builder = FSCMessage.GetFriendRequestResult.newBuilder();
        builder.addAllEntry(friendRequests);
        byte[] data = builder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
