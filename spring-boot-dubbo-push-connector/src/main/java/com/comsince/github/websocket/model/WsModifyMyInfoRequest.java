package com.comsince.github.websocket.model;

public class WsModifyMyInfoRequest {
    int type;
    String value;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "WsModifyMyInfoRequest{" +
                "type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
