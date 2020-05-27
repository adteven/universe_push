package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFrindRequestMessage;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午10:37
 **/
@Handler(IMTopic.FriendPullTopic)
public class FriendPullHandler extends WsImHandler<WsFrindRequestMessage,WFCMessage.GetFriendsResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsFrindRequestMessage request) {
        WFCMessage.Version version = WFCMessage.Version.newBuilder().setVersion(request.getVersion()).build();
        return version.toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.GetFriendsResult result) {
        return null;
    }
}
