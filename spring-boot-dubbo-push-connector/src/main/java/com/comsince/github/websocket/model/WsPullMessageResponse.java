package com.comsince.github.websocket.model;

import com.comsince.github.model.MessageResponse;

import java.io.Serializable;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-1-2 下午2:56
 **/
public class WsPullMessageResponse implements Serializable {
    private String current;
    private String head;

    private List<MessageResponse> messageResponseList;

    public WsPullMessageResponse(String current, String head, List<MessageResponse> messageResponseList) {
        this.current = current;
        this.head = head;
        this.messageResponseList = messageResponseList;
    }

    public int getMessageCount(){
        return messageResponseList != null ? messageResponseList.size() : 0;
    }


    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public List<MessageResponse> getMessageResponseList() {
        return messageResponseList;
    }

    public void setMessageResponseList(List<MessageResponse> messageResponseList) {
        this.messageResponseList = messageResponseList;
    }
}
