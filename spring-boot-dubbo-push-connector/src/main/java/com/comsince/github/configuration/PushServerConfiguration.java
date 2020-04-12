package com.comsince.github.configuration;

import com.comsince.github.PushServer;
import com.comsince.github.websocket.ShowcaseServerConfig;
import com.comsince.github.websocket.ShowcaseWebsocketStarter;
import com.comsince.github.websocket.ShowcaseWsMsgHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-28 下午3:01
 **/
@Configuration
@Component("pushServerConfiguration")
public class PushServerConfiguration extends PushCommonConfiguration{
    @Bean
    public PushServer pushServer() throws IOException {
        PushServer pushServer = new PushServer();
        pushServer.init(kafkaProperties.getBroker());
        return pushServer;
    }

    @Bean
    public ShowcaseWebsocketStarter webSocketServer() throws Exception {
        ShowcaseWebsocketStarter showcaseWebsocketStarter = new ShowcaseWebsocketStarter(ShowcaseServerConfig.SERVER_PORT,
                ShowcaseWsMsgHandler.me,
                sslConfiguration.getKeystore(),
                sslConfiguration.getTruststore(),
                sslConfiguration.password);
        showcaseWebsocketStarter.getWsServerStarter().start();
        return showcaseWebsocketStarter;
    }
}
