package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsRecallMessageRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:27
 **/
@Handler(value = IMTopic.RecallMessageTopic)
public class RecallMessageHandler extends WsImHandler<WsRecallMessageRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsRecallMessageRequest wsRecallMessageRequest) {
        log.info("recall messageUid {}",wsRecallMessageRequest.getMessageUid());
        WFCMessage.INT64Buf int64Buf = WFCMessage.INT64Buf.newBuilder()
                .setId(Long.parseLong(wsRecallMessageRequest.getMessageUid()))
                .build();
        return int64Buf.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
