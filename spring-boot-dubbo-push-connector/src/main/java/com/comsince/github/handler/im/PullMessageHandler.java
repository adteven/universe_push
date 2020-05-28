package com.comsince.github.handler.im;

import com.comsince.github.proto.ProtoConstants;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.PullMessageResultResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

@Handler(value = IMTopic.PullMessageTopic)
public class PullMessageHandler extends IMHandler<FSCMessage.PullMessageRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.PullMessageRequest request, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode = ErrorCode.ERROR_CODE_SUCCESS;

        if (request.getType() == ProtoConstants.PullType.Pull_ChatRoom && !messageService.checkUserClientInChatroom(fromUser, clientID, null)) {
            errorCode = ErrorCode.ERROR_CODE_NOT_IN_CHATROOM;
        } else {
            PullMessageResultResponse result = messageService.fetchMessage(fromUser, clientID, request.getId(), request.getType());
            FSCMessage.PullMessageResult pullMessageResult = PullMessageResultResponse.convertWFCPullResult(result);
            byte[] data = pullMessageResult.toByteArray();
            LOG.info("User {} pull message with count({}), payload size({})", fromUser, result.getMessageCount(), data.length);
            ackPayload.ensureWritable(data.length).writeBytes(data);
        }
        return errorCode;
    }
}
