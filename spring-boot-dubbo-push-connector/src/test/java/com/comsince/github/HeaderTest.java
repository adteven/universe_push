package com.comsince.github;

import org.junit.Test;

public class HeaderTest {

    @Test
    public void testHeader(){
        Header header = new Header();
        header.setSignal(Signal.SUB);
        header.setSubSignal(SubSignal.MS);
        header.setMessageId(1232);
        header.setLength(70434);
        System.out.println("signal "+header.getSignal()+" subSignal "+header.getSubSignal()+" messageId "+header.getMessageId());
        System.out.println("content length "+header.getLength());
    }
}
