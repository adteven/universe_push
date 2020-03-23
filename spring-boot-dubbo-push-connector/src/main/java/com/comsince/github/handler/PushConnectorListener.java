package com.comsince.github.handler;

import com.comsince.github.SessionService;
import com.comsince.github.configuration.PushCommonConfiguration;
import com.comsince.github.context.SpringApplicationContext;
import com.comsince.github.utils.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-14 上午10:26
 **/
public class PushConnectorListener implements ServerAioListener{
    Logger logger = LoggerFactory.getLogger(PushConnectorListener.class);

    public void onAfterConnected(ChannelContext channelContext, boolean b, boolean b1) throws Exception {

    }

    public void onAfterDecoded(ChannelContext channelContext, Packet packet, int i) throws Exception {

    }

    public void onAfterReceivedBytes(ChannelContext channelContext, int i) throws Exception {

    }

    /**
     * 本接口用来回调发送是否成功,用户缓存消息，便于用户上线后消息下发
     * */
    public void onAfterSent(ChannelContext channelContext, Packet packet, boolean b) throws Exception {
        logger.info("onAfterSent client:"+channelContext.getClientNode()+" bsId "+channelContext.getBsId()+" sendSuccess "+b);
    }

    public void onAfterHandled(ChannelContext channelContext, Packet packet, long l) throws Exception {

    }

    public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String s, boolean b) throws Exception {
        //监听链接断开，清除相关的session,防止过多无用的发送消息
        if(StringUtils.isNotBlank(channelContext.getBsId())){
            sessionService().cleanSession(channelContext.getBsId());
        }
    }

    private SessionService sessionService(){
        PushCommonConfiguration pushServerConfiguration = (PushCommonConfiguration) SpringApplicationContext.getBean(Constants.PUSHSERVER_CONFIGURATION);
        return pushServerConfiguration.sessionService();
    }


}
