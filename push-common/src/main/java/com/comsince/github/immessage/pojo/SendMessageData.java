/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.immessage.pojo;

import com.comsince.github.proto.FSCMessage;
import io.netty.util.internal.StringUtil;

public class SendMessageData {
    private String sender;
    private Conversation conv;
    private MessagePayload payload;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Conversation getConv() {
        return conv;
    }

    public void setConv(Conversation conv) {
        this.conv = conv;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public static boolean isValide(SendMessageData sendMessageData) {
        if(sendMessageData == null ||
            sendMessageData.getConv() == null ||
            sendMessageData.getConv().getType() < 0 ||
            sendMessageData.getConv().getType() > 6 ||
            StringUtil.isNullOrEmpty(sendMessageData.getConv().getTarget()) ||
            StringUtil.isNullOrEmpty(sendMessageData.getSender()) ||
            sendMessageData.getPayload() == null) {
            return false;
        }
        return true;
    }

    public FSCMessage.Message toProtoMessage() {
        return FSCMessage.Message.newBuilder().setFromUser(sender)
            .setConversation(FSCMessage.Conversation.newBuilder().setType(conv.getType()).setTarget(conv.getTarget()).setLine(conv.getLine()))
            .setContent(payload.toProtoMessageContent())
            .build();
    }

    public static SendMessageData fromProtoMessage(FSCMessage.Message protoMessage) {
        SendMessageData data = new SendMessageData();
        data.sender = protoMessage.getFromUser();
        data.conv = new Conversation();
        data.conv.setTarget(protoMessage.getConversation().getTarget());
        data.conv.setType(protoMessage.getConversation().getType());
        data.conv.setLine(protoMessage.getConversation().getLine());
        data.payload = MessagePayload.fromProtoMessageContent(protoMessage.getContent());

        return data;
    }
}
