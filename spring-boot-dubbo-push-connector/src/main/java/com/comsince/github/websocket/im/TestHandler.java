package com.comsince.github.websocket.im;

import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsFriendAddRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午6:16
 **/
@Handler(IMTopic.AddFriendRequestTopic)

public class TestHandler extends WsImHandler<WsFriendAddRequest,Byte>{
    @Override
    public byte[] convert2ProtoMessage(WsFriendAddRequest request) {
        return new byte[0];
    }

//    @Override
//    public String convert2WebsocketMessage(Byte protoMessage) {
//        return null;
//    }


}
