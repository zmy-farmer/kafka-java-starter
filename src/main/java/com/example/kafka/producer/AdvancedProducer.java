package com.example.kafka.producer;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.Order;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * 高级Kafka生产者示例
 * 演示分区、事务、压缩等高级功能
 */
public class AdvancedProducer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedProducer.class);
    private final Producer<String, String> producer;
    
    public AdvancedProducer() {
        Properties props = KafkaConfig.getProducerConfig();
        
        // 添加高级配置
        props.put("compression.type", "snappy"); // 压缩类型
        props.put("max.in.flight.requests.per.connection", 5); // 最大飞行请求数
        props.put("request.timeout.ms", 30000); // 请求超时时间
        
        this.producer = new KafkaProducer<>(props);
    }
    
    /**
     * 发送消息到指定分区
     */
    public void sendToPartition(String topic, int partition, String key, String message) {
        try {
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, partition, key, message);
            
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("消息发送失败: {}", exception.getMessage());
                } else {
                    logger.info("消息发送到分区 {} 成功 - Offset: {}", 
                              metadata.partition(), metadata.offset());
                }
            });
            
            RecordMetadata metadata = future.get();
            logger.info("消息已发送到分区: {}, 偏移量: {}", metadata.partition(), metadata.offset());
            
        } catch (Exception e) {
            logger.error("发送消息到分区时发生错误", e);
        }
    }
    
    /**
     * 发送订单消息（使用订单ID作为分区键）
     */
    public void sendOrderMessage(String topic, Order order) {
        try {
            String orderJson = JsonUtil.toJson(order);
            // 使用订单ID作为分区键，确保同一订单的消息发送到同一分区
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, order.getOrderId(), orderJson);
            
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("订单消息发送失败: {}", exception.getMessage());
                } else {
                    logger.info("订单消息发送成功 - 订单ID: {}, 分区: {}, 偏移量: {}", 
                              order.getOrderId(), metadata.partition(), metadata.offset());
                }
            });
            
            RecordMetadata metadata = future.get();
            logger.info("订单消息已发送 - 分区: {}, 偏移量: {}", metadata.partition(), metadata.offset());
            
        } catch (Exception e) {
            logger.error("发送订单消息时发生错误", e);
        }
    }
    
    /**
     * 获取主题分区信息
     */
    public void printTopicPartitions(String topic) {
        try {
            List<PartitionInfo> partitions = producer.partitionsFor(topic);
            logger.info("主题 {} 的分区信息:", topic);
            for (PartitionInfo partition : partitions) {
                logger.info("分区 {}: 领导者 {}, 副本 {}, ISR {}", 
                          partition.partition(), 
                          partition.leader().id(),
                          partition.replicas().length,
                          partition.inSyncReplicas().length);
            }
        } catch (Exception e) {
            logger.error("获取分区信息时发生错误", e);
        }
    }
    
    /**
     * 发送带时间戳的消息
     */
    public void sendMessageWithTimestamp(String topic, String key, String message) {
        try {
            long timestamp = System.currentTimeMillis();
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, timestamp, key, message);
            
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("消息发送失败: {}", exception.getMessage());
                } else {
                    logger.info("带时间戳的消息发送成功 - 分区: {}, 偏移量: {}", 
                              metadata.partition(), metadata.offset());
                }
            });
            
            RecordMetadata metadata = future.get();
            logger.info("带时间戳的消息已发送 - 分区: {}, 偏移量: {}", metadata.partition(), metadata.offset());
            
        } catch (Exception e) {
            logger.error("发送带时间戳的消息时发生错误", e);
        }
    }
    
    /**
     * 关闭生产者
     */
    public void close() {
        if (producer != null) {
            producer.close();
            logger.info("高级生产者已关闭");
        }
    }
    
    public static void main(String[] args) {
        AdvancedProducer producer = new AdvancedProducer();
        
        try {
            // 打印主题分区信息
            producer.printTopicPartitions(KafkaConfig.TOPIC_NAME);
            
            // 发送消息到指定分区
            producer.sendToPartition(KafkaConfig.TOPIC_NAME, 0, "partition-key", "发送到分区0的消息");
            
            // 发送订单消息
            Order order = new Order("ORDER-001", "USER-001", "笔记本电脑", 5999.99);
            producer.sendOrderMessage(KafkaConfig.ORDER_TOPIC, order);
            
            // 发送带时间戳的消息
            producer.sendMessageWithTimestamp(KafkaConfig.TOPIC_NAME, "timestamp-key", "带时间戳的消息");
            
        } finally {
            producer.close();
        }
    }
}
