package com.comsince.github.websocket.im;

import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.model.MessageResponse;
import com.comsince.github.proto.FSCMessage;
import com.comsince.github.websocket.model.WsSendMessageResponse;
import org.tio.utils.json.Json;

import java.nio.ByteBuffer;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:16
 **/
@Handler(value = IMTopic.SendMessageTopic)
public class SendMessageHandler extends WsImHandler<MessageResponse,ByteBuffer>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, MessageResponse messageResponse) {
        log.info("message send {}",messageResponse);
        FSCMessage.Message message = MessageResponse.convertWFCMessage(messageResponse);
        return message.toByteArray();
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, ByteBuffer resultBuf) {
        long messageUid = resultBuf.getLong();
        long timestamp = resultBuf.getLong();
        return Json.toJson(new WsSendMessageResponse(String.valueOf(messageUid),timestamp));
    }
}
