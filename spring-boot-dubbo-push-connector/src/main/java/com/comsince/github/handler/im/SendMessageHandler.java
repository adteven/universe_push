package com.comsince.github.handler.im;

import com.comsince.github.proto.ProtoConstants;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.utils.MessageShardingUtil;
import io.netty.buffer.ByteBuf;
import static com.comsince.github.proto.ProtoConstants.ContentType.Text;

@Handler(value = IMTopic.SendMessageTopic)
public class SendMessageHandler extends IMHandler<FSCMessage.Message> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.Message message, ImMessageProcessor.IMCallback callback) {
        ErrorCode errorCode = ErrorCode.ERROR_CODE_SUCCESS;
        if (message != null) {
            if (!isAdmin) {  //admin do not check the right
                int userStatus = messageService.getUserStatus(fromUser);
                if (userStatus == 1 || userStatus == 2) {
                    return ErrorCode.ERROR_CODE_FORBIDDEN_SEND_MSG;
                }

                if (message.getConversation().getType() == ProtoConstants.ConversationType.ConversationType_Private) {
                    if (messageService.isBlacked(message.getConversation().getTarget(), fromUser)) {
                        return ErrorCode.ERROR_CODE_IN_BLACK_LIST;
                    }
                }

                if (message.getConversation().getType() == ProtoConstants.ConversationType.ConversationType_Group && !messageService.isMemberInGroup(fromUser, message.getConversation().getTarget())) {
                    return ErrorCode.ERROR_CODE_NOT_IN_GROUP;
                } else if (message.getConversation().getType() == ProtoConstants.ConversationType.ConversationType_Group && messageService.isForbiddenInGroup(fromUser, message.getConversation().getTarget())) {
                    return ErrorCode.ERROR_CODE_NOT_RIGHT;
                } else if (message.getConversation().getType() == ProtoConstants.ConversationType.ConversationType_ChatRoom && !messageService.checkUserClientInChatroom(fromUser, clientID, message.getConversation().getTarget())) {
                    return ErrorCode.ERROR_CODE_NOT_IN_CHATROOM;
                } else if (message.getConversation().getType() == ProtoConstants.ConversationType.ConversationType_Channel && !messageService.checkUserInChannel(fromUser, message.getConversation().getTarget())) {
                    return ErrorCode.ERROR_CODE_NOT_IN_CHANNEL;
                }
            }

            long timestamp = System.currentTimeMillis();
            long messageId = MessageShardingUtil.generateId();
            LOG.info("generate messageId "+messageId);
            message = message.toBuilder().setFromUser(fromUser).setMessageId(messageId).setServerTimestamp(timestamp).build();


            boolean ignoreMsg = false;
            if (!isAdmin && message.getContent().getType() == Text) {
                message = message.toBuilder().setContent(message.getContent().toBuilder().setSearchableContent(message.getContent().getSearchableContent()).build()).build();
            }

            if (errorCode == ErrorCode.ERROR_CODE_SUCCESS) {
                if (!ignoreMsg) {
                    saveAndPublish(fromUser, clientID, message);
                }
                ackPayload = ackPayload.capacity(20);
                ackPayload.writeLong(messageId);
                ackPayload.writeLong(timestamp);
            }
        } else {
            errorCode = ErrorCode.ERROR_CODE_INVALID_MESSAGE;
        }
        return errorCode;
    }
}
