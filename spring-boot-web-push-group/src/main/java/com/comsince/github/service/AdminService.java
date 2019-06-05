package com.comsince.github.service;

import com.comsince.github.controller.im.pojo.*;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午10:58
 **/
public interface AdminService {
    /**
     * 获取用户信息
     * */
    InputOutputUserInfo getUserInfo(InputGetUserInfo inputGetUserInfo);

    /**
     * 创建用户
     * */
    OutputCreateUser createUser(InputOutputUserInfo inputOutputUserInfo);


    OutputGetIMTokenData getUserToken(InputGetToken inputGetToken);
}
