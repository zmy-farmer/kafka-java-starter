package com.example.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Kafka配置类
 * 提供生产者和消费者的配置
 */
public class KafkaConfig {
    
    // Kafka服务器地址
    public static final String BOOTSTRAP_SERVERS = "localhost:29092";
    
    // 主题名称
    public static final String TOPIC_NAME = "test-topic";
    public static final String USER_TOPIC = "user-topic";
    public static final String ORDER_TOPIC = "order-topic";
    
    // 消费者组
    public static final String CONSUMER_GROUP = "test-group";
    
    /**
     * 获取生产者配置
     */
    public static Properties getProducerConfig() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        
        // 可靠性配置
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // 等待所有副本确认
        props.put(ProducerConfig.RETRIES_CONFIG, 3); // 重试次数
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // 幂等性
        
        // 性能配置
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // 批量大小
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5); // 等待时间
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432); // 缓冲区大小
        
        return props;
    }
    
    /**
     * 获取消费者配置
     */
    public static Properties getConsumerConfig() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        
        // 偏移量配置
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // 从最早开始消费
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 手动提交偏移量
        
        // 性能配置
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, 1);
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);
        
        return props;
    }
    
    /**
     * 获取流处理配置
     */
    public static Properties getStreamsConfig() {
        Properties props = new Properties();
        props.put(org.apache.kafka.streams.StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-app");
        props.put(org.apache.kafka.streams.StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, 
                  org.apache.kafka.common.serialization.Serdes.String().getClass().getName());
        props.put(org.apache.kafka.streams.StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, 
                  org.apache.kafka.common.serialization.Serdes.String().getClass().getName());
        
        return props;
    }
}
