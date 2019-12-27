package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.model.UserResponse;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-14 下午4:42
 **/
@Handler(IMTopic.GetUserInfoTopic)
public class GetUserInfoHandler extends IMHandler<WFCMessage.PullUserRequest>{
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.PullUserRequest request, ImMessageProcessor.IMCallback callback) {
        WFCMessage.PullUserResult.Builder resultBuilder = WFCMessage.PullUserResult.newBuilder();

        for(WFCMessage.UserRequest userRequest: request.getRequestList()){
            UserResponse userResponse = messageService.getUserInfo(userRequest.getUid());
            LOG.info("get user info {}",userResponse);
            if(userResponse != null){
                WFCMessage.UserResult.Builder userResultBuilder = WFCMessage.UserResult.newBuilder();
                userResultBuilder.setUser(WFCMessage.User.newBuilder()
                        .setUid(userResponse.getUid())
                        .setDisplayName(userResponse.getDisplayName())
                        .setMobile(userResponse.getMobile())
                        .setName(userResponse.getName())
                        .setPortrait(userResponse.getPortrait())
                        .setCompany(userResponse.getCompany())
                        .setEmail(userResponse.getEmail())
                        .setAddress(userResponse.getAddress())
                        .build());
                userResultBuilder.setCode(ProtoConstants.UserResultCode.Success);
                resultBuilder.addResult(userResultBuilder.build());
            } else {
                WFCMessage.UserResult.Builder userResultBuilder = WFCMessage.UserResult.newBuilder();
                userResultBuilder.setUser(WFCMessage.User.newBuilder().setUid(userRequest.getUid()).build());
                resultBuilder.addResult(userResultBuilder);
            }
        }
        byte[] data = resultBuilder.build().toByteArray();
        ackPayload.ensureWritable(data.length).writeBytes(data);
        return ErrorCode.ERROR_CODE_SUCCESS;
    }
}
