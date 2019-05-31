package com.comsince.github.service;


import com.comsince.github.push.Signal;

public interface MessageCallback {
    public void receiveMessage(Signal signal, String message);
}
