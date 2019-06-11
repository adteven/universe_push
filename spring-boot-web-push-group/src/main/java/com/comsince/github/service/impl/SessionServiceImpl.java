package com.comsince.github.service.impl;

import com.comsince.github.SessionService;
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
}
