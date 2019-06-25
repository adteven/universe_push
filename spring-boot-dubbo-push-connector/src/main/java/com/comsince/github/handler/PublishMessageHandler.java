package com.comsince.github.handler;

import com.comsince.github.*;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMHandler;
import com.comsince.github.handler.im.MessagesPublisher;
import com.comsince.github.immessage.PublishAckMessagePacket;
import com.comsince.github.immessage.PublishMessagePacket;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.utils.ClassUtil;
import com.comsince.github.utils.Constants;
import com.comsince.github.utils.ThreadPoolExecutorWrapper;
import com.comsince.github.utils.Utility;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.util.HashMap;
import java.util.concurrent.Executors;

import static com.comsince.github.common.ErrorCode.ERROR_CODE_NOT_IMPLEMENT;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-13 下午5:11
 **/
public class PublishMessageHandler {

    Logger LOG = LoggerFactory.getLogger(PublishMessageHandler.class);

    private HashMap<String, IMHandler> m_imHandlers = new HashMap<>();
    private final ThreadPoolExecutorWrapper m_imBusinessExecutor;


    public PublishMessageHandler(MessageService messageService, SessionService sessionService){
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        this.m_imBusinessExecutor = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "business");
        IMHandler.init(m_imBusinessExecutor,messageService,new MessagesPublisher(sessionService,messageService));
        registerAllAction();

    }


    public void receivePublishMessage(PushPacket pushPacket, ChannelContext channelContext){
        SubSignal subSignal = pushPacket.getHeader().getSubSignal();
        String clientID = (String) channelContext.getAttribute(Constants.ATTR_CLIENTID);
        String fromUser = (String) channelContext.getAttribute(Constants.ATTR_USERNAME);
        int messageID = pushPacket.getHeader().getMessageId();
        ImMessageProcessor.IMCallback wrapper = new ImMessageProcessor.IMCallback() {
            @Override
            public void onIMHandled(ErrorCode errorCode, ByteBuf ackPayload) {
                LOG.info("handle message errorcode {}",errorCode);
                sendPubAck(clientID, messageID, ackPayload,subSignal);
            }
        };
        byte[] payloadContent = pushPacket.getBody();
        IMHandler handler = m_imHandlers.get(subSignal.name());
        if (handler != null) {
            handler.doHandler(clientID, fromUser, subSignal.name(), payloadContent, wrapper, false);
        } else {
            LOG.error("imHandler unknown topic={}", subSignal.name());
            ByteBuf ackPayload = Unpooled.buffer();
            ackPayload.ensureWritable(1).writeByte(ERROR_CODE_NOT_IMPLEMENT.getCode());
            try {
                wrapper.onIMHandled(ERROR_CODE_NOT_IMPLEMENT, ackPayload);
            } catch (Exception e) {
                Utility.printExecption(LOG, e);
            }
        }
    }

    /**
     * puback 消息格式
     * 第一个自己消息错误码，之后跟着消息体
     * */
    private void sendPubAck(String clientId, int messageID, ByteBuf payload, SubSignal subSignal){
        LOG.info("clientId {} messagId {} send PUB_ACK message size {} subSignal {}",clientId,messageID,payload.readableBytes(),subSignal);
        PublishAckMessagePacket publishAckMessagePacket = new PublishAckMessagePacket();
        publishAckMessagePacket.setMessageId(messageID);
        publishAckMessagePacket.setSubSignal(subSignal);
        byte[] messageByte = new byte[payload.readableBytes()];
        payload.readBytes(messageByte);
        publishAckMessagePacket.setBody(messageByte);
        boolean flag = Tio.sendToBsId(PushServer.serverGroupContext,clientId,publishAckMessagePacket);
        LOG.info("send client {} message size {} {}",clientId,messageByte.length,flag ? "sucess":"fail");
    }


    private void registerAllAction() {
        try {
            for (Class cls : ClassUtil.getAllAssignedClass(IMHandler.class)) {
                Handler annotation = (Handler)cls.getAnnotation(Handler.class);
                if(annotation != null) {
                    IMHandler handler = (IMHandler) cls.newInstance();
                    m_imHandlers.put(annotation.value(), handler);
                }
            }
        } catch (Exception e) {
            Utility.printExecption(LOG, e);
        }
    }
}
