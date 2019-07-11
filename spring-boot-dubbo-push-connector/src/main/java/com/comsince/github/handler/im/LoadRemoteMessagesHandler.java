package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.Conversation;
import com.comsince.github.model.PullMessageResultResponse;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-7-11 上午10:17
 **/
@Handler(value = IMTopic.LoadRemoteMessagesTopic)
public class LoadRemoteMessagesHandler extends IMHandler<WFCMessage.LoadRemoteMessages>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.LoadRemoteMessages request, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode = ErrorCode.ERROR_CODE_SUCCESS;

        long beforeUid = request.getBeforeUid();
        if (beforeUid == 0) {
            beforeUid = Long.MAX_VALUE;
        }
        Conversation conversation = Conversation.convert2Conversation(request.getConversation());
        PullMessageResultResponse result = messageService.loadRemoteMessages(fromUser, conversation, beforeUid, request.getCount());
        WFCMessage.PullMessageResult pullMessageResult = PullMessageResultResponse.convertWFCPullResult(result);
        byte[] data = pullMessageResult.toByteArray();
        LOG.info("User {} beforeUid {} load message with count({}), payload size({})", fromUser, beforeUid,result.getMessageCount(), data.length);
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return errorCode;
    }
}
