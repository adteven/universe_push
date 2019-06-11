package com.comsince.github.service.impl;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.MessageService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.message.ConnectMessage;
import com.comsince.github.persistence.IMessagesStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectInput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.remoting.buffer.ChannelBufferOutputStream;
import org.apache.zookeeper.server.ByteBufferOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:08
 **/
@Service
public class MessageServiceImpl implements MessageService {
    Logger logger = LoggerFactory.getLogger(MessageServiceImpl.class);

    @Autowired
    IMessagesStore messagesStore;

    @Override
    public ErrorCode saveAddFriendRequest(String userId, AddFriendMessage request, long[] head) {
        logger.info("request targetUid {} reason {}",request.getTargetUid(),request.getReason());
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setTargetUid(request.getTargetUid())
                .setReason(request.getReason())
                .build();
        return messagesStore.saveAddFriendRequest(userId,addFriendRequest,head);
    }

    @Override
    public WFCMessage.User getUserInfo(String userId) {
        return messagesStore.getUserInfo(userId);
    }


}
