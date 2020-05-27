package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsUserSearchRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:18
 **/
@Handler(IMTopic.UserSearchTopic)
public class SearchUserHandler extends WsImHandler<WsUserSearchRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsUserSearchRequest wsUserSearchRequest) {
        log.info("user search {}",wsUserSearchRequest);
        WFCMessage.SearchUserRequest request = WFCMessage.SearchUserRequest.newBuilder()
                .setKeyword(wsUserSearchRequest.getKeyword())
                .setFuzzy(wsUserSearchRequest.getFuzzy())
                .setPage(wsUserSearchRequest.getPage())
                .build();
        return request.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
