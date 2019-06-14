package com.comsince.github.service.impl;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.MessageService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.UserResponse;
import com.comsince.github.persistence.IMessagesStore;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:08
 **/
@Service
public class MessageServiceImpl implements MessageService {
    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    IMessagesStore messagesStore;

    @Override
    public ErrorCode saveAddFriendRequest(String userId, AddFriendMessage request, long[] head) {
        logger.info("request targetUid {} reason {}",request.getTargetUid(),request.getReason());
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setTargetUid(request.getTargetUid())
                .setReason(request.getReason())
                .build();
        return messagesStore.saveAddFriendRequest(userId,addFriendRequest,head);
    }

    @Override
    public UserResponse getUserInfo(String userId) {
        UserResponse userResponse = null;
        WFCMessage.UserRequest userRequest = WFCMessage.UserRequest.newBuilder().setUid(userId).build();
        List<WFCMessage.UserRequest> userRequests = new ArrayList<>();
        userRequests.add(userRequest);
        WFCMessage.PullUserResult.Builder resultBuilder = WFCMessage.PullUserResult.newBuilder();
        messagesStore.getUserInfo(userRequests,resultBuilder);
        for(WFCMessage.UserResult userResult : resultBuilder.getResultList()){
            WFCMessage.User user = userResult.getUser();
            userResponse = convertWFCUser(user);
        }
        return userResponse;
    }


    @Override
    public int getUserStatus(String userId) {
        return messagesStore.getUserStatus(userId);
    }

    @Override
    public long getMessageHead(String user) {
        return messagesStore.getMessageHead(user);
    }

    @Override
    public long getFriendHead(String user) {
        return messagesStore.getFriendHead(user);
    }

    @Override
    public long getFriendRqHead(String user) {
        return messagesStore.getFriendRqHead(user);
    }

    @Override
    public long getSettingHead(String user) {
        return messagesStore.getSettingHead(user);
    }

    @Override
    public List<UserResponse> searchUser(String keyword, boolean buzzy, int page) {
        List<UserResponse> userResponseList = new ArrayList<>();
        List<WFCMessage.User> users = messagesStore.searchUser(keyword,buzzy,page);
        for(WFCMessage.User user : users){
            UserResponse userResponse = convertWFCUser(user);
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }


    private UserResponse convertWFCUser(WFCMessage.User user){
        UserResponse userResponse = new UserResponse();
        userResponse.setUid(user.getUid());
        userResponse.setAddress(user.getAddress());
        userResponse.setCompany(user.getCompany());
        userResponse.setEmail(user.getEmail());
        userResponse.setName(user.getName());
        userResponse.setMobile(user.getMobile());
        userResponse.setDisplayName(user.getDisplayName());
        userResponse.setGender(user.getGender());
        userResponse.setExtra(user.getExtra());
        userResponse.setPortrait(user.getPortrait());
        userResponse.setUpdateDt(user.getUpdateDt());
        return userResponse;
    }


}
