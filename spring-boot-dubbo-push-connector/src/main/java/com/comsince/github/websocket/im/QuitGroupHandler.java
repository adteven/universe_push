package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsGroupQuitRequest;

import static com.comsince.github.handler.im.IMTopic.QuitGroupTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:29
 **/
@Handler(value = QuitGroupTopic)
public class QuitGroupHandler extends WsImHandler<WsGroupQuitRequest,Byte>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsGroupQuitRequest wsGroupQuitRequest) {
        FSCMessage.QuitGroupRequest.Builder builder = FSCMessage.QuitGroupRequest.newBuilder();
        builder.setGroupId(wsGroupQuitRequest.getGroupId());
        return builder.build().toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Byte result) {
        return byteResult(result);
    }
}
