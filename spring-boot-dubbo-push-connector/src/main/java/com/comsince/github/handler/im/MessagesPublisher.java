package com.comsince.github.handler.im;

import com.comsince.github.PushServer;
import com.comsince.github.SessionService;
import com.comsince.github.immessage.PublishMessagePacket;
import com.comsince.github.model.SessionResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.util.Collection;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:04
 **/
public class MessagesPublisher {

    private Logger logger = LoggerFactory.getLogger(MessagesPublisher.class);

    private SessionService sessionService;

    public MessagesPublisher(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public void publishNotification(String topic, String receiver, long head) {
        publishNotificationLocal(topic, receiver, head);
    }

    void publishNotificationLocal(String topic, String receiver, long head) {
        Collection<SessionResponse> sessions = sessionService.sessionForUser(receiver);
        for (SessionResponse targetSession : sessions) {
            ChannelContext channelContext = Tio.getChannelContextByBsId(PushServer.serverGroupContext,targetSession.clientID);
            boolean targetIsActive = !channelContext.isClosed;
            if (targetIsActive) {
                ByteBuf payload = Unpooled.buffer();
                payload.writeLong(head);
                PublishMessagePacket publishMessage = new PublishMessagePacket();
                publishMessage.setTopic(topic);
                publishMessage.setBody(payload.array());
                boolean result = Tio.send(channelContext,publishMessage);
                if (!result) {
                    logger.warn("Publish friend request failure");
                }
            }

        }
    }
}
