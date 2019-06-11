package com.comsince.github.service.impl;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.MessageService;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.ConnectMessage;
import com.comsince.github.persistence.IMessagesStore;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectInput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.remoting.buffer.ChannelBufferOutputStream;
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
    public ErrorCode saveAddFriendRequest(String userId, WFCMessage.AddFriendRequest request, long[] head) {
        logger.info("request targetUid {} reason {}",request);
        return messagesStore.saveAddFriendRequest(userId,request,head);
    }

    @Override
    public WFCMessage.User getUserInfo(String userId) {
        return messagesStore.getUserInfo(userId);
    }

    public static void main(String[] args){
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setReason("comsince 请求添加好友")
                .setTargetUid("comsince")
                .build();


        ConnectMessage connectMessage = new ConnectMessage();
        connectMessage.setUserName("comsince");

        ByteBuf byteBuffer = Unpooled.buffer();
        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                System.out.println(b);
                byteBuffer.writeInt(b);
            }
        };
        Hessian2ObjectOutput hessian2ObjectOutput = new Hessian2ObjectOutput(outputStream);
        try {
            hessian2ObjectOutput.writeObject(connectMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = byteBuffer.readableBytes();
        byte[] request = new byte[length];
        byteBuffer.readBytes(request);
        InputStream inputStream = new ByteArrayInputStream(request);

        Hessian2ObjectInput hessian2ObjectInput = new Hessian2ObjectInput(inputStream);
        try {
            ConnectMessage result = hessian2ObjectInput.readObject(ConnectMessage.class);
            System.out.println("addfriendRequest "+result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
