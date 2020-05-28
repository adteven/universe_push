/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.controller.im.pojo;

import com.comsince.github.proto.ProtoConstants;
import com.comsince.github.immessage.pojo.MessagePayload;
import com.comsince.github.proto.FSCMessage;
import io.netty.util.internal.StringUtil;

public class BroadMessageData {
    private String sender;
    private int line;
    private MessagePayload payload;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public MessagePayload getPayload() {
        return payload;
    }

    public void setPayload(MessagePayload payload) {
        this.payload = payload;
    }

    public static boolean isValide(BroadMessageData sendMessageData) {
        if(sendMessageData == null ||
            StringUtil.isNullOrEmpty(sendMessageData.getSender()) ||
            sendMessageData.getPayload() == null) {
            return false;
        }
        return true;
    }

    public FSCMessage.Message toProtoMessage() {
        return FSCMessage.Message.newBuilder().setFromUser(sender)
            .setConversation(FSCMessage.Conversation.newBuilder().setType(ProtoConstants.ConversationType.ConversationType_Private).setTarget(sender).setLine(line))
            .setContent(payload.toProtoMessageContent())
            .build();
    }
}
