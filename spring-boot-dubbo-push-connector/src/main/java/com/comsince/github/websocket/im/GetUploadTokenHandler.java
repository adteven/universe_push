package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsUploadTokenRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:17
 **/
@Handler(IMTopic.GetQiniuUploadTokenTopic)
public class GetUploadTokenHandler extends WsImHandler<WsUploadTokenRequest,WFCMessage.GetUploadTokenResult>{

    @Override
    public byte[] request(Signal signal, SubSignal subSignal, WsUploadTokenRequest wsUploadTokenRequest) {
        log.info("upload media type {}",wsUploadTokenRequest.getMediaType());
        byte[] result = new byte[1];
        result[0] = (byte) wsUploadTokenRequest.getMediaType();
        return result;
    }

    @Override
    public String result(Signal signal, SubSignal subSignal, WFCMessage.GetUploadTokenResult result) {
        return null;
    }
}
