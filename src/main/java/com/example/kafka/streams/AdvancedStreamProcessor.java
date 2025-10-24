package com.example.kafka.streams;

import com.example.kafka.config.KafkaConfig;
import com.example.kafka.model.Order;
import com.example.kafka.util.JsonUtil;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

/**
 * 高级流处理示例
 * 演示窗口操作、连接、聚合等高级功能
 */
public class AdvancedStreamProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(AdvancedStreamProcessor.class);
    private static final String ORDER_TOPIC = "order-topic";
    private static final String USER_TOPIC = "user-topic";
    private static final String ORDER_SUMMARY_TOPIC = "order-summary";
    private static final String USER_ORDER_JOIN_TOPIC = "user-order-join";
    
    /**
     * 启动高级流处理器
     */
    public static void start() {
        Properties props = KafkaConfig.getStreamsConfig();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "advanced-stream-processor");
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // 创建输入流
        KStream<String, String> orderStream = builder.stream(ORDER_TOPIC);
        KStream<String, String> userStream = builder.stream(USER_TOPIC);
        
        // 1. 窗口聚合 - 计算每分钟的订单总额
        KTable<Windowed<String>, Double> orderAmountByMinute = orderStream
            .mapValues(value -> extractOrderAmount(value))
            .groupByKey()
            .windowedBy(TimeWindows.of(Duration.ofMinutes(1)))
            .aggregate(
                () -> 0.0,
                (key, value, aggregate) -> aggregate + value,
                Materialized.with(Serdes.String(), Serdes.Double())
            );
        
        orderAmountByMinute.toStream()
            .map((key, value) -> new org.apache.kafka.streams.KeyValue<>(key.key(), value))
            .to(ORDER_SUMMARY_TOPIC, Produced.with(Serdes.String(), Serdes.Double()));
        
        // 2. 流连接 - 将订单和用户信息连接
        KStream<String, String> enrichedOrders = orderStream
            .selectKey((key, value) -> extractUserId(value))
            .join(
                userStream.selectKey((key, value) -> extractUserIdFromUser(value)),
                (orderValue, userValue) -> enrichOrderWithUser(orderValue, userValue),
                JoinWindows.of(Duration.ofMinutes(5))
            );
        
        enrichedOrders.to(USER_ORDER_JOIN_TOPIC, Produced.with(Serdes.String(), Serdes.String()));
        
        // 3. 分支处理 - 根据订单金额进行不同处理
        KStream<String, String>[] branches = orderStream.branch(
            (key, value) -> isHighValueOrder(value), // 高价值订单
            (key, value) -> isMediumValueOrder(value), // 中等价值订单
            (key, value) -> true // 低价值订单
        );
        
        // 处理高价值订单
        branches[0]
            .peek((key, value) -> logger.info("处理高价值订单: {}", value))
            .mapValues(value -> addProcessingFlag(value, "HIGH_VALUE"))
            .to("high-value-orders", Produced.with(Serdes.String(), Serdes.String()));
        
        // 处理中等价值订单
        branches[1]
            .peek((key, value) -> logger.info("处理中等价值订单: {}", value))
            .mapValues(value -> addProcessingFlag(value, "MEDIUM_VALUE"))
            .to("medium-value-orders", Produced.with(Serdes.String(), Serdes.String()));
        
        // 处理低价值订单
        branches[2]
            .peek((key, value) -> logger.info("处理低价值订单: {}", value))
            .mapValues(value -> addProcessingFlag(value, "LOW_VALUE"))
            .to("low-value-orders", Produced.with(Serdes.String(), Serdes.String()));
        
        // 4. 状态存储 - 维护用户订单统计
        KTable<String, Long> userOrderCount = orderStream
            .mapValues(value -> extractUserId(value))
            .groupByKey()
            .count();
        
        userOrderCount.toStream()
            .peek((key, value) -> logger.info("用户 {} 的订单数量: {}", key, value))
            .to("user-order-count", Produced.with(Serdes.String(), Serdes.Long()));
        
        // 创建KafkaStreams实例
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("正在关闭高级流处理器...");
            streams.close();
        }));
        
        try {
            streams.start();
            logger.info("高级流处理器已启动");
        } catch (Exception e) {
            logger.error("启动高级流处理器时发生错误", e);
        }
    }

    /**
     * 提取订单金额
     */
    private static Double extractOrderAmount(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            return order.getAmount();
        } catch (Exception e) {
            logger.warn("无法提取订单金额: {}", orderJson);
            return 0.0;
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
    
    /**
     * 从用户信息中提取用户ID
     */
    private static String extractUserIdFromUser(String userJson) {
        try {
            // 这里需要根据实际的用户JSON结构来解析
            // 假设用户JSON包含id字段
            return "user-id"; // 简化处理
        } catch (Exception e) {
            return "unknown";
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
     * 判断是否为中等价值订单
     */
    private static boolean isMediumValueOrder(String orderJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            return order.getAmount() > 100.0 && order.getAmount() <= 1000.0;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 为订单添加处理标记
     */
    private static String addProcessingFlag(String orderJson, String flag) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            // 这里可以添加处理标记到订单对象
            logger.info("为订单 {} 添加标记: {}", order.getOrderId(), flag);
            return orderJson;
        } catch (Exception e) {
            logger.error("添加处理标记时发生错误", e);
            return orderJson;
        }
    }
    
    /**
     * 将订单和用户信息连接
     */
    private static String enrichOrderWithUser(String orderJson, String userJson) {
        try {
            Order order = JsonUtil.fromJson(orderJson, Order.class);
            // 这里可以将用户信息添加到订单中
            logger.info("为订单 {} 添加用户信息", order.getOrderId());
            return orderJson;
        } catch (Exception e) {
            logger.error("连接订单和用户信息时发生错误", e);
            return orderJson;
        }
    }
}
