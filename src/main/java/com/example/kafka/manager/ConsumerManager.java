package com.example.kafka.manager;

import com.example.kafka.consumer.SimpleConsumer;
import com.example.kafka.consumer.AdvancedConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消费者管理器
 * 负责管理不同类型的消费者
 */
public class ConsumerManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConsumerManager.class);
    
    /**
     * 运行简单消费者
     */
    public void runSimpleConsumer() {
        System.out.println("\n启动简单消费者...");
        System.out.println("按 Enter 键停止消费者");
        
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 使用简单的输入监听
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                System.in.read();
                System.out.println("正在停止消费者...");
                consumer.stop();
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
        
        try {
            consumer.consumeMessages("test-topic");
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
        
        // 使用简单的输入监听
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                System.in.read();
                System.out.println("正在停止高级消费者...");
                consumer.stop();
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
        
        try {
            consumer.consumeMessagesMultiThread("test-topic");
        } catch (Exception e) {
            logger.error("高级消费者运行时发生错误", e);
        } finally {
            consumer.close();
            System.out.println("高级消费者已停止，返回主菜单...");
        }
    }
    
    /**
     * 运行指定主题的消费者
     */
    public void runConsumerForTopic(String topic) {
        System.out.println("\n启动消费者监听主题: " + topic);
        System.out.println("按 Enter 键停止消费者");
        
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 使用简单的输入监听
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                System.in.read();
                System.out.println("正在停止消费者...");
                consumer.stop();
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
        
        try {
            consumer.consumeMessages(topic);
        } catch (Exception e) {
            logger.error("消费者监听主题 " + topic + " 时发生错误", e);
        } finally {
            consumer.close();
        }
    }
    
    /**
     * 运行多主题消费者
     */
    public void runMultiTopicConsumer(String[] topics) {
        System.out.println("\n启动多主题消费者...");
        System.out.println("监听主题: " + String.join(", ", topics));
        System.out.println("按 Enter 键停止消费者");
        
        // 使用简单消费者处理多主题
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 使用简单的输入监听
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                System.in.read();
                System.out.println("正在停止多主题消费者...");
                consumer.stop();
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
        
        try {
            // 使用第一个主题作为主要监听主题
            if (topics.length > 0) {
                consumer.consumeMessages(topics[0]);
            } else {
                System.out.println("没有指定主题");
            }
        } catch (Exception e) {
            logger.error("多主题消费者运行时发生错误", e);
        } finally {
            consumer.close();
        }
    }
    
    /**
     * 运行真正的多线程消费者
     */
    public void runMultiThreadConsumer(String topic, int threadCount) {
        System.out.println("\n启动多线程消费者...");
        System.out.println("监听主题: " + topic + ", 线程数: " + threadCount);
        System.out.println("按 Enter 键停止消费者");
        
        com.example.kafka.consumer.ImprovedMultiThreadConsumer consumer = 
            new com.example.kafka.consumer.ImprovedMultiThreadConsumer(topic, threadCount);
        
        // 使用简单的输入监听
        Thread inputListener = new Thread(() -> {
            try {
                // 等待用户按Enter键
                System.in.read();
                System.out.println("正在停止多线程消费者...");
                consumer.stop();
            } catch (Exception e) {
                // 忽略异常
            }
        });
        inputListener.setDaemon(true);
        inputListener.start();
        
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
