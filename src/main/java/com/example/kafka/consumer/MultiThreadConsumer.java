package com.example.kafka.consumer;

import com.example.kafka.config.KafkaConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 真正的多线程消费者
 * 每个线程使用独立的KafkaConsumer实例
 */
public class MultiThreadConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(MultiThreadConsumer.class);
    
    private final String topic;
    private final int threadCount;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executorService;
    private Thread mainThread;
    
    public MultiThreadConsumer(String topic, int threadCount) {
        this.topic = topic;
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }
    
    /**
     * 启动多线程消费者
     */
    public void start() {
        logger.info("启动多线程消费者，主题: {}, 线程数: {}", topic, threadCount);
        
        // 保存主线程引用
        mainThread = Thread.currentThread();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.submit(() -> {
                runConsumerThread(threadId);
            });
        }
    }
    
    /**
     * 运行消费者线程
     */
    private void runConsumerThread(int threadId) {
        Properties props = KafkaConfig.getConsumerConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "multi-thread-consumer-group-" + threadId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "consumer-thread-" + threadId);
        
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        
        try {
            consumer.subscribe(Arrays.asList(topic));
            logger.info("消费者线程 {} 开始消费主题: {}", threadId, topic);
            
            while (running.get()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) {
                    continue;
                }
                
                logger.info("线程 {} 收到 {} 条消息", threadId, records.count());
                
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(threadId, record);
                }
                
                // 提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("消费者线程 {} 发生错误", threadId, e);
        } finally {
            consumer.close();
            logger.info("消费者线程 {} 已关闭", threadId);
        }
    }
    
    /**
     * 处理消息
     */
    private void processMessage(int threadId, ConsumerRecord<String, String> record) {
        try {
            logger.info("线程 {} 处理消息 - Topic: {}, Partition: {}, Offset: {}, Key: {}", 
                threadId, record.topic(), record.partition(), record.offset(), record.key());
            
            // 模拟消息处理
            Thread.sleep(100);
            
            logger.info("线程 {} 处理简单消息: {}", threadId, record.value());
            
        } catch (Exception e) {
            logger.error("线程 {} 处理消息时发生错误", threadId, e);
        }
    }
    
    /**
     * 停止消费者
     */
    public void stop() {
        logger.info("正在停止多线程消费者...");
        running.set(false);
        executorService.shutdown();
        
        // 中断主线程
        if (mainThread != null) {
            mainThread.interrupt();
        }
    }
    
    /**
     * 关闭资源
     */
    public void close() {
        stop();
    }
}
