package com.example.kafka.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流处理器管理器
 * 负责管理不同类型的流处理器
 */
public class StreamProcessorManager {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamProcessorManager.class);
    
    /**
     * 运行单词计数流处理器
     */
    public void runWordCountStream() {
        System.out.println("\n启动单词计数流处理器...");
        System.out.println("按 Enter 键停止流处理器");
        
        try {
            com.example.kafka.streams.WordCountStream.main(new String[]{});
        } catch (Exception e) {
            logger.error("启动单词计数流处理器时发生错误", e);
        }
        
        // 等待用户按Enter键
        try {
            System.in.read();
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 运行订单处理流处理器
     */
    public void runOrderProcessingStream() {
        System.out.println("\n启动订单处理流处理器...");
        System.out.println("按 Enter 键停止流处理器");
        
        try {
            com.example.kafka.streams.OrderProcessingStream.main(new String[]{});
        } catch (Exception e) {
            logger.error("启动订单处理流处理器时发生错误", e);
        }
        
        // 等待用户按Enter键
        try {
            System.in.read();
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 运行高级流处理器
     */
    public void runAdvancedStreamProcessor() {
        System.out.println("\n启动高级流处理器...");
        System.out.println("按 Enter 键停止流处理器");
        
        try {
            com.example.kafka.streams.AdvancedStreamProcessor.main(new String[]{});
        } catch (Exception e) {
            logger.error("启动高级流处理器时发生错误", e);
        }
        
        // 等待用户按Enter键
        try {
            System.in.read();
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 运行指定的流处理器
     */
    public void runStreamProcessor(int choice) {
        switch (choice) {
            case 1:
                runWordCountStream();
                break;
            case 2:
                runOrderProcessingStream();
                break;
            case 3:
                runAdvancedStreamProcessor();
                break;
            default:
                System.out.println("无效选择");
        }
    }
    
    /**
     * 显示流处理器状态
     */
    public void showStreamProcessorStatus() {
        System.out.println("\n=== 流处理器状态 ===");
        System.out.println("1. 单词计数流处理器 - 处理文本数据，统计单词频率");
        System.out.println("2. 订单处理流处理器 - 处理订单数据，分类和统计");
        System.out.println("3. 高级流处理器 - 复杂的数据流处理，包含窗口和连接操作");
        System.out.println("==================");
    }
    
    /**
     * 显示流处理器使用说明
     */
    public void showStreamProcessorUsage() {
        System.out.println("\n=== 流处理器使用说明 ===");
        System.out.println("1. 确保Kafka集群正在运行");
        System.out.println("2. 确保相关主题已创建");
        System.out.println("3. 向输入主题发送数据");
        System.out.println("4. 查看输出主题的处理结果");
        System.out.println("5. 按Enter键停止流处理器");
        System.out.println("========================");
    }
}
