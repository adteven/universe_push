package com.comsince.github.model;

import com.comsince.github.proto.FSCMessage;
import com.google.protobuf.ByteString;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;

import static com.comsince.github.proto.ProtoConstants.PersistFlag.Transparent;
import static com.comsince.github.model.MessageContentType.*;

public class MessageResponse implements Serializable {
    private int conversationType;
    private String target;
    private int line;
    private String from;
    private String[] tos;
    private MessageConentResponse content;
    private long messageId;
    private int direction;
    private int status;
    private long messageUid;
    private long timestamp;

    public int getConversationType() {
        return conversationType;
    }

    public void setConversationType(int conversationType) {
        this.conversationType = conversationType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }

    public MessageConentResponse getContent() {
        return content;
    }

    public void setContent(MessageConentResponse content) {
        this.content = content;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getMessageUid() {
        return messageUid;
    }

    public void setMessageUid(long messageUid) {
        this.messageUid = messageUid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public static MessageResponse convertMessageResponse(FSCMessage.Message message){
        MessageResponse messageResponse = new MessageResponse();
        messageResponse.setFrom(message.getFromUser());
        messageResponse.setMessageId(message.getMessageId());
        messageResponse.setTimestamp(message.getServerTimestamp());
        FSCMessage.Conversation conversation  = message.getConversation();
        messageResponse.setConversationType(conversation.getType());
        messageResponse.setTarget(conversation.getTarget());
        messageResponse.setLine(conversation.getLine());
        FSCMessage.MessageContent messageContent = message.getContent();
        MessageConentResponse messageConentResponse = new MessageConentResponse();
        messageConentResponse.setType(messageContent.getType());
        messageConentResponse.setBinaryContent(messageContent.getData().toByteArray());
        messageConentResponse.setContent(message.getContent().getContent());
        messageConentResponse.setPushContent(message.getContent().getPushContent());
        messageConentResponse.setSearchableContent(messageContent.getSearchableContent());
        messageConentResponse.setRemoteMediaUrl(messageContent.getRemoteMediaUrl());
        messageConentResponse.setMediaType(messageContent.getMediaType());
        messageResponse.setContent(messageConentResponse);
        return messageResponse;
    }

    public static FSCMessage.Message convertWFCMessage(MessageResponse messageResponse){
        FSCMessage.Message.Builder builder = FSCMessage.Message.newBuilder();
        builder.setMessageId(messageResponse.getMessageId());
        builder.setServerTimestamp(messageResponse.getTimestamp());
        if(!StringUtils.isEmpty(messageResponse.getFrom())){
            builder.setFromUser(messageResponse.getFrom());
        }
        FSCMessage.Conversation conversation = FSCMessage.Conversation.newBuilder()
                .setType(messageResponse.getConversationType())
                .setLine(messageResponse.getLine())
                .setTarget(messageResponse.getTarget())
                .build();
        builder.setConversation(conversation);
        FSCMessage.MessageContent.Builder messageContentBuilder = FSCMessage.MessageContent.newBuilder();
        messageContentBuilder.setType(messageResponse.getContent().getType());
        if(messageResponse.getContent().getBinaryContent() != null){
            messageContentBuilder.setData(ByteString.copyFrom(messageResponse.getContent().getBinaryContent()));
        }
        if(!StringUtils.isEmpty(messageResponse.getContent().getContent())){
            messageContentBuilder.setContent(messageResponse.getContent().getContent());
        }
        if(!StringUtils.isEmpty(messageResponse.getContent().getSearchableContent())){
            messageContentBuilder.setSearchableContent(messageResponse.getContent().getSearchableContent());
        }
        if(!StringUtils.isEmpty(messageResponse.getContent().getPushContent())){
            messageContentBuilder.setPushContent(messageResponse.getContent().getPushContent());
        }

        messageContentBuilder.setMediaType(messageResponse.getContent().getMediaType());
        if(!StringUtils.isEmpty(messageResponse.getContent().getRemoteMediaUrl())){
            messageContentBuilder.setRemoteMediaUrl(messageResponse.getContent().getRemoteMediaUrl());
        }

        if(isTransparentFlag(messageResponse.getContent().getType())){
            messageContentBuilder.setPersistFlag(Transparent);
        }

        builder.setContent(messageContentBuilder.build());
        return builder.build();
    }

    private static boolean isTransparentFlag(int contentType){
        return contentType == ContentType_Typing ||
                contentType == ContentType_Call_Accept_T ||
                contentType == ContentType_Call_Signal ||
                contentType == ContentType_Call_End ||
                contentType == ContentType_Call_Modify ||
                contentType == ContentType_Call_Accept ;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "conversationType=" + conversationType +
                ", target='" + target + '\'' +
                ", line=" + line +
                ", from='" + from + '\'' +
                ", tos=" + Arrays.toString(tos) +
                ", content=" + content +
                ", messageId=" + messageId +
                ", direction=" + direction +
                ", status=" + status +
                ", messageUid=" + messageUid +
                ", timestamp=" + timestamp +
                '}';
    }
}
