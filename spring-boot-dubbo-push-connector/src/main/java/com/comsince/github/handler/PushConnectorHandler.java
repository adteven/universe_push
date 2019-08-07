package com.comsince.github.handler;

import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.process.MessageDispatcher;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午9:53
 **/
public class PushConnectorHandler extends PushMessageHandler{
    @Override
    public void handler(Packet packet, ChannelContext channelContext) throws Exception {
        PushPacket pushPacket = (PushPacket) packet;
        Signal signal = pushPacket.getHeader().getSignal();
        logger.info("handle signal :" + signal.name());
        MessageDispatcher.handleMessage(pushPacket,channelContext);
    }
}
