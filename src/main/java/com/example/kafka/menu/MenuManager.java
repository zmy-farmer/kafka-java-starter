package com.example.kafka.menu;

import com.example.kafka.util.InputManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 菜单管理器
 * 负责显示菜单和处理用户输入
 */
public class MenuManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuManager.class);
    
    public MenuManager() {
        // 使用全局输入管理器，不需要初始化
    }
    
    /**
     * 显示主菜单
     */
    public void printMainMenu() {
        System.out.println("\n=== Kafka 详细使用示例 ===");
        System.out.println("1. 运行简单生产者");
        System.out.println("2. 运行简单消费者");
        System.out.println("3. 运行高级生产者");
        System.out.println("4. 运行高级消费者");
        System.out.println("5. 运行多线程消费者");
        System.out.println("6. 运行流处理器");
        System.out.println("7. 退出");
        System.out.println("==========================");
    }
    
    /**
     * 显示流处理器子菜单
     */
    public void printStreamProcessorMenu() {
        System.out.println("\n启动流处理器...");
        System.out.println("选择流处理类型:");
        System.out.println("1. 单词计数流处理 (WordCount)");
        System.out.println("2. 订单处理流处理 (OrderProcessing)");
        System.out.println("3. 高级流处理器 (Advanced)");
        System.out.print("请选择 (1-3): ");
    }
    
    /**
     * 获取用户选择
     */
    public int getUserChoice() {
        System.out.print("请选择操作 (1-7): ");
        
        try {
            int choice = InputManager.readInt();
            if (choice == -1) {
                System.out.println("请输入有效的数字 (1-7)");
            }
            return choice;
        } catch (Exception e) {
            logger.error("处理用户输入时发生错误", e);
            return -1;
        }
    }
    
    /**
     * 获取流处理器选择
     */
    public int getStreamProcessorChoice() {
        try {
            int choice = InputManager.readInt();
            if (choice == -1) {
                System.out.println("请输入有效的数字");
            }
            return choice;
        } catch (Exception e) {
            logger.error("运行流处理器时发生错误", e);
            return -1;
        }
    }
    
    /**
     * 等待用户按Enter键
     */
    public void waitForEnter() {
        try {
            System.in.read();
        } catch (Exception e) {
            // 忽略异常
        }
    }
    
    /**
     * 关闭资源
     */
    public void close() {
        // 使用全局输入管理器，不需要关闭
        // InputManager.close(); // 如果需要关闭，可以调用这个方法
    }
}
