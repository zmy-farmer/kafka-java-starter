package com.example.kafka.manager;

import com.example.kafka.consumer.SimpleConsumer;
import com.example.kafka.consumer.AdvancedConsumer;
import com.example.kafka.consumer.ImprovedMultiThreadConsumer;
import com.example.kafka.config.KafkaConfig;
import com.example.kafka.util.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者管理器
 * 负责管理不同类型的消费者
 */
public class ConsumerManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsumerManager.class);
    
    /**
     * 创建并启动输入监听线程
     * @param consumer 消费者实例
     * @param consumerName 消费者名称（用于日志）
     */
    private void createInputListener(Object consumer, String consumerName) {
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                InputManager.readLine();
                System.out.println("正在停止" + consumerName + "...");
                
                // 调用消费者的stop方法
                if (consumer instanceof SimpleConsumer) {
                    ((SimpleConsumer) consumer).stop();
                } else if (consumer instanceof AdvancedConsumer) {
                    ((AdvancedConsumer) consumer).stop();
                } else if (consumer instanceof ImprovedMultiThreadConsumer) {
                    ((ImprovedMultiThreadConsumer) consumer).stop();
                }
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
    }
    
    /**
     * 运行简单消费者
     */
    public void runSimpleConsumer() {
        System.out.println("\n启动简单消费者...");
        System.out.println("按 Enter 键停止消费者");
        
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 使用专用的输入监听
        createInputListener(consumer, "简单消费者");
        
        try {
            consumer.consumeMessages(KafkaConfig.TOPIC_NAME);
        } catch (Exception e) {
            logger.error("简单消费者运行时发生错误", e);
        } finally {
            consumer.close();
            System.out.println("简单消费者已停止，返回主菜单...");
        }
    }
    
    /**
     * 运行高级消费者
     */
    public void runAdvancedConsumer() {
        System.out.println("\n启动高级消费者...");
        System.out.println("按 Enter 键停止消费者");
        
        AdvancedConsumer consumer = new AdvancedConsumer();
        
        // 使用专用的输入监听
        createInputListener(consumer, "高级消费者");
        
        try {
            consumer.consumeMessagesMultiThread(KafkaConfig.TOPIC_NAME);
        } catch (Exception e) {
            logger.error("高级消费者运行时发生错误", e);
        } finally {
            consumer.close();
            System.out.println("高级消费者已停止，返回主菜单...");
        }
    }
    
    /**
     * 运行真正的多线程消费者
     */
    public void runMultiThreadConsumer(String topic, int threadCount) {
        System.out.println("\n启动多线程消费者...");
        System.out.println("监听主题: " + topic + ", 线程数: " + threadCount);
        System.out.println("按 Enter 键停止消费者");
        
        ImprovedMultiThreadConsumer consumer = new ImprovedMultiThreadConsumer(topic, threadCount);
        
        // 使用专用的输入监听
        createInputListener(consumer, "多线程消费者");
        
        try {
            consumer.start();
            // 等待所有消费者线程完成
            consumer.waitForShutdown();
        } catch (Exception e) {
            logger.error("多线程消费者运行时发生错误", e);
        } finally {
            consumer.close();
            System.out.println("多线程消费者已停止，返回主菜单...");
        }
    }
}
