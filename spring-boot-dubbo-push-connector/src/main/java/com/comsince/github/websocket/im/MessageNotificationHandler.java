package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsNotifyMessage;
import org.tio.utils.json.Json;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-28 上午11:23
 **/
@Handler("MN")
public class MessageNotificationHandler extends WsImHandler<Object, FSCMessage.NotifyMessage>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, Object request) {
        return new byte[0];
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, FSCMessage.NotifyMessage notifyMessage) {
        log.info("notify message messageSeq {} type {}",notifyMessage.getHead(),notifyMessage.getType());
        return Json.toJson(new WsNotifyMessage(Long.toString(notifyMessage.getHead()),notifyMessage.getType()));
    }
}
