package com.example.kafka.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * 菜单管理器
 * 负责显示菜单和处理用户输入
 */
public class MenuManager {
    
    private static final Logger logger = LoggerFactory.getLogger(MenuManager.class);
    private final Scanner scanner;
    
    public MenuManager() {
        this.scanner = new Scanner(System.in);
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
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
                return choice;
            } else {
                System.out.println("请输入有效的数字 (1-7)");
                scanner.nextLine(); // 清除无效输入
                return -1;
            }
        } catch (Exception e) {
            logger.error("处理用户输入时发生错误", e);
            if (scanner.hasNextLine()) {
                scanner.nextLine(); // 清除错误输入
            }
            return -1;
        }
    }
    
    /**
     * 获取流处理器选择
     */
    public int getStreamProcessorChoice() {
        try {
            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine();
                return choice;
            } else {
                System.out.println("请输入有效的数字");
                scanner.nextLine();
                return -1;
            }
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
        if (scanner != null) {
            scanner.close();
        }
    }
}
