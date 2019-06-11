package com.comsince.github.client;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.core.callback.CompletedCallback;
import com.comsince.github.message.ConnectMessage;
import com.comsince.github.push.Signal;
import com.comsince.github.push.SubSignal;
import org.tio.utils.json.Json;

/**
 * @author comsicne
 * Copyright (c) [2019]
 * @Time 19-6-11 下午3:16
 **/
public class ImAndroidClient extends AndroidNIOClient implements PushMessageCallback{
    public ImAndroidClient(String host, int port) {
        super(host, port);
        setPushMessageCallback(this);
    }


    public void connectIM(){
        ConnectMessage connectMessage = new ConnectMessage();
        connectMessage.setUserName("13802671429");
        connectMessage.setClientIdentifier("imei");

        sendMessage(Signal.CONNECT, Json.toJson(connectMessage), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                AddFriendRequest();
            }
        });
    }

    public void AddFriendRequest(){
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setReason("comsince 请求添加好友")
                .setTargetUid("comsince")
                .build();
        sendMessage(Signal.PUBLISH, SubSignal.FAR, addFriendRequest.toByteArray(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {

            }
        });
    }

    @Override
    public void receiveMessage(Signal signal, String message) {

    }

    @Override
    public void receiveException(Exception e) {

    }

    @Override
    public void onConnected() {
        connectIM();
    }
}
