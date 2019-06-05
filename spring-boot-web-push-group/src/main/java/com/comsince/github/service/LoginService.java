package com.comsince.github.service;

import com.comsince.github.controller.im.model.RestResult;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午9:44
 **/
public interface LoginService {
    RestResult sendCode(String mobile);
    RestResult login(String mobile, String code, String clientId);
}
