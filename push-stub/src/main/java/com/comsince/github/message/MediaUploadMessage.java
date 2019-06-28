package com.comsince.github.message;


import java.io.Serializable;

public class MediaUploadMessage implements Serializable {
    private int mediaType;
    private String key;

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
