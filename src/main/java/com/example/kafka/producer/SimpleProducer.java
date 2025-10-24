package com.example.kafka.producer;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.User;
import com.example.kafka.util.JsonUtil;
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
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
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
     * 发送用户对象消息
     */
    public void sendUserMessage(String topic, String key, User user) {
        try {
            String userJson = JsonUtil.toJson(user);
            sendMessage(topic, key, userJson);
        } catch (Exception e) {
            logger.error("发送用户消息时发生错误", e);
        }
    }
    
    /**
     * 批量发送消息
     */
    public void sendBatchMessages(String topic, int messageCount) {
        logger.info("开始批量发送 {} 条消息到主题: {}", messageCount, topic);
        
        for (int i = 0; i < messageCount; i++) {
            String key = "key-" + i;
            String message = "消息内容-" + i + "-" + System.currentTimeMillis();
            sendMessage(topic, key, message);
            
            // 添加小延迟，避免发送过快
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        logger.info("批量发送完成");
    }
    
    /**
     * 关闭生产者
     */
    public void close() {
        if (producer != null) {
            producer.close();
            logger.info("生产者已关闭");
        }
    }
    
    public static void main(String[] args) {
        SimpleProducer producer = new SimpleProducer();
        
        try {
            // 发送简单消息
            producer.sendMessage(KafkaConfig.TOPIC_NAME, "test-key", "Hello Kafka!");
            
            // 发送用户消息
            User user = new User("1", "张三", "zhangsan@example.com", 25);
            producer.sendUserMessage(KafkaConfig.USER_TOPIC, "user-1", user);
            
            // 批量发送消息
            producer.sendBatchMessages(KafkaConfig.TOPIC_NAME, 5);
            
        } finally {
            producer.close();
        }
    }
}
