package com.comsince.github.websocket.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.handler.im.Handler;
import com.comsince.github.handler.im.IMTopic;
import com.comsince.github.websocket.model.WsPullMessageRequest;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 20-5-27 下午2:14
 **/
@Handler(value = IMTopic.PullMessageTopic)
public class PullMessageHandler extends WsImHandler<WsPullMessageRequest>{
    @Override
    public byte[] convert2ProtoMessage(WsPullMessageRequest pullMessage) {
        log.info("pull message {} sendMessageCount {} pullType {}",pullMessage.getMessageId(),pullMessage.getSendMessageCount(),pullMessage.getPullType());
        //只有通知下拉消息才需要消息Id减1,在这里做减1操作，主要时因为js对long类型精度丢失无法做加减操作
        long pullMessageId = Long.parseLong(pullMessage.getMessageId());
        if(pullMessage.getPullType() == 1){
            pullMessageId -= 1;
        } else if(pullMessage.getPullType() == 0){
            pullMessageId = pullMessageId + pullMessage.getSendMessageCount();
        }
        WFCMessage.PullMessageRequest pullMessageRequest = WFCMessage.PullMessageRequest.newBuilder()
                .setId(pullMessageId)
                .setType(pullMessage.getType())
                .build();
        return pullMessageRequest.toByteArray();
    }

    @Override
    public String convert2WebsocketMessage(PushPacket pushPacket) {
        return null;
    }
}
