#!/bin/bash

# Kafka项目运行脚本

echo "=== Kafka 详细使用示例 ==="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 11+"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi

# 编译项目
echo "正在编译项目..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "编译失败，请检查代码"
    exit 1
fi

echo "编译成功！"

# 显示菜单
echo ""
echo "请选择要运行的程序:"
echo "1. 主应用 (交互式菜单)"
echo "2. 简单生产者"
echo "3. 简单消费者"
echo "4. 高级生产者"
echo "5. 高级消费者"
echo "6. 单词计数流处理"
echo "7. 订单处理流"
echo "8. 高级流处理器"
echo ""

read -p "请输入选择 (1-8): " choice

case $choice in
    1)
        echo "启动主应用..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.KafkaApplication"
        ;;
    2)
        echo "启动简单生产者..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.producer.SimpleProducer"
        ;;
    3)
        echo "启动简单消费者..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.consumer.SimpleConsumer"
        ;;
    4)
        echo "启动高级生产者..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.producer.AdvancedProducer"
        ;;
    5)
        echo "启动高级消费者..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.consumer.AdvancedConsumer"
        ;;
    6)
        echo "启动单词计数流处理..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.streams.WordCountStream"
        ;;
    7)
        echo "启动订单处理流..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.streams.OrderProcessingStream"
        ;;
    8)
        echo "启动高级流处理器..."
        mvn exec:java -Dexec.mainClass="com.example.kafka.streams.AdvancedStreamProcessor"
        ;;
    *)
        echo "无效选择"
        exit 1
        ;;
esac
