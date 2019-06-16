package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.FriendData;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

import java.util.List;

@Handler(IMTopic.FriendPullTopic)
public class FriendPullHandler extends IMHandler<WFCMessage.Version> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        List<FriendData> friendDatas = messageService.getFriendList(fromUser, request.getVersion());
        LOG.info("friend data {}",friendDatas);
        WFCMessage.GetFriendsResult.Builder builder = WFCMessage.GetFriendsResult.newBuilder();
        for (FriendData data : friendDatas) {
            builder.addEntry(WFCMessage.Friend.newBuilder().setState(data.getState()).setUid(data.getFriendUid()).setUpdateDt(data.getTimestamp()).setAlias(data.getAlias()).build());
        }
        byte[] data = builder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
