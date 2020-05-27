package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFriendAddRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:20
 **/
public class AddFriendHandler extends WsImHandler<WsFriendAddRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsFriendAddRequest wsFriendAddRequest) {
        log.info("friend add request {}",wsFriendAddRequest);
        WFCMessage.AddFriendRequest friendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setReason(wsFriendAddRequest.getReason())
                .setTargetUid(wsFriendAddRequest.getTargetUserId())
                .build();
        return friendRequest.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
