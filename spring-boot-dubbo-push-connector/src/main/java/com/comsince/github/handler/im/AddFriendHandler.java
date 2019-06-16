/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.SubSignal;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.UserResponse;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;
import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;

@Handler(IMTopic.AddFriendRequestTopic)
public class AddFriendHandler extends GroupHandler<WFCMessage.AddFriendRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.AddFriendRequest request, ImMessageProcessor.IMCallback callback) {
        LOG.info("targetUid {} reason {}",request.getTargetUid(),request.getReason());
        AddFriendMessage addFriendMessage = new AddFriendMessage();
        addFriendMessage.setTargetUid(request.getTargetUid());
        addFriendMessage.setReason(request.getReason());
        ErrorCode errorCode = messageService.saveAddFriendRequest(fromUser, addFriendMessage);
        if (errorCode == ERROR_CODE_SUCCESS) {
            UserResponse user = messageService.getUserInfo(request.getTargetUid());
            if (user != null && user.getType() == ProtoConstants.UserType.UserType_Normal) {
                publisher.publishNotification(SubSignal.FRN, request.getTargetUid(),0);
            }
        }
        return errorCode;
    }
}
