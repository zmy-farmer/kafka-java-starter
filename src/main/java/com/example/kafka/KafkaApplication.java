package com.example.kafka;

import com.example.kafka.consumer.SimpleConsumer;
import com.example.kafka.producer.SimpleProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Kafka应用主类
 * 提供交互式菜单来运行不同的Kafka示例
 */
public class KafkaApplication {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaApplication.class);
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            while (true) {
            printMenu();
            System.out.print("请选择操作 (1-6): ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
                
                switch (choice) {
                    case 1:
                        runSimpleProducer();
                        break;
                    case 2:
                        runSimpleConsumer();
                        break;
                    case 3:
                        runAdvancedProducer();
                        break;
                    case 4:
                        runAdvancedConsumer();
                        break;
                    case 5:
                        runStreamProcessor();
                        break;
                    case 6:
                        System.out.println("退出程序");
                        return;
                    default:
                        System.out.println("无效选择，请重新输入");
                }
            } catch (Exception e) {
                logger.error("处理用户输入时发生错误", e);
                scanner.nextLine(); // 清除错误输入
            }
        }
        } finally {
            scanner.close();
        }
    }
    
    private static void printMenu() {
        System.out.println("\n=== Kafka 详细使用示例 ===");
        System.out.println("1. 运行简单生产者");
        System.out.println("2. 运行简单消费者");
        System.out.println("3. 运行高级生产者");
        System.out.println("4. 运行高级消费者");
        System.out.println("5. 运行流处理器");
        System.out.println("6. 退出");
        System.out.println("==========================");
    }
    
    private static void runSimpleProducer() {
        System.out.println("\n启动简单生产者...");
        SimpleProducer producer = new SimpleProducer();
        
        try {
            // 发送测试消息
            producer.sendMessage("test-topic", "test-key", "Hello from Simple Producer!");
            System.out.println("消息发送完成");
        } finally {
            producer.close();
        }
    }
    
    private static void runSimpleConsumer() {
        System.out.println("\n启动简单消费者...");
        System.out.println("按 Ctrl+C 停止消费者");
        
        SimpleConsumer consumer = new SimpleConsumer();
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在停止消费者...");
            consumer.stop();
        }));
        
        try {
            consumer.consumeMessages("test-topic");
        } finally {
            consumer.close();
        }
    }
    
    private static void runAdvancedProducer() {
        System.out.println("\n启动高级生产者...");
        com.example.kafka.producer.AdvancedProducer producer = new com.example.kafka.producer.AdvancedProducer();
        
        try {
            // 发送测试消息
            producer.sendMessageWithTimestamp("test-topic", "advanced-key", "Hello from Advanced Producer!");
            System.out.println("高级生产者消息发送完成");
        } finally {
            producer.close();
        }
    }
    
    private static void runAdvancedConsumer() {
        System.out.println("\n启动高级消费者...");
        System.out.println("按 Ctrl+C 停止消费者");
        
        com.example.kafka.consumer.AdvancedConsumer consumer = new com.example.kafka.consumer.AdvancedConsumer();
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n正在停止高级消费者...");
            consumer.stop();
        }));
        
        try {
            consumer.consumeMessagesMultiThread("test-topic");
        } finally {
            consumer.close();
        }
    }
    
    private static void runStreamProcessor() {
        System.out.println("\n启动流处理器...");
        System.out.println("按 Ctrl+C 停止流处理器");
        
        // 这里可以启动流处理应用
        System.out.println("流处理器功能需要单独运行相应的流处理类");
        System.out.println("例如: java -cp target/kafka-demo-1.0.0.jar com.example.kafka.streams.WordCountStream");
    }
}
