package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFriendHandleRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:23
 **/
@Handler(IMTopic.HandleFriendRequestTopic)
public class HandleFriendReuquestHandler extends WsImHandler<WsFriendHandleRequest,Byte>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsFriendHandleRequest wsFriendHandleRequest) {
        WFCMessage.HandleFriendRequest handleFriendRequest = WFCMessage.HandleFriendRequest.newBuilder()
                .setTargetUid(wsFriendHandleRequest.getTargetUid())
                .setStatus(wsFriendHandleRequest.getStatus())
                .build();
        return handleFriendRequest.toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return byteResult(result);
    }
}
