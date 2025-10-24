package com.example.kafka.consumer;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.User;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * 简单Kafka消费者示例
 * 演示基本的消息消费功能
 */
public class SimpleConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    private final Consumer<String, String> consumer;
    private volatile boolean running = true;
    
    public SimpleConsumer() {
        Properties props = KafkaConfig.getConsumerConfig();
        this.consumer = new KafkaConsumer<>(props);
    }
    
    /**
     * 订阅主题并消费消息
     */
    public void consumeMessages(String topic) {
        try {
            // 订阅主题
            consumer.subscribe(Arrays.asList(topic));
            logger.info("开始消费主题: {}", topic);
            
            while (this.running && !Thread.currentThread().isInterrupted()) {
                // 拉取消息，减少超时时间让程序更响应
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) {
                    continue;
                }
                
                logger.info("收到 {} 条消息", records.count());
                
                // 处理每条消息
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
                
                // 手动提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("消费消息时发生错误", e);
        } finally {
            logger.info("消费者正在关闭...");
            try {
                consumer.close();
                logger.info("消费者已关闭");
            } catch (Exception e) {
                logger.warn("关闭消费者时发生错误", e);
            }
        }
    }
    
    /**
     * 处理单条消息
     */
    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            logger.info("收到消息 - Topic: {}, Partition: {}, Offset: {}, Key: {}, Value: {}", 
                       record.topic(), record.partition(), record.offset(), 
                       record.key(), record.value());
            
            // 根据主题处理不同类型的消息
            if (KafkaConfig.USER_TOPIC.equals(record.topic())) {
                processUserMessage(record);
            } else {
                processSimpleMessage(record);
            }
            
        } catch (Exception e) {
            logger.error("处理消息时发生错误", e);
        }
    }
    
    /**
     * 处理用户消息
     */
    private void processUserMessage(ConsumerRecord<String, String> record) {
        try {
            User user = JsonUtil.fromJson(record.value(), User.class);
            logger.info("处理用户消息: {}", user);
            
            // 这里可以添加业务逻辑，比如保存到数据库
            // userService.saveUser(user);
            
        } catch (Exception e) {
            logger.error("处理用户消息时发生错误", e);
        }
    }
    
    /**
     * 处理简单消息
     */
    private void processSimpleMessage(ConsumerRecord<String, String> record) {
        logger.info("处理简单消息: {}", record.value());
        
        // 这里可以添加业务逻辑
        // 例如：数据验证、转换、存储等
    }

    /**
     * 停止消费
     */
    public void stop() {
        logger.info("正在停止消费者...");
        this.running = false;
        logger.info("消费者停止信号已发送");
    }
    
    /**
     * 关闭消费者
     */
    public void close() {
        if (consumer != null) {
            consumer.close();
            logger.info("消费者已关闭");
        }
    }

}
