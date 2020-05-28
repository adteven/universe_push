package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsRecallNotifyMessage;
import org.tio.utils.json.Json;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-28 上午11:26
 **/
@Handler("RMN")
public class RecallMessageNotificationHandler extends WsImHandler<Object, FSCMessage.NotifyRecallMessage>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, Object request) {
        return new byte[0];
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, FSCMessage.NotifyRecallMessage notifyRecallMessage) {
        return Json.toJson(new WsRecallNotifyMessage(notifyRecallMessage.getFromUser(),String.valueOf(notifyRecallMessage.getId())));
    }
}
