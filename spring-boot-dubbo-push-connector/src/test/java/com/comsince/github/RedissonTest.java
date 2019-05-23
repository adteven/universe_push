package com.comsince.github;

import com.comsince.github.utils.RedisUtils;
import org.junit.Before;
import org.junit.Test;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.tio.cluster.TioClusterVo;
import org.tio.utils.SerializeUtil;
import org.tio.utils.json.Json;

/**
 * @author comsicne
 *         Copyright (c) [2019] [Meizu.inc]
 * @Time 19-2-25 下午5:17
 **/
public class RedissonTest {
    RedissonClient redissonClient;
    @Before
    public void prepare(){
         redissonClient = RedisUtils.getInstance().getRedisson("172.16.178.28","22126");
    }

    @Test
    public void testPut(){
         long result = redissonClient.getAtomicLong("dubbo.test").addAndGet(1);
         System.out.println(result);

         RTopic rTopic = redissonClient.getTopic("topic");
         rTopic.addListener(TioClusterVo.class, new MessageListener<TioClusterVo>() {
             @Override
             public void onMessage(CharSequence channel, TioClusterVo msg) {

             }
         });
         System.out.println(rTopic);
    }

    @Test
    public void testListGroup(){
        RList<String> list = redissonClient.getList("push.test");
        for (String token :list){
            System.out.println(token);
        }
    }

    @Test
    public void testJson(){
        String clusterVo = "{\"clientId\":\"ad9e3360-3493-440d-843e-8e645c8a91ad\",\"packet\":{\"blockSend\":false,\"body\":\"YXNkZnNkZmRmYQ==\",\"byteCount\":0,\"fromCluster\":false,\"id\":12,\"sslEncrypted\":false,\"synSeq\":0},\"toAll\":true}";
        TioClusterVo tioClusterVo = Json.toBean(clusterVo,TioClusterVo.class);
        System.out.println(tioClusterVo.getPacket());
    }


    @Test
    public void testSerialize(){
        PushPacket pushPacket = new PushPacket();
        pushPacket.setBody("test".getBytes());
        TioClusterVo tioClusterVo = new TioClusterVo();
        tioClusterVo.setBsId("test");
        tioClusterVo.setPacket(pushPacket);

        byte[] result = SerializeUtil.serialize(tioClusterVo);
        TioClusterVo to = (TioClusterVo) SerializeUtil.deserialize(result,TioClusterVo.class);
        System.out.println(to.getPacket());
    }
}
