package com.comsince.github;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.UserResponse;

import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午10:24
 **/
public interface MessageService {
    ErrorCode saveAddFriendRequest(String userId, AddFriendMessage request, long[] head);
    UserResponse getUserInfo(String userId);
    int getUserStatus(String userId);

    long getMessageHead(String user);
    long getFriendHead(String user);
    long getFriendRqHead(String user);
    long getSettingHead(String user);

    List<UserResponse> searchUser(String keyword, boolean buzzy, int page);
}
