package com.comsince.github.websocket;

import cn.wildfirechat.proto.ProtoConstants;
import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.Header;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.model.GroupMember;
import com.comsince.github.model.MessageResponse;
import com.comsince.github.process.MessageDispatcher;
import com.comsince.github.websocket.model.*;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.json.Json;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author tanyaowu
 * 2017年6月28日 下午5:32:38
 */
public class ShowcaseWsMsgHandler implements IWsMsgHandler {
	private static Logger log = LoggerFactory.getLogger(ShowcaseWsMsgHandler.class);

	public static final ShowcaseWsMsgHandler me = new ShowcaseWsMsgHandler();

	private ShowcaseWsMsgHandler() {

	}

	/**
	 * 握手时走这个方法，业务可以在这里获取cookie，request参数等
	 */
	@Override
	public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
		String clientip = request.getClientIp();
		log.info("收到来自{}的ws握手包\r\n{}", clientip, request.toString());
		return httpResponse;
	}

	/** 
	 * @param httpRequest
	 * @param httpResponse
	 * @param channelContext
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) throws Exception {
	}

	/**
	 * 字节消息（binaryType = arraybuffer）过来后会走这个方法
	 */
	@Override
	public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
		return null;
	}

	/**
	 * 当客户端发close flag时，会走这个方法
	 */
	@Override
	public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) throws Exception {
		Tio.remove(channelContext, "receive close flag");
		return null;
	}

	/*
	 * 字符消息（binaryType = blob）过来后会走这个方法
	 */
	@Override
	public Object onText(WsRequest wsRequest, String text, ChannelContext channelContext) throws Exception {
		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();
		HttpRequest httpRequest = wsSessionContext.getHandshakeRequestPacket();//获取websocket握手包
		if (log.isDebugEnabled()) {
			log.debug("握手包:{}", httpRequest);
		}

		log.info("收到client: {} ws消息:{}",channelContext.getClientNode(),text);

		if (Objects.equals("心跳内容", text)) {
			return null;
		}

		//接收到web端发送的消息，需要将其转换为二进制协议格式
        if(!StringUtil.isNullOrEmpty(text)){
        	try {
				WebSocketProtoMessage webSocketProtoMessage = Json.toBean(text, WebSocketProtoMessage.class);
				log.info("convert to websocket proto message {}",webSocketProtoMessage);
				MessageDispatcher.handleMessage(convert2PushPacket(webSocketProtoMessage),channelContext);
			} catch (Exception e){
        		log.info("invalid websocket message");
			}
		}
		//返回值是要发送给客户端的内容，一般都是返回null
		return null;
	}

	private PushPacket convert2PushPacket(WebSocketProtoMessage webSocketProtoMessage){
		PushPacket pushPacket = new PushPacket();
		Header header = new Header();
		header.setSignal(Signal.toEnum(webSocketProtoMessage.getSignal()));
		header.setSubSignal(SubSignal.toEnum(webSocketProtoMessage.getSubSignal()));
		header.setMessageId(webSocketProtoMessage.getMessageId());
		int length = webSocketProtoMessage.getContent() != null ? webSocketProtoMessage.getContent().getBytes().length : 0;
		header.setLength(length);
		pushPacket.setHeader(header);
		if(length != 0){
			pushPacket.setBody(convert2ProtoMessage(header.getSignal(),header.getSubSignal(),webSocketProtoMessage.getContent()));
		}
		return pushPacket;
	}

	private byte[] convert2ProtoMessage(Signal signal,SubSignal subSignal,String content){
		byte[] result = null;
		switch  (signal) {
			case CONNECT:
				result = content.getBytes();
				break;
			case DISCONNECT:
				DisConnectMessage disConnectMessage = Json.toBean(content,DisConnectMessage.class);
				byte[] clearSession = new byte[1];
				clearSession[0] = (byte)disConnectMessage.getClearSession();
				result = clearSession;
				break;
			case PUBLISH:
				if(subSignal == SubSignal.FP){
					WsFrindRequestMessage frindRequestMessage = Json.toBean(content, WsFrindRequestMessage.class);
					WFCMessage.Version version = WFCMessage.Version.newBuilder().setVersion(frindRequestMessage.getVersion()).build();
					result = version.toByteArray();
				} else if(subSignal == SubSignal.UPUI){
					List<String> userIds = Json.toBean(content, ArrayList.class);
					log.info("get user info userIds {}",userIds);
					WFCMessage.PullUserRequest.Builder userRequestBuilder = WFCMessage.PullUserRequest.newBuilder();
					for(String user : userIds){
						WFCMessage.UserRequest userRequest = WFCMessage.UserRequest.newBuilder().setUid(user).build();
						userRequestBuilder.addRequest(userRequest);
					}
					result = userRequestBuilder.build().toByteArray();
				} else if(subSignal == SubSignal.GPGI){
                    List<String> groupIds = Json.toBean(content,ArrayList.class);
                    log.info("get group info group ids {}",groupIds);
					WFCMessage.PullUserRequest.Builder userRequestBuilder = WFCMessage.PullUserRequest.newBuilder();
					for(String groupId : groupIds){
						WFCMessage.UserRequest userRequest = WFCMessage.UserRequest.newBuilder().setUid(groupId).build();
						userRequestBuilder.addRequest(userRequest);
					}
					result = userRequestBuilder.build().toByteArray();
				} else if(subSignal == subSignal.GPGM){
                    WsGetGroupMemberRequest getGroupMemberRequest = Json.toBean(content,WsGetGroupMemberRequest.class);
                    log.info("get group member {}",getGroupMemberRequest);
					WFCMessage.PullGroupMemberRequest.Builder groupMemberBuilder = WFCMessage.PullGroupMemberRequest.newBuilder();
					groupMemberBuilder.setTarget(getGroupMemberRequest.getGroupId());
					groupMemberBuilder.setHead(getGroupMemberRequest.getVersion());
					result = groupMemberBuilder.build().toByteArray();
				} else if(subSignal == subSignal.GC){
					WsCreateGroupRequest wsCreateGroupRequest = Json.toBean(content,WsCreateGroupRequest.class);
                    log.info("create group {}",wsCreateGroupRequest);
                    String groupId = wsCreateGroupRequest.getGroupInfo().getTarget();
                    String groupPortrait = wsCreateGroupRequest.getGroupInfo().getPortrait();
					WFCMessage.GroupInfo groupInfo = WFCMessage.GroupInfo.newBuilder()
							.setName(wsCreateGroupRequest.getGroupInfo().getName())
							.setTargetId(StringUtil.isNullOrEmpty(groupId)? "":groupId)
							.setPortrait(StringUtil.isNullOrEmpty(groupPortrait)? "":groupPortrait)
							.setType(ProtoConstants.GroupType.GroupType_Normal)
							.build();

					WFCMessage.Group.Builder groupBuilder = WFCMessage.Group.newBuilder();
					groupBuilder.setGroupInfo(groupInfo);
					if(wsCreateGroupRequest.getGroupMembers() != null){
						for(GroupMember groupMember : wsCreateGroupRequest.getGroupMembers()){
							WFCMessage.GroupMember member = WFCMessage.GroupMember.newBuilder()
									.setMemberId(groupMember.memberId)
									.setType(groupMember.type)
									.build();
							groupBuilder.addMembers(member);
						}
					}
					WFCMessage.CreateGroupRequest.Builder createGroupRequestBuilder = WFCMessage.CreateGroupRequest.newBuilder();
					createGroupRequestBuilder.setGroup(groupBuilder.build());
					if(wsCreateGroupRequest.getLines() != null){
						createGroupRequestBuilder.addAllToLine(wsCreateGroupRequest.getLines());
					}
					result = createGroupRequestBuilder.build().toByteArray();
				} else if(subSignal == SubSignal.GAM){
					WsAddGroupMemberRequest wsAddGroupMemberRequest = Json.toBean(content,WsAddGroupMemberRequest.class);
					log.info("add group member {}",wsAddGroupMemberRequest);
					WFCMessage.AddGroupMemberRequest.Builder memberRequestBuilder = WFCMessage.AddGroupMemberRequest.newBuilder();
					memberRequestBuilder.setGroupId(wsAddGroupMemberRequest.getGroupId());
					if(wsAddGroupMemberRequest.getGroupMembers() != null){
						for(GroupMember gm : wsAddGroupMemberRequest.getGroupMembers()){
							WFCMessage.GroupMember groupMember = WFCMessage.GroupMember.newBuilder()
									.setMemberId(gm.getMemberId())
									.setType(gm.type)
									.build();
							memberRequestBuilder.addAddedMember(groupMember);
						}
					}
					result = memberRequestBuilder.build().toByteArray();
				} else if(subSignal == SubSignal.GMI){
                   WsModifyGroupInfoRequest wsModifyGroupInfoRequest = Json.toBean(content,WsModifyGroupInfoRequest.class);
                   log.info("modify group info {}",wsModifyGroupInfoRequest);
                   WFCMessage.ModifyGroupInfoRequest.Builder modifyGroupInfoBuilder = WFCMessage.ModifyGroupInfoRequest.newBuilder();
                   modifyGroupInfoBuilder.setGroupId(wsModifyGroupInfoRequest.getGroupId());
                   modifyGroupInfoBuilder.setType(wsModifyGroupInfoRequest.getType());
                   modifyGroupInfoBuilder.setValue(wsModifyGroupInfoRequest.getValue());
                   result = modifyGroupInfoBuilder.build().toByteArray();
				} else if(subSignal == SubSignal.GQ){
                   WsGroupQuitRequest wsGroupQuitRequest = Json.toBean(content,WsGroupQuitRequest.class);
                   log.info("quit group info {}",wsGroupQuitRequest.getGroupId());
					WFCMessage.QuitGroupRequest.Builder builder = WFCMessage.QuitGroupRequest.newBuilder();
					builder.setGroupId(wsGroupQuitRequest.getGroupId());
					result = builder.build().toByteArray();
				} else if(subSignal == SubSignal.MP){
					WsPullMessageRequest pullMessage = Json.toBean(content, WsPullMessageRequest.class);
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
					result = pullMessageRequest.toByteArray();
				} else if(subSignal == SubSignal.MS){
					MessageResponse messageResponse = Json.toBean(content,MessageResponse.class);
					log.info("message send {}",messageResponse);
					WFCMessage.Message message = MessageResponse.convertWFCMessage(messageResponse);
					result = message.toByteArray();
				} else if(subSignal == SubSignal.GQNUT){
					WsUploadTokenRequest wsUploadTokenRequest = Json.toBean(content,WsUploadTokenRequest.class);
					log.info("upload media type {}",wsUploadTokenRequest.getMediaType());
					result = new byte[1];
					result[0] = (byte) wsUploadTokenRequest.getMediaType();
				} else if(subSignal == SubSignal.US){
                    WsUserSearchRequest wsUserSearchRequest = Json.toBean(content,WsUserSearchRequest.class);
                    log.info("user search {}",wsUserSearchRequest);
					WFCMessage.SearchUserRequest request = WFCMessage.SearchUserRequest.newBuilder()
							.setKeyword(wsUserSearchRequest.getKeyword())
							.setFuzzy(wsUserSearchRequest.getFuzzy())
							.setPage(wsUserSearchRequest.getPage())
							.build();
					result = request.toByteArray();
				} else if(subSignal == SubSignal.FAR){
					WsFriendAddRequest wsFriendAddRequest = Json.toBean(content,WsFriendAddRequest.class);
					log.info("friend add request {}",wsFriendAddRequest);
					WFCMessage.AddFriendRequest friendRequest = WFCMessage.AddFriendRequest.newBuilder()
							.setReason(wsFriendAddRequest.getReason())
							.setTargetUid(wsFriendAddRequest.getTargetUserId())
							.build();
					result = friendRequest.toByteArray();
				} else if(subSignal == SubSignal.FRP){
                    WsFriendRequestPullRequest wsFriendRequestPullRequest = Json.toBean(content,WsFriendRequestPullRequest.class);
                    log.info("friend pull request {} ",wsFriendRequestPullRequest);
                    long requestVersion = Long.parseLong(wsFriendRequestPullRequest.getVersion());
					WFCMessage.Version version = WFCMessage.Version.newBuilder().setVersion( requestVersion - 1000).build();
					result = version.toByteArray();
				} else if(subSignal == SubSignal.FHR){
					WsFriendHandleRequest wsFriendHandleRequest = Json.toBean(content,WsFriendHandleRequest.class);
					WFCMessage.HandleFriendRequest handleFriendRequest = WFCMessage.HandleFriendRequest.newBuilder()
							.setTargetUid(wsFriendHandleRequest.getTargetUid())
							.setStatus(wsFriendHandleRequest.getStatus())
							.build();
					result = handleFriendRequest.toByteArray();
				} else if(subSignal == SubSignal.MMI){
                    WsModifyMyInfoRequest wsModifyMyInfoRequest = Json.toBean(content,WsModifyMyInfoRequest.class);
                    log.info("modify myInfo {}",wsModifyMyInfoRequest);
					WFCMessage.ModifyMyInfoRequest.Builder modifyMyInfoBuilder = WFCMessage.ModifyMyInfoRequest.newBuilder();
					WFCMessage.InfoEntry infoEntry = WFCMessage.InfoEntry.newBuilder()
							.setType(wsModifyMyInfoRequest.getType())
							.setValue(wsModifyMyInfoRequest.getValue()).build();
					modifyMyInfoBuilder.addEntry(infoEntry);
					result = modifyMyInfoBuilder.build().toByteArray();
				} else if(subSignal == SubSignal.MR){
					WsRecallMessageRequest wsRecallMessageRequest = Json.toBean(content,WsRecallMessageRequest.class);
					log.info("recall messageUid {}",wsRecallMessageRequest.getMessageUid());
					WFCMessage.INT64Buf int64Buf = WFCMessage.INT64Buf.newBuilder()
							.setId(Long.parseLong(wsRecallMessageRequest.getMessageUid()))
							.build();
					result = int64Buf.toByteArray();
				}
				break;
			default:
				break;
		}
		return result;
	}

}
