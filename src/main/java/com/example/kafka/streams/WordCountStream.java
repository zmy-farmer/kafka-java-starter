package com.example.kafka.streams;

import com.example.kafka.config.KafkaConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka Streams 单词计数示例
 * 演示基本的流处理功能
 */
public class WordCountStream {
    
    private static final Logger logger = LoggerFactory.getLogger(WordCountStream.class);
    private static final String INPUT_TOPIC = "streams-plaintext-input";
    private static final String OUTPUT_TOPIC = "streams-wordcount-output";
    
    /**
     * 启动单词计数流处理器
     */
    public static void start() {
        Properties props = KafkaConfig.getStreamsConfig();
        
        StreamsBuilder builder = new StreamsBuilder();
        
        // 创建输入流
        KStream<String, String> source = builder.stream(INPUT_TOPIC);
        
        // 单词计数处理
        KTable<String, Long> wordCounts = source
            .flatMapValues(value -> java.util.Arrays.asList(value.toLowerCase().split("\\W+")))
            .groupBy((key, value) -> value)
            .count();
        
        // 输出结果
        wordCounts.toStream().to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.Long()));
        
        // 创建KafkaStreams实例
        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        
        // 添加关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("正在关闭Kafka Streams...");
            streams.close();
        }));
        
        try {
            streams.start();
            logger.info("Kafka Streams 单词计数应用已启动");
        } catch (Exception e) {
            logger.error("启动Kafka Streams时发生错误", e);
        }
    }

}
