package com.example.kafka.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 全局输入管理器
 * 避免多个输入流冲突
 */
public class InputManager {
    
    private static final Logger logger = LoggerFactory.getLogger(InputManager.class);
    private static BufferedReader reader;
    
    static {
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            logger.error("初始化输入流失败", e);
        }
    }
    
    /**
     * 读取一行输入
     */
    public static String readLine() {
        try {
            if (reader == null) {
                recreateReader();
            }
            return reader.readLine();
        } catch (IOException e) {
            logger.error("读取输入时发生错误", e);
            recreateReader();
            return null;
        }
    }
    
    /**
     * 读取整数输入
     */
    public static int readInt() {
        try {
            String input = readLine();
            if (input == null || input.trim().isEmpty()) {
                return -1;
            }
            return Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            logger.warn("无效的数字输入: {}", e.getMessage());
            return -1;
        }
    }
    
    /**
     * 重新创建输入流
     */
    private static void recreateReader() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            // 忽略关闭异常
        }
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            logger.error("重新创建输入流失败", e);
        }
    }
    
    /**
     * 关闭输入流
     */
    public static void close() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            logger.warn("关闭输入流时发生错误", e);
        }
    }
}
