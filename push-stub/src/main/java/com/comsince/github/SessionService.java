package com.comsince.github;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.session.ClientSession;
import com.comsince.github.session.Session;

import java.util.Collection;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:15
 **/
public interface SessionService {
    Collection<Session> sessionForUser(String username);
    Session getSession(String clientID);
    ErrorCode createNewSession(String username, String clientID, boolean cleanSession, boolean createNoExist);
    void loadUserSession(String username, String clientID);
    boolean sessionForClient(String clientID);
    boolean updateExistSession(String username, String clientID, boolean cleanSession);
}
