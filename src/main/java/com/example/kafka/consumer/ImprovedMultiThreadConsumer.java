package com.example.kafka.consumer;

import com.example.kafka.config.KafkaConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 改进的多线程消费者
 * 使用CountDownLatch来同步线程，确保能正确停止
 */
public class ImprovedMultiThreadConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(ImprovedMultiThreadConsumer.class);
    
    private final String topic;
    private final int threadCount;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final ExecutorService executorService;
    private final CountDownLatch shutdownLatch;
    
    public ImprovedMultiThreadConsumer(String topic, int threadCount) {
        this.topic = topic;
        this.threadCount = threadCount;
        this.executorService = Executors.newFixedThreadPool(threadCount);
        this.shutdownLatch = new CountDownLatch(threadCount);
    }
    
    /**
     * 启动多线程消费者
     */
    public void start() {
        logger.info("启动改进的多线程消费者，主题: {}, 线程数: {}", topic, threadCount);
        
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
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "improved-multi-thread-consumer-group-" + threadId);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "improved-consumer-thread-" + threadId);
        
        Consumer<String, String> consumer = new KafkaConsumer<>(props);
        
        try {
            consumer.subscribe(Arrays.asList(topic));
            logger.info("改进消费者线程 {} 开始消费主题: {}", threadId, topic);
            
            while (running.get() && !Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                
                if (records.isEmpty()) {
                    continue;
                }
                
                logger.info("改进线程 {} 收到 {} 条消息", threadId, records.count());
                
                for (ConsumerRecord<String, String> record : records) {
                    processMessage(threadId, record);
                }
                
                // 提交偏移量
                consumer.commitSync();
            }
            
        } catch (Exception e) {
            logger.error("改进消费者线程 {} 发生错误", threadId, e);
        } finally {
            consumer.close();
            logger.info("改进消费者线程 {} 已关闭", threadId);
            shutdownLatch.countDown();
        }
    }
    
    /**
     * 处理消息
     */
    private void processMessage(int threadId, ConsumerRecord<String, String> record) {
        try {
            logger.info("改进线程 {} 处理消息 - Topic: {}, Partition: {}, Offset: {}, Key: {}", 
                threadId, record.topic(), record.partition(), record.offset(), record.key());
            
            // 模拟消息处理
            Thread.sleep(100);
            
            logger.info("改进线程 {} 处理简单消息: {}", threadId, record.value());
            
        } catch (Exception e) {
            logger.error("改进线程 {} 处理消息时发生错误", threadId, e);
        }
    }
    
    /**
     * 停止消费者
     */
    public void stop() {
        logger.info("正在停止改进的多线程消费者...");
        running.set(false);
        executorService.shutdown();
    }
    
    /**
     * 等待所有线程完成
     */
    public void waitForShutdown() {
        try {
            shutdownLatch.await();
            logger.info("所有改进消费者线程已关闭");
        } catch (InterruptedException e) {
            logger.info("等待改进消费者线程关闭时被中断");
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 关闭资源
     */
    public void close() {
        stop();
        waitForShutdown();
    }
}
