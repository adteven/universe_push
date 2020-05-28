package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.websocket.model.WsConnectAcceptedMessage;
import org.tio.utils.json.Json;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-28 上午10:00
 **/
@Handler("CONNECT_ACK")
public class ConnectAckHandler extends WsImHandler<Object, WFCMessage.ConnectAckPayload>{
    @Override
    public byte[] request(Signal signal, SubSignal subSignal, Object request) {
        return new byte[0];
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.ConnectAckPayload connectAckPayload) {
        String result = "";
        if(SubSignal.CONNECTION_ACCEPTED == subSignal){
            WsConnectAcceptedMessage connectAcceptedMessage = new WsConnectAcceptedMessage();
            connectAcceptedMessage.setFriendHead(connectAckPayload.getFriendHead());
            connectAcceptedMessage.setMessageHead(String.valueOf(connectAckPayload.getMsgHead()));
            log.info("msgHead {} friendHead {}",connectAckPayload.getMsgHead(),connectAckPayload.getFriendHead());
            result = Json.toJson(connectAcceptedMessage);
        }
        return result;
    }
}
