package com.comsince.github.model;

import java.io.Serializable;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午4:54
 **/
public class UserResponse implements Serializable {
    private int gender;
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "gender=" + gender +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
