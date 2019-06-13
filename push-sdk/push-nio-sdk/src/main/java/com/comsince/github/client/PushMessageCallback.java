package com.comsince.github.client;

import com.comsince.github.core.ByteBufferList;
import com.comsince.github.push.Signal;
import com.comsince.github.push.SubSignal;

public interface PushMessageCallback {
    void receiveMessage(Signal signal, SubSignal subSignal, ByteBufferList byteBufferList);
    void receiveException(Exception e);
    void onConnected();
}
