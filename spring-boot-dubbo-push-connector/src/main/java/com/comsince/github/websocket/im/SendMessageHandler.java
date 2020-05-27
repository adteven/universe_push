package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.MessageResponse;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:16
 **/
@Handler(value = IMTopic.SendMessageTopic)
public class SendMessageHandler extends WsImHandler<MessageResponse>{
    @Override
    public byte[] convert2ProtoMessage(MessageResponse messageResponse) {
        log.info("message send {}",messageResponse);
        WFCMessage.Message message = MessageResponse.convertWFCMessage(messageResponse);
        return message.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
