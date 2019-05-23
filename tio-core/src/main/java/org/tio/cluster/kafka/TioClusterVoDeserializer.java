package org.tio.cluster.kafka;

import com.alibaba.fastjson.JSON;
import org.apache.kafka.common.serialization.Deserializer;
import org.tio.cluster.TioClusterVo;
import org.tio.utils.SerializeUtil;
import org.tio.utils.json.Json;

import java.util.Map;

/**
 * @author comsicne
 * Copyright (c) [2019] [Meizu.inc]
 * @Time 19-5-22 下午4:01
 **/
public class TioClusterVoDeserializer implements Deserializer<TioClusterVo> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public TioClusterVo deserialize(String topic, byte[] data) {
        return (TioClusterVo) SerializeUtil.deserialize(data,TioClusterVo.class);
    }

    @Override
    public void close() {

    }
}
