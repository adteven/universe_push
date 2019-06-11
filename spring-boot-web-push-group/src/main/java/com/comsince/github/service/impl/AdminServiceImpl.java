package com.comsince.github.service.impl;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.controller.im.pojo.*;
import com.comsince.github.persistence.IMessagesStore;
import com.comsince.github.security.TokenAuthenticator;
import com.comsince.github.service.AdminService;
import com.comsince.github.session.ISessionsStore;
import com.comsince.github.session.Session;
import com.comsince.github.util.UUIDGenerator;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Base64;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午11:02
 **/
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    IMessagesStore messagesStore;

    @Autowired
    ISessionsStore sessionsStore;

    @Override
    public InputOutputUserInfo getUserInfo(InputGetUserInfo inputUserId) {
        InputOutputUserInfo inputOutputUserInfo = null;
        if (inputUserId != null
                && (!StringUtil.isNullOrEmpty(inputUserId.getUserId())
                || !StringUtil.isNullOrEmpty(inputUserId.getName())
                || !StringUtil.isNullOrEmpty(inputUserId.getMobile()))) {
            WFCMessage.User user = null;
            if(!StringUtil.isNullOrEmpty(inputUserId.getUserId())) {
                user = messagesStore.getUserInfo(inputUserId.getUserId());
            } else if(!StringUtil.isNullOrEmpty(inputUserId.getName())) {
                user = messagesStore.getUserInfoByName(inputUserId.getName());
            } else if(!StringUtil.isNullOrEmpty(inputUserId.getMobile())) {
                user = messagesStore.getUserInfoByMobile(inputUserId.getMobile());
            }
            if(user != null){
                inputOutputUserInfo = InputOutputUserInfo.fromPbUser(user);
            }
        }
        return inputOutputUserInfo;
    }

    @Override
    public OutputCreateUser createUser(InputOutputUserInfo inputCreateUser) {
        if (inputCreateUser != null
                && !StringUtil.isNullOrEmpty(inputCreateUser.getName())) {

            if(StringUtil.isNullOrEmpty(inputCreateUser.getPassword())) {
                inputCreateUser.setPassword(UUIDGenerator.getUUID());
            }

            if(StringUtil.isNullOrEmpty(inputCreateUser.getUserId())) {
                inputCreateUser.setUserId(messagesStore.getShortUUID());
            }

            WFCMessage.User.Builder newUserBuilder = WFCMessage.User.newBuilder()
                    .setUid(StringUtil.isNullOrEmpty(inputCreateUser.getUserId()) ? "" : inputCreateUser.getUserId());
            if (inputCreateUser.getName() != null)
                newUserBuilder.setName(inputCreateUser.getName());
            if (inputCreateUser.getDisplayName() != null)
                newUserBuilder.setDisplayName(StringUtil.isNullOrEmpty(inputCreateUser.getDisplayName()) ? inputCreateUser.getName() : inputCreateUser.getDisplayName());
            if (inputCreateUser.getPortrait() != null)
                newUserBuilder.setPortrait(inputCreateUser.getPortrait());
            if (inputCreateUser.getEmail() != null)
                newUserBuilder.setEmail(inputCreateUser.getEmail());
            if (inputCreateUser.getAddress() != null)
                newUserBuilder.setAddress(inputCreateUser.getAddress());
            if (inputCreateUser.getCompany() != null)
                newUserBuilder.setCompany(inputCreateUser.getCompany());

            if (inputCreateUser.getSocial() != null)
                newUserBuilder.setSocial(inputCreateUser.getSocial());


            if (inputCreateUser.getMobile() != null)
                newUserBuilder.setMobile(inputCreateUser.getMobile());
            newUserBuilder.setGender(inputCreateUser.getGender());
            if (inputCreateUser.getExtra() != null)
                newUserBuilder.setExtra(inputCreateUser.getExtra());

            newUserBuilder.setUpdateDt(System.currentTimeMillis());

            messagesStore.addUserInfo(newUserBuilder.build(), inputCreateUser.getPassword());

            return new OutputCreateUser(inputCreateUser.getUserId(), inputCreateUser.getName());
        } else {
            return null;
        }
    }

    @Override
    public OutputGetIMTokenData getUserToken(InputGetToken inputGetToken) {
        Session session = sessionsStore.createUserSession(inputGetToken.getUserId(), inputGetToken.getClientId());
        TokenAuthenticator authenticator = new TokenAuthenticator();
        String strToken = authenticator.generateToken(inputGetToken.getUserId());
        String result = strToken + "|" + session.getSecret() + "|" + session.getDbSecret();
        String token = Base64.getEncoder().encodeToString(result.getBytes());
        return new OutputGetIMTokenData(inputGetToken.getUserId(), token);
    }
}
