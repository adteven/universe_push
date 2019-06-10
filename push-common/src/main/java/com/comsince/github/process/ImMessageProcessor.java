package com.comsince.github.process;

import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMHandler;
import com.comsince.github.security.PermitAllAuthorizator;
import com.comsince.github.utils.ClassUtil;
import com.comsince.github.utils.RateLimiter;
import com.comsince.github.utils.ThreadPoolExecutorWrapper;
import com.comsince.github.utils.Utility;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;

import com.comsince.github.security.IAuthorizator;
import org.tio.core.ChannelContext;

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
    private final RateLimiter mLimitCounter = new RateLimiter(5, 100);

    private HashMap<String, IMHandler> m_imHandlers = new HashMap<>();

    public ImMessageProcessor() {
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        this.m_imBusinessExecutor = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "business");
        this.m_authorizator = new PermitAllAuthorizator();
        IMHandler.init(m_imBusinessExecutor);
        registerAllAction();
    }

    @Override
    public void process(PushPacket pushPacket, ChannelContext channelContext) {
        Signal signal = pushPacket.getHeader().getSignal();
        String clientID = null;
        String fromUser = null;
        IMCallback wrapper = null;
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
