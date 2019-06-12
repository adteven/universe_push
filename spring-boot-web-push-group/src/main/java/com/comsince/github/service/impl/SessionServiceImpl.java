package com.comsince.github.service.impl;

import com.comsince.github.SessionService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.session.ClientSession;
import com.comsince.github.session.ISessionsStore;
import com.comsince.github.session.Session;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:10
 **/
@Service
public class SessionServiceImpl implements SessionService {
    @Autowired
    ISessionsStore sessionsStore;

    @Override
    public Collection<Session> sessionForUser(String username) {
        return sessionsStore.sessionForUser(username);
    }

    @Override
    public Session getSession(String clientID) {
        return sessionsStore.getSession(clientID);
    }

    @Override
    public ErrorCode createNewSession(String username, String clientID, boolean cleanSession, boolean createNoExist) {
        return sessionsStore.createNewSession(username,clientID,cleanSession,createNoExist);
    }

    @Override
    public void loadUserSession(String username, String clientID) {
        sessionsStore.loadUserSession(username,clientID);
    }

    @Override
    public boolean sessionForClient(String clientID) {
        return sessionsStore.sessionForClient(clientID) != null ? true : false;
    }

    @Override
    public boolean updateExistSession(String username, String clientID, boolean cleanSession) {
        return sessionsStore.updateExistSession(username,clientID,null,cleanSession) != null ? true : false;
    }
}
