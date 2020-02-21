package com.comsince.github.websocket.model;

public class WsUploadTokenResponse {
    String domain;
    String server;
    String token;
    int port;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "WsUploadTokenResponse{" +
                "domain='" + domain + '\'' +
                ", server='" + server + '\'' +
                ", token='" + token + '\'' +
                ", port=" + port +
                '}';
    }
}
