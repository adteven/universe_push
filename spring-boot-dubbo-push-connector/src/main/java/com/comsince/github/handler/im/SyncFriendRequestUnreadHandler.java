package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.SubSignal;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

@Handler(IMTopic.RriendRequestUnreadSyncTopic)
public class SyncFriendRequestUnreadHandler extends IMHandler<WFCMessage.Version>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.Version request, ImMessageProcessor.IMCallback callback) {
        long unReadSize = messageService.SyncFriendRequestUnread(fromUser, request.getVersion());
        LOG.info("unread size {}",unReadSize);
        publisher.publishNotification(SubSignal.FRN, fromUser, unReadSize);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
