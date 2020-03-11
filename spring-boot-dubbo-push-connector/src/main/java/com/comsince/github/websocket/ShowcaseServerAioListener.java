/**
 * 
 */
package com.comsince.github.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.common.WsSessionContext;
import org.tio.websocket.server.WsServerAioListener;

/**
 * @author tanyaowu
 * 用户根据情况来完成该类的实现
 */
public class ShowcaseServerAioListener extends WsServerAioListener {
	private static Logger log = LoggerFactory.getLogger(ShowcaseServerAioListener.class);

	public static final ShowcaseServerAioListener me = new ShowcaseServerAioListener();

	private ShowcaseServerAioListener() {

	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) throws Exception {
		super.onAfterConnected(channelContext, isConnected, isReconnect);
		if (log.isInfoEnabled()) {
			log.info("onAfterConnected client {}", channelContext.getClientNode());
		}

	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {
		super.onAfterSent(channelContext, packet, isSentSuccess);
		if (log.isInfoEnabled()) {
			log.info("onAfterSent client:"+channelContext.getClientNode()+" bsId "+channelContext.getBsId()+" sendSuccess "+isSentSuccess);
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) throws Exception {
		super.onBeforeClose(channelContext, throwable, remark, isRemove);
		if (log.isInfoEnabled()) {
			log.info("onBeforeClose\r\n{}", channelContext);
		}

		WsSessionContext wsSessionContext = (WsSessionContext) channelContext.getAttribute();

		if (wsSessionContext.isHandshaked()) {
			
//			int count = Tio.getAllChannelContexts(channelContext.groupContext).getObj().size();
//
//			String msg = channelContext.getClientNode().toString() + " 离开了，现在共有【" + count + "】人在线";
//			//用tio-websocket，服务器发送到客户端的Packet都是WsResponse
//			WsResponse wsResponse = WsResponse.fromText(msg, ShowcaseServerConfig.CHARSET);
//			//群发
//			Tio.sendToGroup(channelContext.groupContext, Const.GROUP_ID, wsResponse);
		}
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
		log.info("onAfterHandled client {} message {}", channelContext.getClientNode(),packet.logstr());
	}

}
