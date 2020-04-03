package com.comsince.github.websocket.model;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-4-3 下午5:07
 **/
public class WsUserSearchRequest {
    String keyword;
    int fuzzy;
    int page;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getFuzzy() {
        return fuzzy;
    }

    public void setFuzzy(int fuzzy) {
        this.fuzzy = fuzzy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public String toString() {
        return "WsUserSearchRequest{" +
                "keyword='" + keyword + '\'' +
                ", fuzzy=" + fuzzy +
                ", page=" + page +
                '}';
    }
}
