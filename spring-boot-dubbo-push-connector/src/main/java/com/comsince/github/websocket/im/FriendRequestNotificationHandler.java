package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.websocket.model.WsFriendRequestNotificationMessage;
import org.tio.utils.json.Json;

import java.nio.ByteBuffer;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-28 上午11:23
 **/
@Handler("FRN")
public class FriendRequestNotificationHandler extends WsImHandler<Object, ByteBuffer>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, Object request) {
        return new byte[0];
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, ByteBuffer byteBuffer) {
        long version = byteBuffer.getLong();
        WsFriendRequestNotificationMessage friendRequestNotificationMessage = new WsFriendRequestNotificationMessage(String.valueOf(version));
        log.info("friend request notification version {}",version);
        return Json.toJson(friendRequestNotificationMessage);
    }
}
