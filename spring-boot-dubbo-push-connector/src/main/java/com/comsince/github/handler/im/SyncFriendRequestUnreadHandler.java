package com.comsince.github.handler.im;

import com.comsince.github.SubSignal;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

@Handler(IMTopic.RriendRequestUnreadSyncTopic)
public class SyncFriendRequestUnreadHandler extends IMHandler<FSCMessage.Version>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        long unReadSize = messageService.SyncFriendRequestUnread(fromUser, request.getVersion());
        LOG.info("unread size {}",unReadSize);
        publisher.publishNotification(SubSignal.FRN, fromUser, unReadSize);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
