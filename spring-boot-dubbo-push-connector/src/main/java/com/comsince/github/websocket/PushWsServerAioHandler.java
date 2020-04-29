package com.comsince.github.websocket;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.model.*;
import com.comsince.github.websocket.model.*;
import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseEncoder;
import org.tio.utils.json.Json;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsServerEncoder;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.WsServerAioHandler;
import org.tio.websocket.server.WsServerConfig;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-24 下午2:46
 * websocket 消息解码器，在原有的基础上扩展功能以支持proto消息格式转换,时间紧迫，目前没有重构代码，忘见谅
 **/
public class PushWsServerAioHandler extends WsServerAioHandler {
    private static Logger log = LoggerFactory.getLogger(PushWsServerAioHandler.class);
    /**
     * @param wsServerConfig
     * @param wsMsgHandler
     */
    public PushWsServerAioHandler(WsServerConfig wsServerConfig, IWsMsgHandler wsMsgHandler) {
        super(wsServerConfig, wsMsgHandler);
    }

    /**
     * 由于复用的是proto相关协议，因此在发送前需要将proto协议体转为json消息体
     * */
    @Override
    public ByteBuffer encode(Packet packet, GroupContext groupContext, ChannelContext channelContext) {
        WsResponse wsResponse = null;
        try {
            wsResponse = (WsResponse) packet;
            //握手包
            if (wsResponse.isHandShake()) {
                WsSessionContext imSessionContext = (WsSessionContext) channelContext.getAttribute();
                HttpResponse handshakeResponsePacket = imSessionContext.getHandshakeResponsePacket();
                try {
                    return HttpResponseEncoder.encode(handshakeResponsePacket, groupContext, channelContext);
                } catch (UnsupportedEncodingException e) {
                    log.error(e.toString(), e);
                    return null;
                }
            }

        } catch (Exception e){
            //log.error("fore convert wsResponse error",e);
        } finally {
            if(wsResponse == null){
                PushPacket pushPacket = (PushPacket) packet;
                WebSocketProtoMessage webSocketProtoMessage = new WebSocketProtoMessage();
                webSocketProtoMessage.setSignal(pushPacket.signal().name());
                webSocketProtoMessage.setSubSignal(pushPacket.subSignal().name());
                webSocketProtoMessage.setMessageId(pushPacket.messageId());
                webSocketProtoMessage.setContent(convert2WebsocketMessage(pushPacket));
                String websocketResponse = Json.toJson(webSocketProtoMessage);
                log.info("websocket response json {}",websocketResponse);
                wsResponse = WsResponse.fromText(websocketResponse,ShowcaseServerConfig.CHARSET);
            }
        }
        ByteBuffer byteBuffer = WsServerEncoder.encode(wsResponse, groupContext, channelContext);
        return byteBuffer;
    }

