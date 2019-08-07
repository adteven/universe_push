package com.comsince.github;

import cn.wildfirechat.proto.WFCMessage;
import com.comsince.github.message.ConnectMessage;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectInput;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;
import org.apache.zookeeper.server.ByteBufferOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * Unit test for simple App.
 */
public class AppTest{

    public static void testWFCmessage(){
        WFCMessage.AddFriendRequest addFriendRequest = WFCMessage.AddFriendRequest.newBuilder()
                .setReason("comsince 请求添加好友")
                .setTargetUid("comsince")
                .build();


        ConnectMessage connectMessage = new ConnectMessage();
        connectMessage.setUserName("comsince");

        ByteBuffer byteBuffer = ByteBuffer.allocate(500);
        OutputStream outputStream = new ByteBufferOutputStream(byteBuffer);
        Hessian2ObjectOutput hessian2ObjectOutput = new Hessian2ObjectOutput(outputStream);
        try {
            hessian2ObjectOutput.writeObject(addFriendRequest);
            hessian2ObjectOutput.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = byteBuffer.position();
        byte[] request = new byte[length];
        byteBuffer.flip();
        byteBuffer.get(request);
        InputStream inputStream = new ByteArrayInputStream(request);

        Hessian2ObjectInput hessian2ObjectInput = new Hessian2ObjectInput(inputStream);
        try {
            WFCMessage.AddFriendRequest result = hessian2ObjectInput.readObject(WFCMessage.AddFriendRequest.class);
            System.out.println("addfriendRequest "+result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void testConnectMessage(){

        ConnectMessage connectMessage = new ConnectMessage();
        connectMessage.setUserName("comsince");

        ByteBuffer byteBuffer = ByteBuffer.allocate(500);
        OutputStream outputStream = new ByteBufferOutputStream(byteBuffer);
        Hessian2ObjectOutput hessian2ObjectOutput = new Hessian2ObjectOutput(outputStream);
        try {
            hessian2ObjectOutput.writeObject(connectMessage);
            hessian2ObjectOutput.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int length = byteBuffer.position();
        byte[] request = new byte[length];
        byteBuffer.flip();
        byteBuffer.get(request);
        InputStream inputStream = new ByteArrayInputStream(request);

        Hessian2ObjectInput hessian2ObjectInput = new Hessian2ObjectInput(inputStream);
        try {
            ConnectMessage result = hessian2ObjectInput.readObject(ConnectMessage.class);
            System.out.println("addfriendRequest "+result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
