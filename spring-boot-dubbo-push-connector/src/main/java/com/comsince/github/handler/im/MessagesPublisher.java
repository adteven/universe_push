package com.comsince.github.handler.im;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.MessageService;
import com.comsince.github.PushServer;
import com.comsince.github.SessionService;
import com.comsince.github.SubSignal;
import com.comsince.github.immessage.PublishAckMessagePacket;
import com.comsince.github.immessage.PublishMessagePacket;
import com.comsince.github.model.*;
import com.comsince.github.websocket.ShowcaseWebsocketStarter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cn.wildfirechat.proto.ProtoConstants.PersistFlag.Transparent;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 上午11:04
 **/
public class MessagesPublisher {

    private Logger LOG = LoggerFactory.getLogger(MessagesPublisher.class);

    private SessionService sessionService;
    private MessageService messageService;

    public MessagesPublisher(SessionService sessionService, MessageService messageService) {
        this.sessionService = sessionService;
        this.messageService = messageService;
    }

    public void publishRecall2Receivers(long messageUid, String operatorId, Set<String> receivers, String exceptClientId) {
        publishRecall2ReceiversLocal(messageUid, operatorId, receivers, exceptClientId);
    }

    public void publishRecall2ReceiversLocal(long messageUid, String operatorId, Collection<String> receivers, String exceptClientId) {
        for (String user : receivers) {
            Collection<SessionResponse> sessions = sessionService.sessionForUser(user);
            for (SessionResponse targetSession : sessions) {
                if (exceptClientId != null && exceptClientId.equals(targetSession.clientID)) {
                    continue;
                }

                if (targetSession.getClientID() == null) {
                    continue;
                }

                LOG.info("send recall messageUid {} to receiver {} clientId {}",messageUid,user,targetSession.clientID);
                WFCMessage.NotifyRecallMessage notifyMessage = WFCMessage.NotifyRecallMessage
                        .newBuilder()
                        .setFromUser(operatorId)
                        .setId(messageUid)
                        .build();
                PublishMessagePacket publishMessage = new PublishMessagePacket();
                publishMessage.setSubSignal(SubSignal.RMN);
                publishMessage.setBody(notifyMessage.toByteArray());
                Tio.sendToBsId(PushServer.serverGroupContext,targetSession.clientID,publishMessage);
            }
        }
    }

    private void publishTransparentMessage2Receivers(long messageHead, Collection<String> receivers, int pullType) {
        MessageResponse messageResponse = messageService.getMessage(messageHead);
        if (messageResponse != null) {
            for (String user : receivers) {
                Collection<SessionResponse> sessions = sessionService.sessionForUser(user);

                for (SessionResponse targetSession : sessions) {
                    if (targetSession.getClientID() == null) {
                        continue;
                    }
                    LOG.info("send transparent message {} contentType {} to receiver {} clientId {}",messageHead,messageResponse.getContent().getType(),user,targetSession.clientID);
                    WFCMessage.Message wfcMessage = MessageResponse.convertWFCMessage(messageResponse);
                    WFCMessage.PullMessageResult pullMessageResult = WFCMessage.PullMessageResult.newBuilder()
                            .addMessage(wfcMessage)
                            .setCurrent(0)
                            .setHead(0)
                            .build();
                    //按照消息ack消息格式组装，第一个字节为错误码
                    byte[] pullMessageByteArr = pullMessageResult.toByteArray();
                    ByteBuf byteBuf = Unpooled.buffer();
                    byteBuf.ensureWritable(1).writeByte(0);
                    byteBuf.ensureWritable(pullMessageByteArr.length).writeBytes(pullMessageByteArr);
                    byte[] bodyMessage = new byte[byteBuf.readableBytes()];
                    byteBuf.readBytes(bodyMessage);
                    PublishAckMessagePacket publishAckMessagePacket = new PublishAckMessagePacket();
                    publishAckMessagePacket.setSubSignal(SubSignal.MP);
                    publishAckMessagePacket.setBody(bodyMessage);
                    Tio.sendToBsId(PushServer.serverGroupContext,targetSession.clientID,publishAckMessagePacket);

                    LOG.info("send transparent message to websocket clientId {}",targetSession.getClientID());
                    //这里要通知websocket客户端，因此需要发送到websocket消息
                    Tio.sendToBsId(ShowcaseWebsocketStarter.serverGroupContext,targetSession.getClientID(),publishAckMessagePacket);
                }
            }
        }
    }

    public void publishNotification(SubSignal subSignal, String receiver, long body) {
        publishNotificationLocal(subSignal, receiver,body);
    }

    void publishNotificationLocal(SubSignal subSignal, String receiver,long body) {
        Collection<SessionResponse> sessions = sessionService.sessionForUser(receiver);
        for (SessionResponse targetSession : sessions) {
            PublishMessagePacket publishMessage = new PublishMessagePacket();
            publishMessage.setSubSignal(subSignal);
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.putLong(body);
            publishMessage.setBody(byteBuffer.array());
            Tio.sendToBsId(PushServer.serverGroupContext,targetSession.clientID,publishMessage);

            //send publish notification to websocket client
            Tio.sendToBsId(ShowcaseWebsocketStarter.serverGroupContext,targetSession.getClientID(),publishMessage);
        }
    }

