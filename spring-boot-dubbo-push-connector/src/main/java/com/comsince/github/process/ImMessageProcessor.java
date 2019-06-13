package com.comsince.github.process;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.*;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.configuration.PushCommonConfiguration;
import com.comsince.github.context.SpringApplicationContext;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMHandler;
import com.comsince.github.handler.im.MessagesPublisher;
import com.comsince.github.immessage.ConnectAckMessagePacket;
import com.comsince.github.model.SessionResponse;
import com.comsince.github.security.*;
import com.comsince.github.utils.*;
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
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.Executors;

import static com.comsince.github.SubSignal.*;
import static com.comsince.github.common.ErrorCode.ERROR_CODE_NOT_IMPLEMENT;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 下午2:35
 **/
public class ImMessageProcessor implements MessageProcessor{
    private static final Logger LOG = LoggerFactory.getLogger(ImMessageProcessor.class);

    private final ThreadPoolExecutorWrapper m_imBusinessExecutor;
    protected final IAuthorizator m_authorizator;
    private IAuthenticator m_authenticator;
    private boolean allowAnonymous = false;
    private final RateLimiter mLimitCounter = new RateLimiter(5, 100);
    private HashMap<String, IMHandler> m_imHandlers = new HashMap<>();

    public ImMessageProcessor() {
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        this.m_imBusinessExecutor = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "business");
        this.m_authorizator = new PermitAllAuthorizator();
        this.m_authenticator = new TokenAuthenticator();
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
        String clientId = connectMessage.getClientIdentifier();
        LOG.info("Processing CONNECT message. CId={}, username={}", clientId, connectMessage.getUserName());
        ConnectAckMessagePacket connectAckMessagePacket = new ConnectAckMessagePacket();
        if(!pushPacket.getHeader().isValid()){
             connectAckMessagePacket.setSubSignal(CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION);
             LOG.error(" protocol version is not valid. CId={}", clientId);
             Tio.send(channelContext,connectAckMessagePacket);
             Tio.close(channelContext,"protocol version is not valid");
             return;
        }

        if(clientId == null || clientId.length() == 0){
            connectAckMessagePacket.setSubSignal(CONNECTION_REFUSED_IDENTIFIER_REJECTED);
            LOG.error("The client ID cannot be empty. Username={}", connectMessage.getUserName());
            Tio.send(channelContext,connectAckMessagePacket);
            Tio.close(channelContext,"The client ID cannot be empty");
        }

        //登录验证
        if(!login(channelContext,connectMessage,clientId)){
            Tio.close(channelContext,"login fail");
            return;
        }

        if(!StringUtil.isNullOrEmpty(connectMessage.getClientIdentifier())){
            ChannelContext existChannelContext = Tio.getChannelContextByBsId(channelContext.groupContext,connectMessage.getClientIdentifier());
            if(existChannelContext != null){
                Tio.close(existChannelContext,"close exist im channel context");
            }
            Tio.bindBsId(channelContext,connectMessage.getClientIdentifier());
            channelContext.setAttribute(Constants.ATTR_USERNAME,connectMessage.getUserName());
            channelContext.setAttribute(Constants.ATTR_CLIENTID,connectMessage.getClientIdentifier());
        } else {
            Tio.close(channelContext,"非法的链接，无效的clientID");
        }

        sendAck(channelContext,connectMessage,clientId);

        if (!createOrLoadClientSession(connectMessage.getUserName(),connectMessage, clientId)) {
            ConnectAckMessagePacket badId = connAck(CONNECTION_REFUSED_SESSION_NOT_EXIST);
            Tio.send(channelContext,badId);
            Tio.close(channelContext,"session not exist");
            return;
        }

        SessionResponse session = sessionService().getSession(clientId);
        if(session != null) {
            //远程调用可用有问题，不能刷新时间
            session.refreshLastActiveTime();
        }

