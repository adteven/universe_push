package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsFriendAddRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:20
 **/
@Handler(IMTopic.AddFriendRequestTopic)
public class AddFriendHandler extends WsImHandler<WsFriendAddRequest,Byte>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsFriendAddRequest wsFriendAddRequest) {
        log.info("friend add request {}",wsFriendAddRequest);
        FSCMessage.AddFriendRequest friendRequest = FSCMessage.AddFriendRequest.newBuilder()
                .setReason(wsFriendAddRequest.getReason())
                .setTargetUid(wsFriendAddRequest.getTargetUserId())
                .build();
        return friendRequest.toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return byteResult(result);
    }
}
