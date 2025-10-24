package com.example.kafka.util;

import org.apache.kafka.streams.StreamsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * Kafka Streams 配置助手
 * 解决常见的配置问题
 */
public class StreamsConfigHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamsConfigHelper.class);
    
    /**
     * 创建安全的 Kafka Streams 配置
     * @param baseProps 基础配置
     * @return 优化后的配置
     */
    public static Properties createSafeStreamsConfig(Properties baseProps) {
        Properties props = new Properties();
        props.putAll(baseProps);
        
        // 设置安全的状态目录
        String stateDir = createSafeStateDirectory();
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        
        // 添加安全配置
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 1);
        props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.AT_LEAST_ONCE);
        
        // 禁用可能导致问题的功能
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);
        
        logger.info("使用状态目录: {}", stateDir);
        return props;
    }
    
    /**
     * 创建安全的状态目录
     */
    private static String createSafeStateDirectory() {
        // 尝试多个目录位置
        String[] candidates = {
            System.getProperty("user.home") + File.separator + "kafka-streams-state",
            System.getProperty("java.io.tmpdir") + File.separator + "kafka-streams-state",
            System.getProperty("user.dir") + File.separator + "kafka-streams-state"
        };
        
        for (String candidate : candidates) {
            try {
                File dir = new File(candidate);
                if (!dir.exists()) {
                    boolean created = dir.mkdirs();
                    if (created) {
                        logger.info("创建状态目录: {}", candidate);
                        return candidate;
                    }
                } else {
                    logger.info("使用现有状态目录: {}", candidate);
                    return candidate;
                }
            } catch (Exception e) {
                logger.warn("无法使用目录 {}: {}", candidate, e.getMessage());
            }
        }
        
        // 如果所有目录都失败，使用临时目录
        String fallback = System.getProperty("java.io.tmpdir") + File.separator + "kafka-streams-fallback";
        logger.warn("使用备用目录: {}", fallback);
        return fallback;
    }
    
    /**
     * 清理状态目录
     */
    public static void cleanupStateDirectory(String stateDir) {
        try {
            File dir = new File(stateDir);
            if (dir.exists()) {
                deleteDirectory(dir);
                logger.info("清理状态目录: {}", stateDir);
            }
        } catch (Exception e) {
            logger.warn("清理状态目录失败: {}", e.getMessage());
        }
    }
    
    /**
     * 递归删除目录
     */
    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        dir.delete();
    }
}