    public void publish2Receivers(WFCMessage.Message message, Set<String> receivers, String exceptClientId, int pullType) {
        long messageId = message.getMessageId();
        String pushContent = message.getContent().getPushContent();
        if (StringUtil.isNullOrEmpty(pushContent)) {
            int type = message.getContent().getType();
            if (type == ProtoConstants.ContentType.Image) {
                pushContent = "[图片]";
            } else if(type == ProtoConstants.ContentType.Location) {
                pushContent = "[位置]";
            } else if(type == ProtoConstants.ContentType.Text) {
                pushContent = message.getContent().getSearchableContent();
            } else if(type == ProtoConstants.ContentType.Voice) {
                pushContent = "[语音]";
            } else if(type == ProtoConstants.ContentType.Video) {
                pushContent = "[视频]";
            } else if(type == ProtoConstants.ContentType.RichMedia) {
                pushContent = "[图文]";
            } else if(type == ProtoConstants.ContentType.File) {
                pushContent = "[文件]";
            } else if(type == ProtoConstants.ContentType.Sticker) {
                pushContent = "[表情]";
            }
        }

        if (message.getContent().getPersistFlag() == Transparent) {
            pushContent = null;
        }

        publish2Receivers(message.getFromUser(),
                message.getConversation().getType(),
                message.getConversation().getTarget(),
                message.getConversation().getLine(),
                messageId,
                receivers,
                pushContent, exceptClientId, pullType,
                message.getContent().getType(),
                message.getServerTimestamp(),
                message.getContent().getMentionedType(),
                message.getContent().getMentionedTargetList(),
                message.getContent().getPersistFlag());

    }

    private void publish2Receivers(String sender, int conversationType, String target, int line, long messageHead, Collection<String> receivers, String pushContent, String exceptClientId, int pullType, int messageContentType, long serverTime, int mentionType, List<String> mentionTargets, int persistFlag) {
        if (persistFlag == Transparent) {
            publishTransparentMessage2Receivers(messageHead, receivers, pullType);
            return;
        }
        for (String user : receivers) {
            long messageSeq;
            if (pullType != ProtoConstants.PullType.Pull_ChatRoom) {
                messageSeq = messageService.insertUserMessages(sender, conversationType, target, line, messageContentType, user, messageHead);
            } else {
                messageSeq = messageService.insertChatroomMessages(user, line, messageHead);
            }

            Collection<SessionResponse> sessions = sessionService.sessionForUser(user);
            LOG.info("current user {} sessions {}",user,sessions);

            Collection<String> targetClients = null;
            if (pullType == ProtoConstants.PullType.Pull_ChatRoom) {
                targetClients = messageService.getChatroomMemberClient(user);
            }
            for (SessionResponse targetSession : sessions) {
                //超过7天不活跃的用户忽略 暂时不校验session 过期
//                if(System.currentTimeMillis() - targetSession.getUpdateDt() > 7 * 24 * 60 * 60 * 1000) {
//                    continue;
//                }

                if (exceptClientId != null && exceptClientId.equals(targetSession.clientID)) {
                    continue;
                }

                if (targetSession.getClientID() == null) {
                    continue;
                }

                if (pullType == ProtoConstants.PullType.Pull_ChatRoom && !targetClients.contains(targetSession.getClientID())) {
                    continue;
                }

                if (pullType == ProtoConstants.PullType.Pull_ChatRoom) {
                    if (exceptClientId != null && exceptClientId.equals(targetSession.getClientID())) {
                        targetSession.refreshLastChatroomActiveTime();
                    }

                    if(System.currentTimeMillis() - targetSession.getLastChatroomActiveTime() > 5*60*1000) {
                        messageService.handleQuitChatroom(user, targetSession.getClientID(), target);
                        continue;
                    }
                }

                boolean isSlient;
                if (pullType == ProtoConstants.PullType.Pull_ChatRoom) {
                    isSlient = true;
                } else {
                    isSlient = false;

                    if (!user.equals(sender)) {
                        ConversationResult conversation = new ConversationResult();
                        if (conversationType == ProtoConstants.ConversationType.ConversationType_Private){
                            conversation.setType(conversationType);
                            conversation.setLine(line);
                            conversation.setTarget(sender);
                        } else {
                            conversation.setType(conversationType);
                            conversation.setLine(line);
                            conversation.setTarget(target);
                        }

                        if (messageService.getUserConversationSlient(user, conversation)) {
                            LOG.info("The conversation {}-{}-{} is slient", conversation.getType(), conversation.getTarget(), conversation.getLine());
                            isSlient = true;
                        }

                        if (messageService.getUserGlobalSlient(user)) {
                            LOG.info("The user {} is global sliented", user);
                            isSlient = true;
                        }
                    }

                    if (!StringUtil.isNullOrEmpty(pushContent) || messageContentType == 400 || messageContentType == 402) {
                        if (!isSlient) {
                            targetSession.setUnReceivedMsgs(targetSession.getUnReceivedMsgs() + 1);
                        }
                    }

                    if (isSlient) {
                        if (mentionType == 2 || (mentionType == 1 && mentionTargets.contains(user))) {
                            isSlient = false;
                        }
                    }
                }

                LOG.info("WsNotifyMessage pullType {}  messageSeq {}",pullType,messageSeq);

                WFCMessage.NotifyMessage notifyMessage = WFCMessage.NotifyMessage
                        .newBuilder()
                        .setType(pullType)
                        .setHead(messageSeq)
                        .build();

                byte[] byteData = notifyMessage.toByteArray();
                PublishMessagePacket publishMsg;
                publishMsg = new PublishMessagePacket();
                publishMsg.setBody(byteData);
                publishMsg.setSubSignal(SubSignal.MN);

                LOG.info("send to clientId {}",targetSession.getClientID());

                Tio.sendToBsId(PushServer.serverGroupContext,targetSession.getClientID(),publishMsg);

                LOG.info("send to websocket clientId {}",targetSession.getClientID());
                //这里要通知websocket客户端，因此需要发送到websocket消息
                Tio.sendToBsId(ShowcaseWebsocketStarter.serverGroupContext,targetSession.getClientID(),publishMsg);
            }
        }
    }
}
