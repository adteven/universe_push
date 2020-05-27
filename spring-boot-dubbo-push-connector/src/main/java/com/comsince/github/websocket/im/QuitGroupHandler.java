package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.websocket.model.WsGroupQuitRequest;

import static com.comsince.github.handler.im.IMTopic.QuitGroupTopic;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:29
 **/
@Handler(value = QuitGroupTopic)
public class QuitGroupHandler extends WsImHandler<WsGroupQuitRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsGroupQuitRequest wsGroupQuitRequest) {
        WFCMessage.QuitGroupRequest.Builder builder = WFCMessage.QuitGroupRequest.newBuilder();
        builder.setGroupId(wsGroupQuitRequest.getGroupId());
        return builder.build().toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
