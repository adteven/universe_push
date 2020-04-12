package com.comsince.github.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.tio.server.ServerGroupContext;
import org.tio.websocket.server.WsServerStarter;

import java.io.IOException;

/**
 * @author tanyaowu
 * 2017年6月28日 下午5:34:04
 */
public class ShowcaseWebsocketStarter {

	private PushWsServerStarter wsServerStarter;
	public static ServerGroupContext serverGroupContext;
	private Logger logger = LoggerFactory.getLogger(ShowcaseWebsocketStarter.class);

	/**
	 *
	 * @author tanyaowu
	 */
	public ShowcaseWebsocketStarter(int port, ShowcaseWsMsgHandler wsMsgHandler,String keyStoreFile,String trustStoreFile,String password) throws Exception {
		wsServerStarter = new PushWsServerStarter(port, wsMsgHandler);

		serverGroupContext = wsServerStarter.getServerGroupContext();
		serverGroupContext.setName(ShowcaseServerConfig.PROTOCOL_NAME);
		serverGroupContext.setServerAioListener(ShowcaseServerAioListener.me);

		//设置ip监控
//		serverGroupContext.setIpStatListener(ShowcaseIpStatListener.me);
		//设置ip统计时间段
//		serverGroupContext.ipStats.addDurations(ShowcaseServerConfig.IpStatDuration.IPSTAT_DURATIONS);
		
		//设置心跳超时时间
		serverGroupContext.setHeartbeatTimeout(ShowcaseServerConfig.HEARTBEAT_TIMEOUT);
		if(!StringUtils.isEmpty(keyStoreFile) && !StringUtils.isEmpty(trustStoreFile) && !StringUtils.isEmpty(password)){
			logger.info("start enable wss {},{},{}",keyStoreFile,trustStoreFile,password);
            //如果你希望通过wss来访问，就加上下面的代码吧，不过首先你得有SSL证书（证书必须和域名相匹配，否则可能访问不了ssl）
            serverGroupContext.useSsl(keyStoreFile, trustStoreFile, password);
        }
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	public PushWsServerStarter getWsServerStarter() {
		return wsServerStarter;
	}


}
