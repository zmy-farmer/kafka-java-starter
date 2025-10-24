package com.example.kafka;

import com.example.kafka.menu.MenuManager;
import com.example.kafka.manager.ProducerManager;
import com.example.kafka.manager.ConsumerManager;
import com.example.kafka.manager.StreamProcessorManager;
import com.example.kafka.config.KafkaConfig;

/**
 * 应用启动器
 * 提供更灵活的应用启动方式，支持命令行和交互式两种模式
 */
public class ApplicationLauncher {
    
    // 管理器实例
    private final MenuManager menuManager;
    private final ProducerManager producerManager;
    private final ConsumerManager consumerManager;
    private final StreamProcessorManager streamProcessorManager;
    
    public ApplicationLauncher() {
        this.menuManager = new MenuManager();
        this.producerManager = new ProducerManager();
        this.consumerManager = new ConsumerManager();
        this.streamProcessorManager = new StreamProcessorManager();
    }
    
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
        ApplicationLauncher launcher = new ApplicationLauncher();
        launcher.runInteractive();
    }
    
    /**
     * 运行交互式应用
     */
    public void runInteractive() {
        try {
            while (true) {
                menuManager.printMainMenu();
                int choice = menuManager.getUserChoice();
                
                switch (choice) {
                    case 1:
                        producerManager.runSimpleProducer();
                        break;
                    case 2:
                        consumerManager.runSimpleConsumer();
                        break;
                    case 3:
                        producerManager.runAdvancedProducer();
                        break;
                    case 4:
                        consumerManager.runAdvancedConsumer();
                        break;
                    case 5:
                        consumerManager.runMultiThreadConsumer(KafkaConfig.TOPIC_NAME, 3);
                        break;
                    case 6:
                        runStreamProcessor();
                        break;
                    case 7:
                        System.out.println("退出程序");
                        return;
                    default:
                        if (choice != -1) {
                            System.out.println("无效选择，请重新输入");
                        }
                }
            }
        } finally {
            menuManager.close();
        }
    }
    
    /**
     * 运行流处理器子菜单
     */
    private void runStreamProcessor() {
        menuManager.printStreamProcessorMenu();
        int choice = menuManager.getStreamProcessorChoice();
        
        if (choice != -1) {
            streamProcessorManager.runStreamProcessor(choice);
        }
    }
    
    /**
     * 命令行模式
     */
    private static void runCommandLineMode(String[] args) {
        ApplicationLauncher launcher = new ApplicationLauncher();
        String command = args[0];
        
        switch (command.toLowerCase()) {
            case "producer":
                launcher.runProducerMode(args);
                break;
            case "consumer":
                launcher.runConsumerMode(args);
                break;
            case "stream":
                launcher.runStreamMode(args);
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
    private void runProducerMode(String[] args) {
        
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
    private void runConsumerMode(String[] args) {
        
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
    private void runStreamMode(String[] args) {
        
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
        System.out.println("  (无参数)           - 启动交互式模式（推荐）");
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
        System.out.println("\n注意：");
        System.out.println("  - 无参数启动将进入交互式菜单模式");
        System.out.println("  - 命令行模式适合自动化和脚本使用");
        System.out.println("  - 交互式模式适合学习和测试使用");
        System.out.println("========================");
    }
}
