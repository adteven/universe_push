package com.comsince.github.service.impl;

import com.comsince.github.SessionService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.SessionResponse;
import com.comsince.github.persistence.session.ISessionsStore;
import com.comsince.github.persistence.session.Session;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:10
 **/
@Service
public class SessionServiceImpl implements SessionService {
    Logger logger = LoggerFactory.getLogger(SessionServiceImpl.class);
    @Autowired
    ISessionsStore sessionsStore;

    @Override
    public Collection<SessionResponse> sessionForUser(String username) {
        Collection<Session> sessions = sessionsStore.sessionForUser(username);
        List<SessionResponse> sessionResponseList = new ArrayList<>();
        for(Session session : sessions){
            sessionResponseList.add(convertSession(session));
        }
        return sessionResponseList;
    }

    @Override
    public SessionResponse getSession(String clientID) {
        Session session = sessionsStore.getSession(clientID);
        if(session != null){
            logger.info("find session clientID {}",clientID);
            return convertSession(session);
        }
        return null;
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

    @Override
    public void dropQueue(String clientID) {
        sessionsStore.dropQueue(clientID);
    }

    @Override
    public void cleanSession(String clientID) {
        sessionsStore.cleanSession(clientID);
    }

    private SessionResponse convertSession(Session session){
        SessionResponse sessionResponse = new SessionResponse();
        sessionResponse.clientID = session.getClientID();
        sessionResponse.username = session.username;
        sessionResponse.setAppName(session.getAppName());
        sessionResponse.setCarrierName(session.getCarrierName());
        sessionResponse.setDbSecret(session.getDbSecret());
        sessionResponse.setDeviceName(session.getDeviceName());
        sessionResponse.setDeviceVersion(session.getDeviceVersion());
        sessionResponse.setSecret(session.getSecret());
        return sessionResponse;
    }
}
