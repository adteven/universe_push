package com.comsince.github;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;

import java.nio.ByteBuffer;


public class PushPacket extends Packet {
    Logger logger = LoggerFactory.getLogger(PushPacket.class);

    private Header header;
    private byte[] body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public Signal signal(){
        return Signal.PUSH;
    }

    public SubSignal subSignal(){
        return SubSignal.NONE;
    }

    public void setBody(byte[] body) {
        this.body = body;
        if(body != null){
            setByteCount(body.length);
        }
    }

    public void setBody(byte content){
        this.body = new byte[1];
        body[0] = content;
        setByteCount(1);
    }

    public ByteBuffer encode(){
        int bodyLength = 0;
        if(getBody() != null){
            bodyLength = getBody().length;
        }
        ByteBuffer buffer = ByteBuffer.allocate(Header.LENGTH + bodyLength);
        Header header = new Header();
        header.setSignal(signal());
        header.setSubSignal(subSignal());
        header.setLength(bodyLength);
        buffer.put(header.getContents());
        if(getBody() != null){
            buffer.put(getBody());
        }
        return buffer;
    }

    public PushPacket decode(ByteBuffer byteBuffer, int readableLength, ChannelContext channelContext) throws AioDecodeException{
        if(readableLength < Header.LENGTH){
            return null;
        }

        Header header = new Header(byteBuffer);
        if(!header.isValid()){
            //如果是非法信息，则直接关闭链接
            Tio.close(channelContext,"close by invalid signal");
        }
        setHeader(header);
        //读取消息体的长度
        int bodyLength = header.getLength();

        if (bodyLength < 0) {
            throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right,");
        }

        //计算本次需要的数据长度
        int neededLength = Header.LENGTH + bodyLength;
        //收到的数据是否足够组包
        int isDataEnough = readableLength - neededLength;
        logger.info("readableLength "+readableLength+" neededLength "+neededLength +" isDataEnough "+isDataEnough);
        if(isDataEnough < 0){
            return null;
        }

        if(header.getLength() > 0){
            //消息体大小
            byte[] body = new byte[header.getLength()];
            byteBuffer.get(body);
            setBody(body);
        }

        return this;
    }

}
