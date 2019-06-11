package com.comsince.github;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午10:24
 **/
public interface MessageService {
    ErrorCode saveAddFriendRequest(String userId, AddFriendMessage request, long[] head);
    WFCMessage.User getUserInfo(String userId);
}
