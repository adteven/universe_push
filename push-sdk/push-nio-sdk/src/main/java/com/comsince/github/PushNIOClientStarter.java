package com.comsince.github;
import com.comsince.github.client.AndroidNIOClient;
import com.comsince.github.client.ImAndroidClient;
import com.comsince.github.client.PushMessageCallback;
import com.comsince.github.logger.Log;
import com.comsince.github.client.NIOClient;
import com.comsince.github.logger.LoggerFactory;
import com.comsince.github.push.Signal;

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
                for(int i=0;i<1;i++){
//            sendConnect("172.16.177.107",6789);
//            sendConnect("172.16.176.23",6789);
//            sendConnect("172.16.176.25",6789);
            sendImConnect("127.0.0.1",6789);
//                    sendConnect("152.136.147.18",6789);
//                    try {
//                        Thread.sleep(50);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });


    }

    public static void sendConnect(String host, int port){
        final NIOClient nioClient = new NIOClient(host,port);
        nioClient.connect();
    }

    public static void sendImConnect(String host, int port){
        final ImAndroidClient imAndroidClient = new ImAndroidClient(host,port);
        imAndroidClient.connect();
    }
}
