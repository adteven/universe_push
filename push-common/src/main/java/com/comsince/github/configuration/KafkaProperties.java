package com.comsince.github.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author comsicne
 * Copyright (c) [2019] [Meizu.inc]
 * @Time 19-5-22 下午3:15
 **/
@Configuration
@ConfigurationProperties(prefix = "push.kafka")
public class KafkaProperties {
    String broker;


    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}
