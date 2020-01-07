package com.comsince.github.websocket;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.Header;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.model.MessageResponse;
import com.comsince.github.process.MessageDispatcher;
import com.comsince.github.websocket.model.DisConnectMessage;
import com.comsince.github.websocket.model.WsFrindRequestMessage;
import com.comsince.github.websocket.model.WsPullMessageRequest;
import com.comsince.github.websocket.model.WebSocketProtoMessage;
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
		//绑定到群组，后面会有群发
//		Tio.bindGroup(channelContext, Const.GROUP_ID);
//		int count = Tio.getAllChannelContexts(channelContext.groupContext).getObj().size();
//
//		String msg = channelContext.getClientNode().toString() + " 进来了，现在共有【" + count + "】人在线";
//		//用tio-websocket，服务器发送到客户端的Packet都是WsResponse
//		WsResponse wsResponse = WsResponse.fromText(msg, ShowcaseServerConfig.CHARSET);
//		//群发
//		Tio.sendToGroup(channelContext.groupContext, Const.GROUP_ID, wsResponse);
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

		log.info("收到ws消息:{}", text);

		if (Objects.equals("心跳内容", text)) {
			return null;
		}

		//接收到web端发送的消息，需要将其转换为二进制协议格式
        if(!StringUtil.isNullOrEmpty(text)){
			WebSocketProtoMessage webSocketProtoMessage = Json.toBean(text, WebSocketProtoMessage.class);
			log.info("convert to websocket proto message {}",webSocketProtoMessage);
			MessageDispatcher.handleMessage(convert2PushPacket(webSocketProtoMessage),channelContext);
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
				}else if(subSignal == SubSignal.MP){
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
				}
				break;
			default:
				break;
		}
		return result;
	}

}
