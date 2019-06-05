package com.comsince.github.service.impl;

import com.alibaba.fastjson.JSONException;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.configuration.IMConfig;
import com.comsince.github.configuration.SMSConfig;
import com.comsince.github.controller.im.model.Record;
import com.comsince.github.controller.im.model.RestResult;
import com.comsince.github.controller.im.pojo.*;
import com.comsince.github.service.AdminService;
import com.comsince.github.service.LoginService;
import com.comsince.github.util.Utils;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 上午9:46
 **/
@Service
public class LoginServiceImpl implements LoginService {
    private static final Logger LOG = LoggerFactory.getLogger(LoginServiceImpl.class);

    private static ConcurrentHashMap<String, Record> mRecords = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Count> mCounts = new ConcurrentHashMap<>();

    static class Count {
        long count;
        long startTime;
        void reset() {
            count = 1;
            startTime = System.currentTimeMillis();
        }

        boolean increaseAndCheck() {
            long now = System.currentTimeMillis();
            if (now - startTime > 86400000) {
                reset();
                return true;
            }
            count++;
            if (count > 10) {
                return false;
            }
            return true;
        }
    }

    @Autowired
    private SMSConfig mSMSConfig;

    @Autowired
    private IMConfig mIMConfig;

    @Autowired
    private AdminService adminService;

    @Override
    public RestResult sendCode(String mobile) {
        try {
            if (!Utils.isMobile(mobile)) {
                LOG.error("Not valid mobile {}", mobile);
                return RestResult.error(RestResult.RestCode.ERROR_INVALID_MOBILE);
            }

            Record record = mRecords.get(mobile);
            if (record != null && System.currentTimeMillis() - record.getTimestamp() < 60 * 1000) {
                LOG.error("Send code over frequency. timestamp {}, now {}", record.getTimestamp(), System.currentTimeMillis());
                return RestResult.error(RestResult.RestCode.ERROR_SEND_SMS_OVER_FREQUENCY);
            }
            Count count = mCounts.get(mobile);
            if (count == null) {
                count = new Count();
                mCounts.put(mobile, count);
            }

            if (!count.increaseAndCheck()) {
                LOG.error("Count check failure, already send {} messages today", count.count);
                return RestResult.error(RestResult.RestCode.ERROR_SEND_SMS_OVER_FREQUENCY);
            }

            String code = Utils.getRandomCode(4);
            String[] params = {code};
            SmsSingleSender ssender = new SmsSingleSender(mSMSConfig.getAppid(), mSMSConfig.getAppkey());
            SmsSingleSenderResult result = ssender.sendWithParam("86", mobile,
                    mSMSConfig.getTemplateId(), params, null, "", "");
            if (result.result == 0) {
                mRecords.put(mobile, new Record(code, mobile));
                return RestResult.ok(null);
            } else {
                LOG.error("Failure to send SMS {}", result);
                return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
            }
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
        return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
    }

    @Override
    public RestResult login(String mobile, String code, String clientId) {
        if (("13900000000".equals(mobile) || "13900000001".equals(mobile)) && code.equals("556677")) {
            LOG.info("is test account");
        } else if (StringUtils.isEmpty(mSMSConfig.getSuperCode()) || !code.equals(mSMSConfig.getSuperCode())) {
            Record record = mRecords.get(mobile);
            if (record == null || !record.getCode().equals(code)) {
                LOG.error("not empty or not correct");
                return RestResult.error(RestResult.RestCode.ERROR_CODE_INCORRECT);
            }
            if (System.currentTimeMillis() - record.getTimestamp() > 5 * 60 * 1000) {
                LOG.error("Code expired. timestamp {}, now {}", record.getTimestamp(), System.currentTimeMillis());
                return RestResult.error(RestResult.RestCode.ERROR_CODE_EXPIRED);
            }
        }

        try {
            //使用电话号码查询用户信息。
            //如果用户信息不存在，创建用户
            InputOutputUserInfo user = adminService.getUserInfo(new InputGetUserInfo(null,null,mobile));
            boolean isNewUser = false;
            if (user == null) {
                LOG.info("User not exist, try to create");
                user = new InputOutputUserInfo();
                user.setName(mobile);
                user.setDisplayName(mobile);
                user.setMobile(mobile);
                OutputCreateUser userIdResult = adminService.createUser(user);
                if (userIdResult != null) {
                    user.setUserId(userIdResult.getUserId());
                    isNewUser = true;
                } else {
                    LOG.info("Create user failure");
                    return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
                }
            }

            //使用用户id获取token
            OutputGetIMTokenData tokenResult = adminService.getUserToken(new InputGetToken(user.getUserId(), clientId));
            if (tokenResult != null) {
                LOG.error("Get user failure ");
                return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
            }

            //返回用户id，token和是否新建
            LoginResponse response = new LoginResponse();
            response.setUserId(user.getUserId());
            response.setToken(tokenResult.getToken());
            response.setRegister(isNewUser);
            return RestResult.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception happens {}", e);
            return RestResult.error(RestResult.RestCode.ERROR_SERVER_ERROR);
        }
    }
}
