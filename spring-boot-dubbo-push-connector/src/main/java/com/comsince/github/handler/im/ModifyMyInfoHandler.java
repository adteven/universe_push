package com.comsince.github.handler.im;

import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.ModifyMyInfoRequest;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-27 下午3:50
 **/
@Handler(IMTopic.ModifyMyInfoTopic)
public class ModifyMyInfoHandler extends IMHandler<FSCMessage.ModifyMyInfoRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.ModifyMyInfoRequest request, ImMessageProcessor.IMCallback callback) {
        ModifyMyInfoRequest modifyMyInfoRequest = ModifyMyInfoRequest.convert2MyInfoRequest(request);
        LOG.info("modifyInfo request "+modifyMyInfoRequest);
        return messageService.modifyUserInfo(fromUser, modifyMyInfoRequest);
    }
}
