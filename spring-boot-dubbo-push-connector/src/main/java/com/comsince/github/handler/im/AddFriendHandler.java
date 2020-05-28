/*
 * This file is part of the Wildfire Chat package.
 * (c) Heavyrain2012 <heavyrain.lee@gmail.com>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import com.comsince.github.SubSignal;
import com.comsince.github.common.ErrorCode;
import com.comsince.github.message.AddFriendMessage;
import com.comsince.github.model.UserResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.proto.FSCMessage;
import io.netty.buffer.ByteBuf;
import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;

@Handler(IMTopic.AddFriendRequestTopic)
public class AddFriendHandler extends GroupHandler<FSCMessage.AddFriendRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, FSCMessage.AddFriendRequest request, ImMessageProcessor.IMCallback callback) {
        AddFriendMessage addFriendMessage = new AddFriendMessage();
        addFriendMessage.setTargetUid(request.getTargetUid());
        addFriendMessage.setReason(request.getReason());
        long head = messageService.saveAddFriendRequest(fromUser, addFriendMessage);
        LOG.info("targetUid {} reason {} head {}",request.getTargetUid(),request.getReason(),head);
        UserResponse user = messageService.getUserInfo(request.getTargetUid());
        if (user != null && user.getType() == ProtoConstants.UserType.UserType_Normal) {
            publisher.publishNotification(SubSignal.FRN, request.getTargetUid(),head);
        }
        return ERROR_CODE_SUCCESS;
    }
}
