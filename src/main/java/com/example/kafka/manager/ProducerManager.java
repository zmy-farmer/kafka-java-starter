package com.example.kafka.manager;

import com.example.kafka.producer.SimpleProducer;
import com.example.kafka.producer.AdvancedProducer;
import com.example.kafka.config.KafkaConfig;
import com.example.kafka.util.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生产者管理器
 * 负责管理不同类型的生产者
 */
public class ProducerManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ProducerManager.class);
    
    /**
     * 运行简单生产者
     */
    public void runSimpleProducer() {
        System.out.println("\n启动简单生产者...");
        SimpleProducer producer = new SimpleProducer();
        
        try {
            // 发送测试消息
            producer.sendMessage(KafkaConfig.TOPIC_NAME, "test-key", "Hello from Simple Producer!");
            System.out.println("消息发送完成");
        } catch (Exception e) {
            logger.error("简单生产者发送消息时发生错误", e);
        } finally {
            producer.close();
        }
    }
    
    /**
     * 运行高级生产者
     */
    public void runAdvancedProducer() {
        System.out.println("\n启动高级生产者...");
        AdvancedProducer producer = new AdvancedProducer();
        
        try {
            // 发送测试消息
            producer.sendMessageWithTimestamp(KafkaConfig.TOPIC_NAME, "advanced-key", "Hello from Advanced Producer!");
            System.out.println("高级生产者消息发送完成");
        } catch (Exception e) {
            logger.error("高级生产者发送消息时发生错误", e);
        } finally {
            producer.close();
        }
    }
    
    /**
     * 运行交互式生产者
     */
    public void runInteractiveProducer() {
        System.out.println("\n启动交互式生产者...");
        System.out.println("输入消息内容，输入 'quit' 退出");
        
        SimpleProducer producer = new SimpleProducer();
        
        try {
            while (true) {
                System.out.print("请输入消息: ");
                String message = InputManager.readLine();
                
                if (message == null || "quit".equalsIgnoreCase(message)) {
                    break;
                }
                
                if (!message.trim().isEmpty()) {
                    producer.sendMessage(KafkaConfig.TOPIC_NAME, "interactive-key", message);
                    System.out.println("消息已发送: " + message);
                }
            }
        } catch (Exception e) {
            logger.error("交互式生产者运行时发生错误", e);
        } finally {
            producer.close();
        }
    }
}
