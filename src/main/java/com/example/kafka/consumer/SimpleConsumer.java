package com.example.kafka.consumer;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.User;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

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
            
            while (running && !Thread.currentThread().isInterrupted()) {
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
            consumer.close();
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
     * 从指定偏移量开始消费
     */
    public void consumeFromOffset(String topic, int partition, long offset) {
        try {
            TopicPartition topicPartition = new TopicPartition(topic, partition);
            consumer.assign(Arrays.asList(topicPartition));
            consumer.seek(topicPartition, offset);
            
            logger.info("从主题 {} 分区 {} 偏移量 {} 开始消费", topic, partition, offset);
            
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
                
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("从指定偏移量消费时发生错误", e);
        } finally {
            consumer.close();
        }
    }
    
    /**
     * 获取当前消费进度
     */
    public void printConsumerProgress() {
        try {
            Set<TopicPartition> assignments = consumer.assignment();
            for (TopicPartition partition : assignments) {
                long position = consumer.position(partition);
                logger.info("主题 {} 分区 {} 当前偏移量: {}", 
                          partition.topic(), partition.partition(), position);
            }
        } catch (Exception e) {
            logger.error("获取消费进度时发生错误", e);
        }
    }
    
    /**
     * 停止消费
     */
    public void stop() {
        running = false;
        logger.info("消费者停止");
        // 中断当前线程
        Thread.currentThread().interrupt();
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
    
    public static void main(String[] args) {
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("收到关闭信号，正在停止消费者...");
            consumer.stop();
        }));
        
        try {
            // 消费消息
            consumer.consumeMessages(KafkaConfig.TOPIC_NAME);
        } finally {
            consumer.close();
        }
    }
}
