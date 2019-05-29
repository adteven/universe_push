package com.comsince.github;
import com.comsince.github.client.AndroidNIOClient;
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
                for(int i=0;i<50;i++){
//            sendConnect("172.16.177.107",6789);
//            sendConnect("172.16.176.23",6789);
//            sendConnect("172.16.176.25",6789);
//            sendConnect("127.0.0.1",6789);
                    sendConnect("152.136.147.18",6789);
                }
            }
        });


    }

    public static void sendConnect(String host, int port){
        final AndroidNIOClient nioClient = new AndroidNIOClient(host,port);
        nioClient.setPushMessageCallback(new PushMessageCallback() {
            @Override
            public void receiveMessage(Signal signal, String message) {

            }

            @Override
            public void receiveException(Exception e) {

            }

            @Override
            public void onConnected() {
                 nioClient.sub();
            }
        });
        nioClient.connect();
    }
}
