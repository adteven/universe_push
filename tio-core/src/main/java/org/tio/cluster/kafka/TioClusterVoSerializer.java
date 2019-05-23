package org.tio.cluster.kafka;

import org.apache.kafka.common.serialization.Serializer;
import org.tio.cluster.TioClusterVo;
import org.tio.utils.SerializeUtil;

import java.util.Map;

/**
 * @author comsicne
 * Copyright (c) [2019] [Meizu.inc]
 * @Time 19-5-22 下午4:01
 **/
public class TioClusterVoSerializer implements Serializer<TioClusterVo> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, TioClusterVo data) {
        return SerializeUtil.serialize(data);
    }

    @Override
    public void close() {

    }
}
