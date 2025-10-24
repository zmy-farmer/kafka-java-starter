package com.example.kafka;

import com.example.kafka.manager.ProducerManager;
import com.example.kafka.manager.ConsumerManager;
import com.example.kafka.manager.StreamProcessorManager;

/**
 * 应用启动器
 * 提供更灵活的应用启动方式
 */
public class ApplicationLauncher {
    
    public static void main(String[] args) {
        if (args.length > 0) {
            // 命令行模式
            runCommandLineMode(args);
        } else {
            // 交互式模式
            runInteractiveMode();
        }
    }
    
    /**
     * 交互式模式
     */
    private static void runInteractiveMode() {
        KafkaApplication app = new KafkaApplication();
        app.run();
    }
    
    /**
     * 命令行模式
     */
    private static void runCommandLineMode(String[] args) {
        String command = args[0];
        
        switch (command.toLowerCase()) {
            case "producer":
                runProducerMode(args);
                break;
            case "consumer":
                runConsumerMode(args);
                break;
            case "stream":
                runStreamMode(args);
                break;
            case "help":
                printHelp();
                break;
            default:
                System.out.println("未知命令: " + command);
                printHelp();
        }
    }
    
    /**
     * 生产者模式
     */
    private static void runProducerMode(String[] args) {
        ProducerManager producerManager = new ProducerManager();
        
        if (args.length > 1) {
            switch (args[1]) {
                case "simple":
                    producerManager.runSimpleProducer();
                    break;
                case "advanced":
                    producerManager.runAdvancedProducer();
                    break;
                case "interactive":
                    producerManager.runInteractiveProducer();
                    break;
                default:
                    System.out.println("未知的生产者类型: " + args[1]);
            }
        } else {
            producerManager.runSimpleProducer();
        }
    }
    
    /**
     * 消费者模式
     */
    private static void runConsumerMode(String[] args) {
        ConsumerManager consumerManager = new ConsumerManager();
        
        if (args.length > 1) {
            switch (args[1]) {
                case "simple":
                    consumerManager.runSimpleConsumer();
                    break;
                case "advanced":
                    consumerManager.runAdvancedConsumer();
                    break;
                default:
                    System.out.println("未知的消费者类型: " + args[1]);
            }
        } else {
            consumerManager.runSimpleConsumer();
        }
    }
    
    /**
     * 流处理模式
     */
    private static void runStreamMode(String[] args) {
        StreamProcessorManager streamProcessorManager = new StreamProcessorManager();
        
        if (args.length > 1) {
            switch (args[1]) {
                case "wordcount":
                    streamProcessorManager.runWordCountStream();
                    break;
                case "order":
                    streamProcessorManager.runOrderProcessingStream();
                    break;
                case "advanced":
                    streamProcessorManager.runAdvancedStreamProcessor();
                    break;
                default:
                    System.out.println("未知的流处理器类型: " + args[1]);
            }
        } else {
            System.out.println("请指定流处理器类型: wordcount, order, advanced");
        }
    }
    
    /**
     * 打印帮助信息
     */
    private static void printHelp() {
        System.out.println("\n=== Kafka 应用启动器 ===");
        System.out.println("用法: java -cp target/classes com.example.kafka.ApplicationLauncher [command] [options]");
        System.out.println("\n命令:");
        System.out.println("  (无参数)           - 启动交互式模式");
        System.out.println("  producer [type]     - 启动生产者模式");
        System.out.println("    simple           - 简单生产者");
        System.out.println("    advanced         - 高级生产者");
        System.out.println("    interactive      - 交互式生产者");
        System.out.println("  consumer [type]    - 启动消费者模式");
        System.out.println("    simple           - 简单消费者");
        System.out.println("    advanced         - 高级消费者");
        System.out.println("  stream [type]      - 启动流处理模式");
        System.out.println("    wordcount        - 单词计数流处理器");
        System.out.println("    order            - 订单处理流处理器");
        System.out.println("    advanced         - 高级流处理器");
        System.out.println("  help               - 显示此帮助信息");
        System.out.println("\n示例:");
        System.out.println("  java -cp target/classes com.example.kafka.ApplicationLauncher");
        System.out.println("  java -cp target/classes com.example.kafka.ApplicationLauncher producer simple");
        System.out.println("  java -cp target/classes com.example.kafka.ApplicationLauncher consumer advanced");
        System.out.println("  java -cp target/classes com.example.kafka.ApplicationLauncher stream wordcount");
        System.out.println("========================");
    }
}
