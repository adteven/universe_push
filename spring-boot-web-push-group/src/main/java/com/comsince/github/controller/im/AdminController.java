package com.comsince.github.controller.im;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.controller.im.model.RestResult;
import com.comsince.github.controller.im.pojo.InputGetUserInfo;
import com.comsince.github.controller.im.pojo.InputOutputUserInfo;
import com.comsince.github.controller.im.pojo.OutputCreateUser;
import com.comsince.github.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午10:01
 *
 *
 * 处理Admin相关接口
 **/
@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @PostMapping(value = "/admin/user/get_info", produces = "application/json;charset=UTF-8"   )
    public RestResult getUserInfo(@RequestBody InputGetUserInfo inputGetUserInfo){
        RestResult result;
        if(inputGetUserInfo != null){
            InputOutputUserInfo user = adminService.getUserInfo(inputGetUserInfo);
            if (user == null) {
                result = RestResult.resultOf(ErrorCode.ERROR_CODE_NOT_EXIST);
            } else {
                result = RestResult.ok(user);
            }
        } else {
            result = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
        }
        return result;
    }

    @PostMapping(value = "/admin/user/create", produces = "application/json;charset=UTF-8"   )
    public RestResult creatUser(@RequestBody InputOutputUserInfo inputOutputUserInfo){
        RestResult restResult;
        OutputCreateUser outputCreateUser = adminService.createUser(inputOutputUserInfo);
        if(outputCreateUser != null){
            restResult = RestResult.ok(outputCreateUser);
        } else {
            restResult = RestResult.resultOf(ErrorCode.INVALID_PARAMETER);
        }
        return restResult;
    }
}
