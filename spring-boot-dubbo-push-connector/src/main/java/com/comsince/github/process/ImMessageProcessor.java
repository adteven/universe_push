package com.comsince.github.process;

import com.comsince.github.*;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.configuration.PushCommonConfiguration;
import com.comsince.github.context.SpringApplicationContext;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMHandler;
import com.comsince.github.handler.im.MessagesPublisher;
import com.comsince.github.security.IAuthorizator;
import com.comsince.github.security.PermitAllAuthorizator;
import com.comsince.github.utils.ClassUtil;
import com.comsince.github.utils.Constants;
import com.comsince.github.utils.ThreadPoolExecutorWrapper;
import com.comsince.github.utils.Utility;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.json.Json;
import com.comsince.github.message.ConnectMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;

import static com.comsince.github.common.ErrorCode.ERROR_CODE_NOT_IMPLEMENT;


/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 下午2:35
 **/
public class ImMessageProcessor implements MessageProcessor{
    private static final Logger LOG = LoggerFactory.getLogger(ImMessageProcessor.class);
    public static final String ATTR_USERNAME = "username";
    public static final String ATTR_CLIENTID = "ClientID";
    private final ThreadPoolExecutorWrapper m_imBusinessExecutor;
    protected final IAuthorizator m_authorizator;

    private HashMap<String, IMHandler> m_imHandlers = new HashMap<>();

    public ImMessageProcessor() {
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        this.m_imBusinessExecutor = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "business");
        this.m_authorizator = new PermitAllAuthorizator();
        IMHandler.init(m_imBusinessExecutor,messageService(),new MessagesPublisher(sessionService()));
        registerAllAction();
    }

    private MessageService messageService(){
        PushCommonConfiguration pushServerConfiguration = (PushCommonConfiguration) SpringApplicationContext.getBean(Constants.PUSHSERVER_CONFIGURATION);
        return pushServerConfiguration.messageService();
    }

    private SessionService sessionService(){
        PushCommonConfiguration pushServerConfiguration = (PushCommonConfiguration) SpringApplicationContext.getBean(Constants.PUSHSERVER_CONFIGURATION);
        return pushServerConfiguration.sessionService();
    }

    @Override
    public void process(PushPacket pushPacket, ChannelContext channelContext) {
        Signal signal = pushPacket.getHeader().getSignal();
        switch (pushPacket.getHeader().getSignal()){
            case CONNECT:
                processConnectMessage(pushPacket,channelContext);
                break;
            case PUBLISH:
                processPublishMessage(pushPacket,channelContext);
                break;
            default:
                LOG.error("Unkonwn Singal:{}", signal);
                break;
        }

    }

    private void processConnectMessage(PushPacket pushPacket,ChannelContext channelContext){
        ConnectMessage connectMessage = Json.toBean(new String(pushPacket.getBody()),ConnectMessage.class);
        LOG.info("Processing CONNECT message. CId={}, username={}", connectMessage.getClientIdentifier(), connectMessage.getUserName());
        if(!StringUtil.isNullOrEmpty(connectMessage.getClientIdentifier())){
            ChannelContext existChannelContext = Tio.getChannelContextByBsId(channelContext.groupContext,connectMessage.getClientIdentifier());
            if(existChannelContext != null){
                Tio.close(existChannelContext,"close exist im channel context");
            }
            Tio.bindBsId(channelContext,connectMessage.getClientIdentifier());
            channelContext.setAttribute(ATTR_USERNAME,connectMessage.getUserName());
            channelContext.setAttribute(ATTR_CLIENTID,connectMessage.getClientIdentifier());
        } else {
            Tio.close(channelContext,"非法的链接，无效的clientID");
        }
    }

    private void processPublishMessage(PushPacket pushPacket,ChannelContext channelContext){
        SubSignal signal = pushPacket.getHeader().getSubSignal();
        String clientID = (String) channelContext.getAttribute(ATTR_CLIENTID);
        String fromUser = (String) channelContext.getAttribute(ATTR_USERNAME);
        IMCallback wrapper = new IMCallback() {
            @Override
            public void onIMHandled(ErrorCode errorCode, ByteBuf ackPayload) {
                LOG.info("handle message errorcode {}",errorCode);
            }
        };
        byte[] payloadContent = pushPacket.getBody();
        IMHandler handler = m_imHandlers.get(signal.name());
        if (handler != null) {
            handler.doHandler(clientID, fromUser, signal.name(), payloadContent, wrapper, false);
        } else {
            LOG.error("imHandler unknown topic={}", signal.name());
            ByteBuf ackPayload = Unpooled.buffer();
            ackPayload.ensureWritable(1).writeByte(ERROR_CODE_NOT_IMPLEMENT.getCode());
            try {
                wrapper.onIMHandled(ERROR_CODE_NOT_IMPLEMENT, ackPayload);
            } catch (Exception e) {
                e.printStackTrace();
                Utility.printExecption(LOG, e);
            }
        }
    }

    /**
     * 处理消息IM消息信令
     * */
    @Override
    public boolean match(PushPacket pushPacket) {
        return pushPacket.getHeader().getSignal().ordinal() > Signal.CONTACT.ordinal();
    }

    public interface IMCallback {
        void onIMHandled(ErrorCode errorCode, ByteBuf ackPayload);
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
        } catch (IOException e) {
            e.printStackTrace();
            Utility.printExecption(LOG, e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Utility.printExecption(LOG, e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


}
