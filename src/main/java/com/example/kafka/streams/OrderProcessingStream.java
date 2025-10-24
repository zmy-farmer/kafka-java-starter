package com.example.kafka.streams;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.Order;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * 订单处理流示例
 * 演示复杂的流处理业务逻辑
 */
public class OrderProcessingStream {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingStream.class);
    private static final String ORDER_TOPIC = "order-topic";
    private static final String HIGH_VALUE_ORDERS_TOPIC = "order-summary-topic";
    private static final String ORDER_STATS_TOPIC = "enriched-order-topic";
    
    /**
     * 启动订单处理流处理器
     */
    public static void start() {
        Properties props = KafkaConfig.getStreamsConfig();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "order-processing-app");
        
        // 设置状态目录为用户主目录，避免权限问题
        String userHome = System.getProperty("user.home");
        String stateDir = userHome + File.separator + "kafka-streams-state";
        props.put(StreamsConfig.STATE_DIR_CONFIG, stateDir);
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // 创建订单输入流
        KStream<String, String> orderStream = builder.stream(ORDER_TOPIC);
        
        // 处理订单流 - 验证和过滤
        KStream<String, String> validOrders = orderStream
            .peek((key, value) -> logger.info("收到订单: {}", value))
            .filter((key, value) -> isValidOrder(value))
            .mapValues(value -> processOrder(value))
            .filter((key, value) -> value != null);
        
        // 分支处理：高价值订单和普通订单
        KStream<String, String>[] branches = validOrders.branch(
            (key, value) -> isHighValueOrder(value), // 高价值订单
            (key, value) -> true // 普通订单
        );
        
        KStream<String, String> highValueOrders = branches[0];
        KStream<String, String> normalOrders = branches[1];
        
        // 高价值订单特殊处理
        highValueOrders
            .mapValues(value -> addHighValueFlag(value))
            .to(HIGH_VALUE_ORDERS_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
        
        // 普通订单处理
        normalOrders
            .to("normal-orders", Produced.with(Serdes.String(), Serdes.String()));
        
        // 订单统计 - 使用所有有效订单
        KTable<String, Long> orderCounts = validOrders
            .mapValues(value -> extractUserId(value))
            .groupBy((key, value) -> value)
            .count();
        
        orderCounts.toStream().to(ORDER_STATS_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
        
        // 创建KafkaStreams实例
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("正在关闭订单处理流...");
            streams.close();
        }));
        
        try {
            streams.start();
            logger.info("订单处理流应用已启动");
        } catch (Exception e) {
            logger.error("启动订单处理流时发生错误", e);
        }
    }

    
    /**
     * 验证订单是否有效
     */
    private static boolean isValidOrder(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            return order != null && order.getAmount() > 0;
        } catch (Exception e) {
            logger.warn("无效的订单格式: {}", orderJson);
            return false;
        }
    }
    
    /**
     * 处理订单
     */
    private static String processOrder(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            logger.info("处理订单: {}", order.getOrderId());
            
            // 这里可以添加业务逻辑
            // 例如：库存检查、支付验证等
            
            return orderJson;
        } catch (Exception e) {
            logger.error("处理订单时发生错误", e);
            return null;
        }
    }
    
    /**
     * 判断是否为高价值订单
     */
    private static boolean isHighValueOrder(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            return order.getAmount() > 1000.0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 为高价值订单添加标记
     */
    private static String addHighValueFlag(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            // 这里可以添加高价值订单的特殊处理逻辑
            logger.info("高价值订单: {}", order.getOrderId());
            return orderJson;
        } catch (Exception e) {
            logger.error("处理高价值订单时发生错误", e);
            return orderJson;
        }
    }
    
    /**
     * 提取用户ID
     */
    private static String extractUserId(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            return order.getUserId();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
