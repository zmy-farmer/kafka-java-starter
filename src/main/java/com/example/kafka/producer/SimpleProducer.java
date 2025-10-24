package com.example.kafka.producer;

import com.example.kafka.config.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 简单Kafka生产者示例
 * 演示基本的消息发送功能
 */
public class SimpleProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleProducer.class);
    private final Producer<String, String> producer;
    
    public SimpleProducer() {
        Properties props = KafkaConfig.getProducerConfig();
        this.producer = new KafkaProducer<>(props);
    }
    
    /**
     * 发送简单文本消息
     */
    public void sendMessage(String topic, String key, String message) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);
            
            // 异步发送
            Future<RecordMetadata> future = this.producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("消息发送失败: {}", exception.getMessage());
                } else {
                    logger.info("消息发送成功 - Topic: {}, Partition: {}, Offset: {}", 
                              metadata.topic(), metadata.partition(), metadata.offset());
                }
            });
            
            // 等待发送完成
            RecordMetadata metadata = future.get();
            logger.info("消息已发送到分区: {}, 偏移量: {}", metadata.partition(), metadata.offset());
            
        } catch (Exception e) {
            logger.error("发送消息时发生错误", e);
        }
    }
    
    /**
     * 关闭生产者
     */
    public void close() {
        if (this.producer != null) {
            this.producer.close();
            logger.info("生产者已关闭");
        }
    }

}
