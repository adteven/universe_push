package com.comsince.github.model;

import com.comsince.github.proto.FSCMessage;

import java.io.Serializable;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-7-11 上午10:44
 **/
public class Conversation implements Serializable {
    private int conversationType;
    private String target;
    private int line;

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

    public static Conversation convert2Conversation(FSCMessage.Conversation wfcConversation){
        Conversation conversation = new Conversation();
        conversation.setConversationType(wfcConversation.getType());
        conversation.setLine(wfcConversation.getLine());
        conversation.setTarget(wfcConversation.getTarget());
        return conversation;
    }


    public static FSCMessage.Conversation convert2WfcConversation(Conversation conversation){
        FSCMessage.Conversation wfcConversation = FSCMessage.Conversation.newBuilder()
                .setLine(conversation.getLine())
                .setTarget(conversation.getTarget())
                .setType(conversation.getConversationType())
                .build();
        return wfcConversation;
    }
}
