package com.comsince.github.websocket;

import com.comsince.github.Header;
import com.comsince.github.PushPacket;
import com.comsince.github.Signal;
import com.comsince.github.SubSignal;
import com.comsince.github.process.MessageDispatcher;
import com.comsince.github.websocket.im.WsMessageHandler;
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
			pushPacket.setBody(WsMessageHandler.getInstance().handleRequest(header.getSignal(),header.getSubSignal(),webSocketProtoMessage.getContent()));
		}
		return pushPacket;
	}

}
