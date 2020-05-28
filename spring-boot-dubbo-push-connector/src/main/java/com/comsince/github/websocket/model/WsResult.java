package com.comsince.github.websocket.model;

import com.comsince.github.common.ErrorCode;

public class WsResult {
    public enum  RestCode {
        SUCCESS(200, "success"),
        ERROR(400,"error");
        public int code;
        public String msg;

        RestCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }
    private int code;
    private String message;
    private Object result;

    public static WsResult ok(Object object) {
        return new WsResult(RestCode.SUCCESS, object);
    }

    public static WsResult error(RestCode code) {
        return new WsResult(code, null);
    }

    public static WsResult resultOf(ErrorCode errorCode) {
        return resultOf(errorCode, errorCode.msg, null);
    }

    public static WsResult resultOf(ErrorCode errorCode, String msg) {
        return resultOf(errorCode, msg, null);
    }

    public static WsResult resultOf(ErrorCode errorCode, String msg, Object object) {
        WsResult result = new WsResult();
        result.code = errorCode.code;
        result.message = msg;
        result.result = object;
        return result;
    }

    private WsResult(){

    }

    private WsResult(RestCode code, Object result) {
        this.code = code.code;
        this.message = code.msg;
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
