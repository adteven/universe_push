package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsGroupDismissRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 上午11:32
 **/
@Handler(IMTopic.DismissGroupTopic)
public class DismissGroupHandler extends WsImHandler<WsGroupDismissRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsGroupDismissRequest wsGroupDismissRequest) {
        WFCMessage.DismissGroupRequest.Builder builder = WFCMessage.DismissGroupRequest.newBuilder();
        builder.setGroupId(wsGroupDismissRequest.getGroupId());
        return builder.build().toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
