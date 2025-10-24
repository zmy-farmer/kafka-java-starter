package com.example.kafka.consumer;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.Order;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 高级Kafka消费者示例
 * 演示多线程消费、手动提交偏移量、错误处理等高级功能
 */
public class AdvancedConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedConsumer.class);
    private final Consumer<String, String> consumer;
    private final ExecutorService executorService;
    private volatile boolean running = true;
    
    public AdvancedConsumer() {
        Properties props = KafkaConfig.getConsumerConfig();
        this.consumer = new KafkaConsumer<>(props);
        this.executorService = Executors.newFixedThreadPool(3);
    }
    
    /**
     * 多线程消费消息（修复版本）
     * 注意：KafkaConsumer不是线程安全的，所以这里使用单线程消费，多线程处理
     */
    public void consumeMessagesMultiThread(String topic) {
        try {
            consumer.subscribe(Arrays.asList(topic));
            logger.info("开始多线程消费主题: {}", topic);
            
            while (running && !Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) {
                    continue;
                }
                
                logger.info("收到 {} 条消息，开始多线程处理", records.count());
                
                // 按分区分组处理消息
                Set<TopicPartition> partitions = records.partitions();
                
                for (TopicPartition partition : partitions) {
                    List<ConsumerRecord<String, String>> partitionRecords = records.records(partition);
                    
                    // 为每个分区创建处理任务（只处理消息，不提交偏移量）
                    executorService.submit(() -> {
                        processPartitionRecordsSafe(partition, partitionRecords);
                    });
                }
                
                // 在主线程中统一提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("多线程消费消息时发生错误", e);
        } finally {
            logger.info("多线程消费者正在关闭...");
            executorService.shutdown();
            consumer.close();
            logger.info("多线程消费者已关闭");
        }
    }
    
    /**
     * 处理分区内的消息（线程安全版本）
     * 只处理消息，不提交偏移量
     */
    private void processPartitionRecordsSafe(TopicPartition partition, List<ConsumerRecord<String, String>> records) {
        try {
            logger.info("处理分区 {} 的 {} 条消息", partition.partition(), records.size());
            
            for (ConsumerRecord<String, String> record : records) {
                processMessage(record);
            }
            
            logger.info("分区 {} 的 {} 条消息处理完成", partition.partition(), records.size());
            
        } catch (Exception e) {
            logger.error("处理分区 {} 消息时发生错误", partition.partition(), e);
        }
    }
    
    /**
     * 处理分区内的消息（旧版本，保留用于兼容性）
     */
    private void processPartitionRecords(TopicPartition partition, List<ConsumerRecord<String, String>> records) {
        try {
            logger.info("处理分区 {} 的 {} 条消息", partition.partition(), records.size());
            
            for (ConsumerRecord<String, String> record : records) {
                processMessage(record);
            }
            
            // 注意：这个方法在多线程环境中不安全
            // 手动提交该分区的偏移量
            long lastOffset = records.get(records.size() - 1).offset();
            Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();
            offsets.put(partition, new OffsetAndMetadata(lastOffset + 1));
            consumer.commitSync(offsets);
            
            logger.info("分区 {} 偏移量已提交到: {}", partition.partition(), lastOffset + 1);
            
        } catch (Exception e) {
            logger.error("处理分区 {} 消息时发生错误", partition.partition(), e);
        }
    }
    
    /**
     * 处理单条消息
     */
    private void processMessage(ConsumerRecord<String, String> record) {
        try {
            logger.info("处理消息 - Topic: {}, Partition: {}, Offset: {}, Key: {}", 
                       record.topic(), record.partition(), record.offset(), record.key());
            
            // 根据主题处理不同类型的消息
            if (KafkaConfig.ORDER_TOPIC.equals(record.topic())) {
                processOrderMessage(record);
            } else {
                processSimpleMessage(record);
            }
            
        } catch (Exception e) {
            logger.error("处理消息时发生错误", e);
        }
    }
    
    /**
     * 处理订单消息
     */
    private void processOrderMessage(ConsumerRecord<String, String> record) {
        try {
            Order order = JsonUtil.fromJson(record.value(), Order.class);
            logger.info("处理订单消息: {}", order);
            
            // 模拟业务处理
            Thread.sleep(100); // 模拟处理时间
            
            // 这里可以添加业务逻辑
            // orderService.processOrder(order);
            
        } catch (Exception e) {
            logger.error("处理订单消息时发生错误", e);
        }
    }
    
    /**
     * 处理简单消息
     */
    private void processSimpleMessage(ConsumerRecord<String, String> record) {
        logger.info("处理简单消息: {}", record.value());
        
        // 模拟业务处理
        try {
            Thread.sleep(50); // 模拟处理时间
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 消费指定分区的消息
     */
    public void consumeFromPartition(String topic, int partition) {
        try {
            TopicPartition topicPartition = new TopicPartition(topic, partition);
            consumer.assign(Arrays.asList(topicPartition));
            
            logger.info("开始消费主题 {} 分区 {}", topic, partition);
            
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(record);
                }
                
                // 手动提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("消费指定分区消息时发生错误", e);
        } finally {
            consumer.close();
        }
    }
    
    /**
     * 批量消费消息
     */
    public void consumeBatchMessages(String topic, int batchSize) {
        try {
            consumer.subscribe(Arrays.asList(topic));
            logger.info("开始批量消费主题: {}, 批次大小: {}", topic, batchSize);
            
            while (running) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                
                if (records.isEmpty()) {
                    continue;
                }
                
                // 分批处理消息
                List<ConsumerRecord<String, String>> batch = new ArrayList<>();
                for (ConsumerRecord<String, String> record : records) {
                    batch.add(record);
                    
                    if (batch.size() >= batchSize) {
                        processBatch(batch);
                        batch.clear();
                    }
                }
                
                // 处理剩余消息
                if (!batch.isEmpty()) {
                    processBatch(batch);
                }
                
                // 提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("批量消费消息时发生错误", e);
        } finally {
            consumer.close();
        }
    }
    
    /**
     * 处理批次消息
     */
    private void processBatch(List<ConsumerRecord<String, String>> batch) {
        try {
            logger.info("处理批次消息，数量: {}", batch.size());
            
            for (ConsumerRecord<String, String> record : batch) {
                processMessage(record);
            }
            
            logger.info("批次处理完成");
            
        } catch (Exception e) {
            logger.error("处理批次消息时发生错误", e);
        }
    }
    
    /**
     * 停止消费
     */
    public void stop() {
        running = false;
        logger.info("高级消费者停止");
        // 中断当前线程
        Thread.currentThread().interrupt();
    }
    
    /**
     * 关闭消费者
     */
    public void close() {
        if (executorService != null) {
            executorService.shutdown();
        }
        if (consumer != null) {
            consumer.close();
            logger.info("高级消费者已关闭");
        }
    }
    
    public static void main(String[] args) {
        AdvancedConsumer consumer = new AdvancedConsumer();
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("收到关闭信号，正在停止高级消费者...");
            consumer.stop();
        }));
        
        try {
            // 多线程消费消息
            consumer.consumeMessagesMultiThread(KafkaConfig.TOPIC_NAME);
        } finally {
            consumer.close();
        }
    }
}
