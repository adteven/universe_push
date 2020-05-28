package com.comsince.github.handler.im;

import com.comsince.github.proto.ProtoConstants;
import com.comsince.github.MessageService;
import com.comsince.github.model.MessageResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.utils.RateLimiter;
import com.comsince.github.utils.ThreadPoolExecutorWrapper;
import com.comsince.github.utils.Utility;
import com.google.gson.Gson;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import com.comsince.github.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import static com.comsince.github.common.ErrorCode.*;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-10 上午11:40
 **/
public abstract class IMHandler<T> {
    protected static final Logger LOG = LoggerFactory.getLogger(IMHandler.class);
    private Method parseDataMethod;
    private Class dataCls;
    private static ThreadPoolExecutorWrapper m_imBusinessExecutor;
    private static final RateLimiter mLimitCounter = new RateLimiter(5, 100);

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ActionMethod {
    }

    protected static String actionName;
    protected static MessageService messageService;
    protected static MessagesPublisher publisher;


    public static void init(ThreadPoolExecutorWrapper businessExecutor, MessageService msgService, MessagesPublisher messagesPublisher){
        m_imBusinessExecutor = businessExecutor;
        messageService = msgService;
        publisher = messagesPublisher;
    }

    public IMHandler() {
        try {
            if (StringUtil.isNullOrEmpty(actionName)) {
                Class cls = getClass();
                while (cls.getSuperclass() != null) {
                    for (Method method : cls.getSuperclass().getDeclaredMethods()) {
                        if (method.getAnnotation(ActionMethod.class) != null) {
                            actionName = method.getName();
                            break;
                        }
                    }
                    if (StringUtil.isNullOrEmpty(actionName)) {
                        cls = cls.getSuperclass();
                    } else {
                        break;
                    }
                }
            }


            for (Method method : getClass().getDeclaredMethods()
            ) {

                if (method.getName() == actionName && method.getParameterCount() == 6) {
                    dataCls = method.getParameterTypes()[4];
                    break;
                }
            }

            if (dataCls.getSuperclass().equals(GeneratedMessage.class)) {
                parseDataMethod = dataCls.getMethod("parseFrom", byte[].class);
            } else if (dataCls.isPrimitive()) {

            } else {

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            Utility.printExecption(LOG, e);
        }
    }

    private T getDataObject(byte[] bytes) throws IllegalAccessException, InvocationTargetException {
        if (parseDataMethod != null) {
            T object = (T) parseDataMethod.invoke(dataCls, bytes);
            return object;
        }

        if (dataCls == String.class) {
            String str = new String(bytes);
            return (T)str;
        }

        if (dataCls == Byte.class) {
            Byte b = bytes[0];
            return (T)b;
        }

        if (dataCls == Void.class) {
            return null;
        }
        return (T)(new Gson().fromJson(new String(bytes), dataCls));
    }


    public ErrorCode preAction(String clientID, String fromUser, String topic, ImMessageProcessor.IMCallback callback) {
        LOG.info("imHandler fromUser={}, clientId={}, topic={}", fromUser, clientID, topic);
        if(!mLimitCounter.isGranted(clientID + fromUser + topic)) {
            ByteBuf ackPayload = Unpooled.buffer();
            ackPayload.ensureWritable(1).writeByte(ERROR_CODE_OVER_FREQUENCY.getCode());
            try {
                callback.onIMHandled(ERROR_CODE_OVER_FREQUENCY, ackPayload);
            } catch (Exception e) {
                Utility.printExecption(LOG, e);
            }
            return ErrorCode.ERROR_CODE_OVER_FREQUENCY;
        }
        return ERROR_CODE_SUCCESS;
    }

    public void doHandler(String clientID, String fromUser, String topic, byte[] payloadContent, ImMessageProcessor.IMCallback callback, boolean isAdmin) {
        m_imBusinessExecutor.execute(() -> {
            ImMessageProcessor.IMCallback callbackWrapper = new ImMessageProcessor.IMCallback() {
                @Override
                public void onIMHandled(ErrorCode errorCode, ByteBuf ackPayload) {
                    LOG.debug("execute handler {} with result {}", this.getClass().getName(), errorCode);
                    //callback.onIMHandled(errorCode, ackPayload);
                    afterAction(clientID, fromUser, topic, callback);
                }
            };

            ErrorCode preActionCode = preAction(clientID, fromUser, topic, callbackWrapper);

            if (preActionCode == ERROR_CODE_SUCCESS) {
                ByteBuf ackPayload = Unpooled.buffer(1);
                ErrorCode errorCode = ERROR_CODE_SUCCESS;
                ackPayload.ensureWritable(1).writeByte(errorCode.getCode());

                try {
                    LOG.debug("execute handler for topic {}", topic);
                    errorCode = action(ackPayload, clientID, fromUser, isAdmin, getDataObject(payloadContent), callbackWrapper);
                } catch (IllegalAccessException e) {
                    Utility.printExecption(LOG, e);
                    errorCode = ErrorCode.ERROR_CODE_INVALID_DATA;
                } catch (InvocationTargetException e) {
                    Utility.printExecption(LOG, e);
                    errorCode = ErrorCode.ERROR_CODE_INVALID_DATA;
                } catch (Exception e) {
                    Utility.printExecption(LOG, e);
                    if (e instanceof InvalidProtocolBufferException) {
                        errorCode = ErrorCode.ERROR_CODE_INVALID_DATA;
                    } else {
                        errorCode = ErrorCode.ERROR_CODE_SERVER_ERROR;
                    }
                }

                response(ackPayload, errorCode, callback);
            } else {
                LOG.error("WsHandler {} preAction failure", this.getClass().getName());
                ByteBuf ackPayload = Unpooled.buffer(1);
                ackPayload.ensureWritable(1).writeByte(preActionCode.getCode());
                response(ackPayload, preActionCode, callback);
            }
        });
    }

    private void response(ByteBuf ackPayload, ErrorCode errorCode, ImMessageProcessor.IMCallback callback) {
        ackPayload.setByte(0, errorCode.getCode());
        try {
            callback.onIMHandled(errorCode, ackPayload);
        } catch (Exception e) {
            e.printStackTrace();
            Utility.printExecption(LOG, e);
        }
    }


    @ActionMethod
    abstract public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, T request, ImMessageProcessor.IMCallback callback)   ;


    public void afterAction(String clientID, String fromUser, String topic, ImMessageProcessor.IMCallback callback) {

    }

    protected long saveAndPublish(String username, String clientID, FSCMessage.Message message) {
        MessageResponse messageResponse = MessageResponse.convertMessageResponse(message);
        messageService.storeMessage(username, clientID, messageResponse);
        Set<String> notifyReceivers = messageService.getNotifyReceivers(username, messageResponse);
        LOG.info("notifyReceivers {}",notifyReceivers);
        int pullType = 0;
        switch (message.getConversation().getType()){
            case ProtoConstants.ConversationType.ConversationType_Private:
                pullType = ProtoConstants.PullType.Pull_Normal;
                break;
            case ProtoConstants.ConversationType.ConversationType_Group:
                pullType = ProtoConstants.PullType.Pull_Normal;
                break;
            case ProtoConstants.ConversationType.ConversationType_ChatRoom:
                pullType = ProtoConstants.PullType.Pull_ChatRoom;
                break;
            case ProtoConstants.ConversationType.ConversationType_Channel:
                break;

        }
        this.publisher.publish2Receivers(message, notifyReceivers, clientID, pullType);
        return notifyReceivers.size();
    }
}
