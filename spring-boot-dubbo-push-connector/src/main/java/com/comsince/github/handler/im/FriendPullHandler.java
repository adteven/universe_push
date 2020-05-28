package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.FriendData;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import java.util.List;

@Handler(IMTopic.FriendPullTopic)
public class FriendPullHandler extends IMHandler<FSCMessage.Version> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        List<FriendData> friendDatas = messageService.getFriendList(fromUser, request.getVersion());
        LOG.info("friend data {}",friendDatas);
        FSCMessage.GetFriendsResult.Builder builder = FSCMessage.GetFriendsResult.newBuilder();
        for (FriendData data : friendDatas) {
            builder.addEntry(FSCMessage.Friend.newBuilder().setState(data.getState()).setUid(data.getFriendUid()).setUpdateDt(data.getTimestamp()).setAlias(data.getAlias()).build());
        }
        byte[] data = builder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
