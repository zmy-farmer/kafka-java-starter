package com.example.kafka;

import com.example.kafka.menu.MenuManager;
import com.example.kafka.manager.ProducerManager;
import com.example.kafka.manager.ConsumerManager;
import com.example.kafka.manager.StreamProcessorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Kafka应用主类
 * 提供交互式菜单来运行不同的Kafka示例
 * 重构后的版本，使用管理器模式分离关注点
 */
public class KafkaApplication {
    
    // 管理器实例
    private final MenuManager menuManager;
    private final ProducerManager producerManager;
    private final ConsumerManager consumerManager;
    private final StreamProcessorManager streamProcessorManager;
    
    public KafkaApplication() {
        this.menuManager = new MenuManager();
        this.producerManager = new ProducerManager();
        this.consumerManager = new ConsumerManager();
        this.streamProcessorManager = new StreamProcessorManager();
    }
    
    public static void main(String[] args) {
        KafkaApplication app = new KafkaApplication();
        app.run();
    }
    
    /**
     * 运行主应用
     */
    public void run() {
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
                        consumerManager.runMultiThreadConsumer("test-topic", 3);
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
}
