package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFrindRequestMessage;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午10:37
 **/
@Handler(IMTopic.FriendPullTopic)
public class FriendPullHandler extends WsImHandler<WsFrindRequestMessage>{
    @Override
    public byte[] convert2ProtoMessage(WsFrindRequestMessage request) {
        WFCMessage.Version version = WFCMessage.Version.newBuilder().setVersion(request.getVersion()).build();
        return version.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
