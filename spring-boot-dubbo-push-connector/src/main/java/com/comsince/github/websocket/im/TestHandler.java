package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFriendAddRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午6:16
 **/
public class TestHandler extends WsImHandler<WsFriendAddRequest,WFCMessage.PullUserRequest>{


    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsFriendAddRequest request) {
        return new byte[0];
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.PullUserRequest result) {
        return null;
    }

}
