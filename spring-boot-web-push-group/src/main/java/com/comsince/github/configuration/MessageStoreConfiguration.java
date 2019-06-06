package com.comsince.github.configuration;

import com.comsince.github.persistence.DatabaseStore;
import com.comsince.github.persistence.MemoryMessagesStore;
import com.comsince.github.persistence.MemorySessionStore;
import com.comsince.github.util.DBUtil;
import com.comsince.github.util.ThreadPoolExecutorWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-5 下午2:25
 **/
@Configuration
public class MessageStoreConfiguration extends HazelcastConfiguration{

    @Autowired
    IMConfig imConfig;

    @Autowired
    DatabaseStore databaseStore;

    @PostConstruct
    void init(){
        DBUtil.init(imConfig);

    }

    @Bean
    MemoryMessagesStore memoryMessagesStore(){
        MemoryMessagesStore memoryMessagesStore = new MemoryMessagesStore(databaseStore,memorySessionStore(),hazelcastInstance());
        memoryMessagesStore.initStore();
        return memoryMessagesStore;
    }

    @Bean
    MemorySessionStore memorySessionStore(){
        MemorySessionStore memorySessionStore = new MemorySessionStore(databaseStore);
        return memorySessionStore;
    }
}
