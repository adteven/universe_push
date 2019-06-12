package com.comsince.github.controller.im.pojo;

public class LoginRequest {
    //电话号码一般为手机号码
    private String mobile;
    private String code;
    //设备的唯一标志，一般为手机设备的imei号
    private String clientId;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
