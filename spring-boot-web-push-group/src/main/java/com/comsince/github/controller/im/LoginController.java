package com.comsince.github.controller.im;

import com.comsince.github.controller.im.pojo.LoginRequest;
import com.comsince.github.controller.im.pojo.SendCodeRequest;
import com.comsince.github.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午9:42
 *
 * 登录相关接口,处理客户端登录相关请求
 **/
@RestController
public class LoginController {
    @Autowired
    private LoginService loginService;

    @PostMapping(value = "/send_code", produces = "application/json;charset=UTF-8"   )
    public Object sendCode(@RequestBody SendCodeRequest request) {
        return loginService.sendCode(request.getMobile());
    }

    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8"   )
    public Object login(@RequestBody LoginRequest request) {
        return loginService.login(request.getMobile(), request.getCode(), request.getClientId());
    }
}
