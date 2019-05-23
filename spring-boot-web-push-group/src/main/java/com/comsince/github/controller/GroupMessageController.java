package com.comsince.github.controller;

import com.comsince.github.GroupContactService;
import com.comsince.github.PushService;
import com.comsince.github.model.PushResponse;
import com.comsince.github.utils.Constants;
import org.apache.dubbo.config.annotation.Reference;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-26 下午4:42
 **/
@RequestMapping(value = "message")
@RestController
public class GroupMessageController {

    Logger logger = LoggerFactory.getLogger(GroupMessageController.class);

    @Autowired
    private RedissonClient redissonClient;


    @Reference
    private GroupContactService groupContactService;

    @RequestMapping(value = "sendToGroup")
    public PushResponse sendToGroup(@RequestParam String fromToken, @RequestParam String group, @RequestParam String message){
        RList<String> tokenList = redissonClient.getList(Constants.REDIS_PREFIX + group);
        logger.info("push token list "+tokenList);
        if(tokenList != null){
            for(String sendToken : tokenList){
                if(!sendToken.equals(fromToken)){
                    groupContactService.pushByToken(sendToken,message);
                }
            }
        }
        return new PushResponse(200,"send success");
    }

    @RequestMapping(value = "sendToSingle")
    public PushResponse sendToSingle(@RequestParam String toToken, @RequestParam String group,@RequestParam String message){
        RList<String> tokenList = redissonClient.getList(Constants.REDIS_PREFIX + group);
        if(tokenList != null){
            if(!tokenList.contains(toToken)){
                return new PushResponse(3001,"dont send message out of group");
            }
            groupContactService.pushByToken(toToken,message);
        }
        return new PushResponse(200,"send success");
    }
}
