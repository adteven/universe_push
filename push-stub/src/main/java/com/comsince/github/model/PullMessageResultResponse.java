package com.comsince.github.model;

import cn.wildfirechat.proto.WFCMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PullMessageResultResponse implements Serializable {
    private long current;
    private long head;

    private List<MessageResponse> messageResponseList;

    public int getMessageCount(){
        return messageResponseList != null ? messageResponseList.size() : 0;
    }

    public List<MessageResponse> getMessageResponseList() {
        return messageResponseList;
    }

    public void setMessageResponseList(List<MessageResponse> messageResponseList) {
        this.messageResponseList = messageResponseList;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getHead() {
        return head;
    }

    public void setHead(long head) {
        this.head = head;
    }

    public static PullMessageResultResponse convertPullMessage(WFCMessage.PullMessageResult pullMessageResult){
        PullMessageResultResponse pullMessageResultResponse = new PullMessageResultResponse();
        pullMessageResultResponse.setCurrent(pullMessageResult.getCurrent());
        pullMessageResultResponse.setHead(pullMessageResult.getHead());
        List<MessageResponse> messageResponses = new ArrayList<>();
        for(WFCMessage.Message message : pullMessageResult.getMessageList()){
            messageResponses.add(MessageResponse.convertMessageResponse(message));
        }
        pullMessageResultResponse.setMessageResponseList(messageResponses);
        return pullMessageResultResponse;
    }

    public static WFCMessage.PullMessageResult convertWFCPullResult(PullMessageResultResponse pullMessageResultResponse){
        WFCMessage.PullMessageResult.Builder builder = WFCMessage.PullMessageResult.newBuilder();
        builder.setCurrent(pullMessageResultResponse.getCurrent());
        builder.setHead(pullMessageResultResponse.getHead());
        List<MessageResponse> messageResponses = pullMessageResultResponse.getMessageResponseList();
        if(messageResponses != null){
            for(MessageResponse messageResponse : messageResponses){
                builder.addMessage(MessageResponse.convertWFCMessage(messageResponse));
            }
        }
        return builder.build();
    }

    @Override
    public String toString() {
        return "PullMessageResultResponse{" +
                "current=" + current +
                ", head=" + head +
                ", messageResponseList=" + messageResponseList +
                '}';
    }
}
