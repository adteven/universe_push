package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.UserResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午4:53
 **/
@Handler(IMTopic.UserSearchTopic)
public class UserSearchHandler extends IMHandler<FSCMessage.SearchUserRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.SearchUserRequest request, ImMessageProcessor.IMCallback callback) {
        List<UserResponse> userResponseList = messageService.searchUser(request.getKeyword(), request.getFuzzy() > 0, request.getPage());
        LOG.info("search user result "+userResponseList);
        List<FSCMessage.User> wfcUsers = new ArrayList<>();
        for(UserResponse userResponse : userResponseList){
            FSCMessage.User user = FSCMessage.User.newBuilder()
                    .setUid(userResponse.getUid())
                    .setGender(userResponse.getGender())
                    .setDisplayName(userResponse.getDisplayName())
                    .setAddress(userResponse.getAddress())
                    .setEmail(userResponse.getEmail())
                    .setUpdateDt(userResponse.getUpdateDt())
                    .setMobile(userResponse.getMobile())
                    .setCompany(userResponse.getCompany())
                    .setPortrait(userResponse.getPortrait())
                    .build();
            wfcUsers.add(user);
        }
        FSCMessage.SearchUserResult.Builder builder = FSCMessage.SearchUserResult.newBuilder();
        builder.addAllEntry(wfcUsers);
        byte[] data = builder.build().toByteArray();
        LOG.info("user length "+data.length);
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
