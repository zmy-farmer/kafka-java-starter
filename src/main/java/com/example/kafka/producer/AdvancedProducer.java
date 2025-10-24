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
     * 发送带时间戳的消息
     */
    public void sendMessageWithTimestamp(String topic, String key, String message) {
        try {
            long timestamp = System.currentTimeMillis();
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, null, timestamp, key, message);
            
            Future<RecordMetadata> future = this.producer.send(record, (metadata, exception) -> {
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
        if (this.producer != null) {
            this.producer.close();
            logger.info("高级生产者已关闭");
        }
    }

}