    private String convert2WebsocketMessage(PushPacket pushPacket){
        String result = "";
        if(pushPacket.getBody() != null){
            if(Signal.CONNECT_ACK == pushPacket.signal()){
                if(SubSignal.CONNECTION_ACCEPTED == pushPacket.subSignal()){
                    try {
                        WFCMessage.ConnectAckPayload connectAckPayload = WFCMessage.ConnectAckPayload.parseFrom(pushPacket.getBody());
                        WsConnectAcceptedMessage connectAcceptedMessage = new WsConnectAcceptedMessage();
                        connectAcceptedMessage.setFriendHead(connectAckPayload.getFriendHead());
                        connectAcceptedMessage.setMessageHead(String.valueOf(connectAckPayload.getMsgHead()));
                        log.info("msgHead {} friendHead {}",connectAckPayload.getMsgHead(),connectAckPayload.getFriendHead());
                        result = Json.toJson(connectAcceptedMessage);
                    } catch (InvalidProtocolBufferException e) {
                        e.printStackTrace();
                    }
                }
            } else if(Signal.PUB_ACK == pushPacket.signal()){
                //pub_acK 返回的消息体中第一个字节代码成功与否
                ByteBuffer byteBuffer = ByteBuffer.wrap(pushPacket.getBody());
                byte code = byteBuffer.get();
                byte[] wfcByte = new byte[byteBuffer.remaining()];
                byteBuffer.get(wfcByte);
                if(code == 0){
                    if(SubSignal.FP ==  pushPacket.subSignal()){
                        try {
                            WFCMessage.GetFriendsResult getFriendsResult = WFCMessage.GetFriendsResult.parseFrom(wfcByte);
                            log.info("getFriendsResult {} ",getFriendsResult.getEntryCount());
                            List<FriendData> friendDataList = new ArrayList<>();
                            for(WFCMessage.Friend friend : getFriendsResult.getEntryList()){
                                FriendData friendData = new FriendData();
                                friendData.setState(friend.getState());
                                friendData.setAlias(friend.getAlias());
                                friendData.setFriendUid(friend.getUid());
                                friendData.setTimestamp(friend.getUpdateDt());
                                friendDataList.add(friendData);
                            }
                            result = Json.toJson(friendDataList);
                        } catch (Exception e){
                            log.error("parse friend result error ",e);
                        }
                    } else if(SubSignal.UPUI == pushPacket.subSignal()){
                        try {
                            WFCMessage.PullUserResult pullUserResult = WFCMessage.PullUserResult.parseFrom(wfcByte);
                            List<UserResponse> userResponseList = new ArrayList<>();
                            for(WFCMessage.UserResult userResult : pullUserResult.getResultList()){
                                WFCMessage.User user = userResult.getUser();
                                userResponseList.add(UserResponse.convertWFCUser(user));
                            }
                            result = Json.toJson(userResponseList);
                        } catch (Exception e){
                            log.error("parse user info error ",e);
                        }
                    } else if(SubSignal.GPGI == pushPacket.subSignal()){
                        try {
                            WFCMessage.PullGroupInfoResult groupInfoResult = WFCMessage.PullGroupInfoResult.parseFrom(wfcByte);
                            List<GroupInfo> groupInfos = GroupInfo.convert2GroupInfos(groupInfoResult.getInfoList());
                            result = Json.toJson(groupInfos);
                        } catch (Exception e){
                            log.error("parse group info error ",e);
                        }
                    } else if(SubSignal.GPGM == pushPacket.subSignal()){
                        try {
                            WFCMessage.PullGroupMemberResult pullGroupMemberResult = WFCMessage.PullGroupMemberResult.parseFrom(wfcByte);
                            List<GroupMember> groupMemberList = GroupMember.convertToGroupMember(pullGroupMemberResult.getMemberList());
                            result = Json.toJson(groupMemberList);
                        } catch (Exception e) {
                            log.error("parse group member error ",e);
                        }
                    } else if(SubSignal.GC == pushPacket.subSignal()){
                        result ="{\"code\":200}";
                    } else if(SubSignal.GAM == pushPacket.subSignal()) {
                        result ="{\"code\":200}";
                    } else if(SubSignal.GMI == pushPacket.subSignal()){
                        result = "{\"code\":200}";
                    } else if(SubSignal.GQ == pushPacket.subSignal()){
                        result = "{\"code\":200}";
                    } else if(SubSignal.MP == pushPacket.subSignal()){
                        try {
                            WFCMessage.PullMessageResult pullMessageResult = WFCMessage.PullMessageResult.parseFrom(wfcByte);
                            PullMessageResultResponse pullMessageResultResponse = PullMessageResultResponse.convertPullMessage(pullMessageResult);
                            WsPullMessageResponse wsPullMessageResponse = new WsPullMessageResponse(Long.toString(pullMessageResultResponse.getCurrent()),
                                    Long.toString(pullMessageResultResponse.getHead()),pullMessageResultResponse.getMessageResponseList());
                            result = Json.toJson(wsPullMessageResponse);
                        } catch (Exception e){
                            log.error("parse message error ",e);
                        }
                    } else  if(SubSignal.MS == pushPacket.subSignal()){
                        try {
                            ByteBuffer resultBuf = ByteBuffer.wrap(wfcByte);
                            long messageUid = resultBuf.getLong();
                            long timestamp = resultBuf.getLong();
                            result = Json.toJson(new WsSendMessageResponse(messageUid,timestamp));
                        } catch (Exception e){
                            log.error("parse ms message error ",e);
                        }
                    } else if(SubSignal.GQNUT == pushPacket.subSignal()){
                        try {
                            WFCMessage.GetUploadTokenResult getUploadTokenResult = WFCMessage.GetUploadTokenResult.parseFrom(wfcByte);
                            WsUploadTokenResponse wsUploadTokenResponse = new WsUploadTokenResponse();
                            wsUploadTokenResponse.setDomain(getUploadTokenResult.getDomain());
                            wsUploadTokenResponse.setServer(getUploadTokenResult.getServer());
                            wsUploadTokenResponse.setPort(getUploadTokenResult.getPort());
                            wsUploadTokenResponse.setToken(getUploadTokenResult.getToken());
                            log.info("get upload response {}",wsUploadTokenResponse);
                            result = Json.toJson(wsUploadTokenResponse);
                        } catch (Exception e){
                            log.error("parse upload token error",e);
                        }
                    } else if(SubSignal.US == pushPacket.subSignal()){
                         try {
                             WFCMessage.SearchUserResult searchUserResult = WFCMessage.SearchUserResult.parseFrom(wfcByte);
                             List<UserResponse> userResponseList = new ArrayList<>();
                             for(WFCMessage.User user : searchUserResult.getEntryList()){
                                 userResponseList.add(UserResponse.convertWFCUser(user));
                             }
                             result = Json.toJson(userResponseList);
                         } catch (Exception e){
                             log.error("parse user search result error ",e);
                         }
                    } else if(SubSignal.FAR == pushPacket.subSignal()){
                        result ="{\"code\":200}";
                    } else if(SubSignal.FRP == pushPacket.subSignal()){
                        try {
                            WFCMessage.GetFriendRequestResult getFriendRequestResult = WFCMessage.GetFriendRequestResult.parseFrom(wfcByte);
                            List<FriendRequestResponse> friendRequestResponses = new ArrayList<>();
                            for(WFCMessage.FriendRequest friendRequest : getFriendRequestResult.getEntryList()){
                                friendRequestResponses.add(FriendRequestResponse.convertFriendRequest(friendRequest));
                            }
                            result = Json.toJson(friendRequestResponses);
                        } catch (Exception e){
                            log.error("parse friend request error ",e);
                        }
                    } else if(SubSignal.FHR == pushPacket.subSignal()){
                        result = "{\"code\":200}";
                    } else if(SubSignal.MMI == pushPacket.subSignal()){
                        result = "{\"code\":200}";
                    }
                }

            } else if(Signal.PUBLISH == pushPacket.signal()){
                log.info("receiver signal publish subsignal  {}",pushPacket.subSignal());
                if(SubSignal.MN == pushPacket.subSignal()){
                    try {
                        WFCMessage.NotifyMessage notifyMessage = WFCMessage.NotifyMessage.parseFrom(pushPacket.getBody());
                        log.info("notify message messageSeq {} type {}",notifyMessage.getHead(),notifyMessage.getType());
                        result = Json.toJson(new WsNotifyMessage(Long.toString(notifyMessage.getHead()),notifyMessage.getType()));
                    } catch (Exception e){
                        log.error("parse mn message error ",e);
                    }
                } else if(SubSignal.FRN == pushPacket.subSignal()){
                    ByteBuffer byteBuffer = ByteBuffer.wrap(pushPacket.getBody());
                    long version = byteBuffer.getLong();
                    WsFriendRequestNotificationMessage friendRequestNotificationMessage = new WsFriendRequestNotificationMessage(String.valueOf(version));
                    log.info("friend request notification version {}",version);
                    result = Json.toJson(friendRequestNotificationMessage);
                } else if(SubSignal.FN == pushPacket.subSignal()){
                    ByteBuffer byteBuffer = ByteBuffer.wrap(pushPacket.getBody());
                    long version = byteBuffer.getLong();
                    WsFriendNotificationMessage wsFriendNotificationMessage = new WsFriendNotificationMessage(String.valueOf(version));
                    result = Json.toJson(wsFriendNotificationMessage);
                }
            }
        }
        return result;
    }


}
