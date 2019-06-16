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
import com.comsince.github.model.MessageResponse;
import com.comsince.github.process.ImMessageProcessor;
import com.comsince.github.utils.MessageShardingUtil;
import io.netty.buffer.ByteBuf;

import static com.comsince.github.common.ErrorCode.ERROR_CODE_SUCCESS;


@Handler(IMTopic.HandleFriendRequestTopic)
public class HandleFriendRequestHandler extends IMHandler<WFCMessage.HandleFriendRequest> {
    @Override
    public ErrorCode action(ByteBuf ackPayload, String clientID, String fromUser, boolean isAdmin, WFCMessage.HandleFriendRequest request, ImMessageProcessor.IMCallback callback) {
        WFCMessage.Message.Builder builder = WFCMessage.Message.newBuilder();
        builder.setFromUser(request.getTargetUid());
        long[] heads = new long[2];
        MessageResponse messageResponse = messageService.handleFriendRequest(fromUser, request.getTargetUid(),request.getStatus());



//        long messageId = MessageShardingUtil.generateId();
//        long timestamp = System.currentTimeMillis();
//        builder.setMessageId(messageId);
//        builder.setServerTimestamp(timestamp);
//        saveAndPublish(request.getTargetUid(), null, builder.build());
//
//        WFCMessage.MessageContent.Builder contentBuilder = WFCMessage.MessageContent.newBuilder().setType(90).setContent("以上是打招呼信息");
//        builder = WFCMessage.Message.newBuilder();
//        builder.setFromUser(request.getTargetUid());
//        builder.setConversation(WFCMessage.Conversation.newBuilder().setTarget(fromUser).setLine(0).setType(ProtoConstants.ConversationType.ConversationType_Private).build());
//        builder.setContent(contentBuilder);
//        timestamp = System.currentTimeMillis();
//        builder.setServerTimestamp(timestamp);
//
//        messageId = MessageShardingUtil.generateId();
//        builder.setMessageId(messageId);
//        saveAndPublish(request.getTargetUid(), null, builder.build());
//
//        contentBuilder.setContent("你们已经成为好友了，现在可以开始聊天了");
//        builder.setContent(contentBuilder);
//        messageId = MessageShardingUtil.generateId();
//        builder.setMessageId(messageId);
//        timestamp = System.currentTimeMillis();
//        builder.setServerTimestamp(timestamp);
//        saveAndPublish(request.getTargetUid(), null, builder.build());
//
//        publisher.publishNotification(IMTopic.NotifyFriendTopic, request.getTargetUid(), heads[0]);
//        publisher.publishNotification(IMTopic.NotifyFriendTopic, fromUser, heads[1]);
        return ERROR_CODE_SUCCESS;
    }
}
