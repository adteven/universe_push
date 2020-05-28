package com.comsince.github.websocket;

import com.comsince.github.PushPacket;
import com.comsince.github.websocket.im.WsMessageHandler;
import com.comsince.github.websocket.model.*;
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

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-12-24 下午2:46
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
                webSocketProtoMessage.setContent(WsMessageHandler.getInstance().handleResult(pushPacket.signal(),pushPacket.subSignal(),pushPacket.getBody()));
                String websocketResponse = Json.toJson(webSocketProtoMessage);
                log.info("websocket response json {}",websocketResponse);
                wsResponse = WsResponse.fromText(websocketResponse,ShowcaseServerConfig.CHARSET);
            }
        }
        ByteBuffer byteBuffer = WsServerEncoder.encode(wsResponse, groupContext, channelContext);
        return byteBuffer;
    }

}
