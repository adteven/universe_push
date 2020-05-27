package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsModifyMyInfoRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:26
 **/
@Handler(IMTopic.ModifyMyInfoTopic)
public class ModifyMyInfoHandler extends WsImHandler<WsModifyMyInfoRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsModifyMyInfoRequest wsModifyMyInfoRequest) {
        log.info("modify myInfo {}",wsModifyMyInfoRequest);
        WFCMessage.ModifyMyInfoRequest.Builder modifyMyInfoBuilder = WFCMessage.ModifyMyInfoRequest.newBuilder();
        WFCMessage.InfoEntry infoEntry = WFCMessage.InfoEntry.newBuilder()
                .setType(wsModifyMyInfoRequest.getType())
                .setValue(wsModifyMyInfoRequest.getValue()).build();
        modifyMyInfoBuilder.addEntry(infoEntry);
        return modifyMyInfoBuilder.build().toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
