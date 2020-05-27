package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;

@Handler("CONNECT")
public class ConnectHandler extends WsImHandler<String,Object>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, String content) {
        return content.getBytes();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Object result) {
        return null;
    }
}
