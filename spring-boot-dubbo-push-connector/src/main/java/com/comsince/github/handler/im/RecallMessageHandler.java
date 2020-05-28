package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.MessageResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;
import java.util.Set;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-7-3 下午3:40
 **/
@Handler(value = IMTopic.RecallMessageTopic)
public class RecallMessageHandler extends IMHandler<FSCMessage.INT64Buf>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.INT64Buf int64Buf, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode = messageService.recallMessage(int64Buf.getId(), fromUser);

        if(errorCode != ErrorCode.ERROR_CODE_SUCCESS) {
            return errorCode;
        }

        MessageResponse message = messageService.getMessage(int64Buf.getId());
        if (message == null) {
            return ErrorCode.ERROR_CODE_NOT_EXIST;
        }

        Set<String> notifyReceivers = messageService.getNotifyReceivers(fromUser, message);
        this.publisher.publishRecall2Receivers(int64Buf.getId(), fromUser, notifyReceivers, clientID);

        return errorCode;
    }
}
