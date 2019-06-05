package com.comsince.github.configuration;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.FileSystemXmlConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileNotFoundException;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 下午2:23
 **/
@Configuration
public class HazelcastConfiguration {
    private static final Logger LOG = LoggerFactory.getLogger(HazelcastConfiguration.class);
    @Bean
    public HazelcastInstance hazelcastInstance(){
        HazelcastInstance hazelcastInstance;
        String hzConfigPath = "config/hazelcast.xml";
        if (hzConfigPath != null) {
            boolean isHzConfigOnClasspath = this.getClass().getClassLoader().getResource(hzConfigPath) != null;
            Config hzconfig = null;
            try {
                hzconfig = isHzConfigOnClasspath
                        ? new ClasspathXmlConfig(hzConfigPath)
                        : new FileSystemXmlConfig(hzConfigPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            LOG.info("Starting Hazelcast instance. ConfigurationFile={}", hzconfig);
            hazelcastInstance = Hazelcast.newHazelcastInstance(hzconfig);
        } else {
            LOG.info("Starting Hazelcast instance with default configuration");
            hazelcastInstance = Hazelcast.newHazelcastInstance();
        }
        return hazelcastInstance;
    }
}
