package com.comsince.github;

import com.comsince.github.session.Session;

import java.util.Collection;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:15
 **/
public interface SessionService {
    Collection<Session> sessionForUser(String username);
}
