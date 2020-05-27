package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.websocket.model.DisConnectMessage;

@Handler("DISCONNECT")
public class DisConnectHandler extends WsImHandler<DisConnectMessage,Object>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, DisConnectMessage disConnectMessage) {
        byte[] clearSession = new byte[1];
        clearSession[0] = (byte)disConnectMessage.getClearSession();
        return clearSession;
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, Object result) {
        return null;
    }
}
