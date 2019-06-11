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
import com.comsince.github.common.ErrorCode;
import com.comsince.github.process.ImMessageProcessor;
import io.netty.buffer.ByteBuf;
import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;
import static com.comsince.github.handler.im.IMTopic.HandleFriendRequestTopic;


@Handler(IMTopic.AddFriendRequestTopic)
public class AddFriendHandler extends GroupHandler<WFCMessage.AddFriendRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.AddFriendRequest request, ImMessageProcessor.IMCallback callback) {
            long[] head = new long[1];
            ErrorCode errorCode = messageService.saveAddFriendRequest(fromUser, request, head);
            if (errorCode == ERROR_CODE_SUCCESS) {
                WFCMessage.User user = messageService.getUserInfo(request.getTargetUid());
                if (user != null && user.getType() == ProtoConstants.UserType.UserType_Normal) {
                    publisher.publishNotification(IMTopic.NotifyFriendRequestTopic, request.getTargetUid(), head[0]);
                }
//                else if(user != null && user.getType() == ProtoConstants.UserType.UserType_Robot) {
//                    WFCMessage.HandleFriendRequest handleFriendRequest = WFCMessage.HandleFriendRequest.newBuilder().setTargetUid(fromUser).setStatus(ProtoConstants.FriendRequestStatus.RequestStatus_Accepted).build();
//                    mServer.internalRpcMsg(request.getTargetUid(), null, handleFriendRequest.toByteArray(), 0, fromUser, HandleFriendRequestTopic, false);
//                }
            }
            return errorCode;
    }
}
