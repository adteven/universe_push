package com.comsince.github;
import com.comsince.github.logger.Log;
import com.comsince.github.client.NIOClient;
import com.comsince.github.logger.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-21 下午2:48
 **/
public class PushNIOClientStarter {
    static Log log = LoggerFactory.getLogger(PushNIOClientStarter.class);
    static ExecutorService executorService = Executors.newFixedThreadPool(5);


    public static void main(String[] args){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<10000;i++){
                   sendConnect("localhost",6789);
                }
            }
        });


    }

    public static void sendConnect(String host, int port){
        final NIOClient nioClient = new NIOClient(host,port);
        nioClient.connect();
    }

}
