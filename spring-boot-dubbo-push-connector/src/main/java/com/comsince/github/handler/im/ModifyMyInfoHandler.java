package com.comsince.github.handler.im;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.ModifyMyInfoRequest;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 下午3:50
 **/
@Handler(IMTopic.ModifyMyInfoTopic)
public class ModifyMyInfoHandler extends IMHandler<WFCMessage.ModifyMyInfoRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.ModifyMyInfoRequest request, ImMessageProcessor.IMCallback callback) {
        ModifyMyInfoRequest modifyMyInfoRequest = ModifyMyInfoRequest.convert2MyInfoRequest(request);
        LOG.info("modifyInfo request "+modifyMyInfoRequest);
        return messageService.modifyUserInfo(fromUser, modifyMyInfoRequest);
    }
}
