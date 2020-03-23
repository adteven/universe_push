/**
 * 
 */
package com.comsince.github.websocket;

import com.comsince.github.SessionService;
import com.comsince.github.configuration.PushCommonConfiguration;
import com.comsince.github.context.SpringApplicationContext;
import com.comsince.github.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
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
			log.info("onBeforeClose {}", channelContext);
		}
		if(StringUtils.isNotBlank(channelContext.getBsId())){
			sessionService().cleanSession(channelContext.getBsId());
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

	private SessionService sessionService(){
		PushCommonConfiguration pushServerConfiguration = (PushCommonConfiguration) SpringApplicationContext.getBean(Constants.PUSHSERVER_CONFIGURATION);
		return pushServerConfiguration.sessionService();
	}

}
