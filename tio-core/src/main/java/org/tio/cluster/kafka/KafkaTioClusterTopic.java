package org.tio.cluster.kafka;

import org.apache.dubbo.common.utils.NetUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.TioClusterMessageListener;
import org.tio.cluster.TioClusterTopic;
import org.tio.cluster.TioClusterVo;
import org.tio.utils.json.Json;
import sun.net.util.IPAddressUtil;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author comsicne
 * Copyright (c) [2019] [Meizu.inc]
 * @Time 19-5-22 下午2:56
 **/
public class KafkaTioClusterTopic implements TioClusterTopic {

    private Logger logger = LoggerFactory.getLogger(KafkaTioClusterTopic.class);

    private KafkaProducer<String,TioClusterVo> kafkaProducer;
    private KafkaConsumer<String,TioClusterVo> kafkaConsumer;
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private String channel;
    private TioClusterMessageListener tioClusterMessageListener;

    public KafkaTioClusterTopic(String channel,String broker){
        this.channel = channel;
        createProducer(broker);
        createConsumer(broker);
    }

    @Override
    public void publish(TioClusterVo tioClusterVo) {
        logger.info("publish "+ Json.toJson(tioClusterVo));
        kafkaProducer.send(new ProducerRecord<>(TioClusterConfig.TIO_CLUSTER_TOPIC + channel,tioClusterVo));
    }

    @Override
    public void addMessageListener(TioClusterMessageListener tioClusterMessageListener) {
        this.tioClusterMessageListener = tioClusterMessageListener;
    }

    public void createProducer(String brokerServer){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", brokerServer);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.tio.cluster.kafka.TioClusterVoSerializer");
        kafkaProducer = new KafkaProducer(properties);
    }

    public void createConsumer(String brokerServer){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", brokerServer);
        logger.info("local host is "+NetUtils.getLocalHost());
        properties.put("group.id", "push-connector"+ NetUtils.getLocalHost());
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.tio.cluster.kafka.TioClusterVoDeserializer");
        kafkaConsumer = new KafkaConsumer<String, TioClusterVo>(properties);
        kafkaConsumer.subscribe(Arrays.asList(TioClusterConfig.TIO_CLUSTER_TOPIC + channel));
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    ConsumerRecords<String, TioClusterVo> records = kafkaConsumer.poll(100);
                    for (ConsumerRecord<String, TioClusterVo> record : records) {
                        if(tioClusterMessageListener != null){
                            tioClusterMessageListener.onMessage(TioClusterConfig.TIO_CLUSTER_TOPIC + channel,record.value());
                        }
                    }
                }
            }
        });

    }
}