        LOG.info("The CONNECT message has been processed. CId={}, username={}", clientId, connectMessage.getUserName());
    }

    private boolean createOrLoadClientSession(String username, ConnectMessage msg, String clientId) {
        sessionService().loadUserSession(username, clientId);
        boolean hasSessionClient = sessionService().sessionForClient(clientId);
        if (hasSessionClient) {
            boolean updateFlag = sessionService().updateExistSession(username, clientId, false);
            LOG.info("createOrLoadClientSession flag {}",updateFlag);
            return updateFlag;
        } else {
            return false;
        }

    }

    private void sendAck(ChannelContext channelContext,ConnectMessage connectMessage,String clientId){
        ConnectAckMessagePacket okResp;
        String user = connectMessage.getUserName();
        long messageHead = messageService().getMessageHead(user);
        long friendHead = messageService().getFriendHead(user);
        long friendRqHead = messageService().getFriendRqHead(user);
        long settingHead = messageService().getSettingHead(user);
        WFCMessage.ConnectAckPayload payload = WFCMessage.ConnectAckPayload.newBuilder()
                .setMsgHead(messageHead)
                .setFriendHead(friendHead)
                .setFriendRqHead(friendRqHead)
                .setSettingHead(settingHead)
                .setServerTime(System.currentTimeMillis())
                .build();

        okResp = connAck(CONNECTION_ACCEPTED, payload.toByteArray());
        Tio.send(channelContext,okResp);
        LOG.info("The connect ACK has been sent. CId={}", clientId);
    }

    private boolean login(ChannelContext channelContext, ConnectMessage msg, final String clientId){
        if(!StringUtil.isNullOrEmpty(msg.getUserName())){
            int status = messageService().getUserStatus(msg.getUserName());
            if (status == 2) {
                failedBlocked(channelContext);
                return false;
            }
            byte[] pwd = null;
            if (msg.getPassword() != null) {
                pwd = Base64.getDecoder().decode(msg.getPassword());

                SessionResponse session = sessionService().getSession(clientId);
                if (session == null) {
                    sessionService().createNewSession(msg.getUserName(), clientId, true, false);
                    session = sessionService().getSession(clientId);
                }

                if (session != null && session.getUsername().equals(msg.getUserName())) {
                    pwd = AES.AESDecrypt(pwd, session.getSecret(), true);
                    LOG.info("decrypt pwd "+Base64.getEncoder().encodeToString(pwd));
                } else {
                    LOG.error("no session decrypt failed of client {}", clientId);
                    failedNoSession(channelContext);
                    return false;
                }

                if (pwd == null) {
                    LOG.error("Password decrypt failed of client {}", clientId);
                    failedCredentials(channelContext);
                    return false;
                }
            } else if (!this.allowAnonymous) {
                LOG.error("Client didn't supply any password and MQTT anonymous mode is disabled CId={}", clientId);
                failedCredentials(channelContext);
                return false;
            }
            if (!m_authenticator.checkValid(clientId, msg.getUserName(), pwd)) {
                LOG.error("Authenticator has rejected the credentials CId={}, username={}, password={}",
                        clientId, msg.getUserName(), pwd);
                failedCredentials(channelContext);
                return false;
            }
            channelContext.setAttribute(Constants.ATTR_USERNAME,msg.getUserName());
        } else if(!allowAnonymous){
            LOG.error("Client didn't supply any credentials and anonymous mode is disabled. CId={}", clientId);
            failedCredentials(channelContext);
            return false;
        }
        return true;
    }

    private ConnectAckMessagePacket connAck(SubSignal subSignal){
        return connAck(subSignal,null);
    }

    private ConnectAckMessagePacket connAck(SubSignal subSignal, byte[] body){
        ConnectAckMessagePacket connectAckMessagePacket = new ConnectAckMessagePacket();
        connectAckMessagePacket.setSubSignal(subSignal);
        if(body != null && body.length != 0){
            connectAckMessagePacket.setBody(body);
        }
        return connectAckMessagePacket;
    }

    private void failedBlocked(ChannelContext channelContext) {
        Tio.send(channelContext,connAck(CONNECTION_REFUSED_IDENTIFIER_REJECTED));
        LOG.info("channelID {} failed to connect, use is blocked.", channelContext.getBsId());
    }

    private void failedCredentials(ChannelContext channelContext) {
        Tio.send(channelContext,connAck(CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD));
        LOG.info("channelID {} failed to connect with bad username or password.", channelContext.getBsId());
    }

    private void failedNoSession(ChannelContext channelContext) {
        Tio.send(channelContext,connAck(CONNECTION_REFUSED_SESSION_NOT_EXIST));
        LOG.info("channelID {} failed to connect with bad username or password.", channelContext.getBsId());
    }

    private void processPublishMessage(PushPacket pushPacket,ChannelContext channelContext){
        SubSignal signal = pushPacket.getHeader().getSubSignal();
        String clientID = (String) channelContext.getAttribute(Constants.ATTR_CLIENTID);
        String fromUser = (String) channelContext.getAttribute(Constants.ATTR_USERNAME);
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

//    private void ImHandler(String clientID, String fromUser, String topic, byte[] payloadContent, IMCallback callback){
//        LOG.info("imHandler fromUser={}, topic={}", fromUser, topic);
//        if(!mLimitCounter.isGranted(clientID + fromUser + topic)) {
//            ByteBuf ackPayload = Unpooled.buffer();
//            ackPayload.ensureWritable(1).writeByte(ERROR_CODE_OVER_FREQUENCY.getCode());
//            try {
//                callback.onIMHandled(ERROR_CODE_OVER_FREQUENCY, ackPayload);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Utility.printExecption(LOG, e);
//            }
//            LOG.warn("user {} request over frequency", fromUser);
//            return;
//        }
//
//        IMCallback wrapper = (errorcode, ackPayload) -> {
//            ackPayload.resetReaderIndex();
//            byte code = ackPayload.readByte();
//            if(ackPayload.readableBytes() > 0) {
//                byte[] data = new byte[ackPayload.readableBytes()];
//                ackPayload.getBytes(1, data);
//                try {
//                    //clientID 为空的是server api请求。客户端不允许clientID为空
//                    if (!StringUtil.isNullOrEmpty(clientID)) {
//                        //在route时，使用系统根密钥。当route成功后，用户都使用用户密钥
//                        if (topic.equals(IMTopic.GetTokenTopic)) {
//                            data = AES.AESEncrypt(data, "");
//                        } else {
//                            SessionResponse session = sessionService().getSession(clientID);
//                            if (session != null && session.getUsername().equals(fromUser)) {
//                                if (data.length > 7*1024 && session.getMqttVersion() == MqttVersion.Wildfire_1) {
//                                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                                    GZIPOutputStream gzip;
//                                    try {
//                                        gzip = new GZIPOutputStream(out);
//                                        gzip.write(data);
//                                        gzip.close();
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                    data = out.toByteArray();
//                                    code = (byte)ErrorCode.ERROR_CODE_SUCCESS_GZIPED.code;
//                                }
//
//                                data = AES.AESEncrypt(data, session.getSecret());
//                            }
//                        }
//                    }
//                    ackPayload.clear();
//                    ackPayload.resetWriterIndex();
//                    ackPayload.writeByte(code);
//                    ackPayload.writeBytes(data);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Utility.printExecption(LOG, e);
//                }
//            }
//            ackPayload.resetReaderIndex();
//            try {
//                callback.onIMHandled(errorcode, ackPayload);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Utility.printExecption(LOG, e);
//            }
//        };
//
//        IMHandler handler = m_imHandlers.get(topic);
//        if (handler != null) {
//            handler.doHandler(clientID, fromUser, topic, payloadContent, wrapper, isAdmin);
//        } else {
//            LOG.error("imHandler unknown topic={}", topic);
//            ByteBuf ackPayload = Unpooled.buffer();
//            ackPayload.ensureWritable(1).writeByte(ERROR_CODE_NOT_IMPLEMENT.getCode());
//            try {
//                wrapper.onIMHandled(ERROR_CODE_NOT_IMPLEMENT, ackPayload);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Utility.printExecption(LOG, e);
//            }
//        }
//    }

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
