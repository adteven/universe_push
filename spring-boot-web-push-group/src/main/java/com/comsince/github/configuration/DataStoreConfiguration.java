package com.comsince.github.configuration;

import com.comsince.github.persistence.DatabaseStore;
import com.comsince.github.utils.ThreadPoolExecutorWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-6 下午2:57
 **/
@Configuration
public class DataStoreConfiguration {
    @Bean
    DatabaseStore databaseStore(){
        int threadNum = Runtime.getRuntime().availableProcessors() * 2;
        ThreadPoolExecutorWrapper dbScheduler = new ThreadPoolExecutorWrapper(Executors.newScheduledThreadPool(threadNum), threadNum, "db");
        DatabaseStore databaseStore = new DatabaseStore(dbScheduler);
        return databaseStore;
    }
}
