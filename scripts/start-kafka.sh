#!/bin/bash

# Kafka启动脚本 (KRaft模式)

echo "启动Kafka环境 (KRaft模式)..."

# 检查Docker是否运行
if ! docker info > /dev/null 2>&1; then
    echo "错误: Docker未运行，请先启动Docker"
    exit 1
fi

# 创建数据目录
echo "创建数据目录..."
mkdir -p ./data/kafka-data

# 启动Kafka服务
echo "启动Kafka (KRaft模式)..."
docker-compose up -d

# 等待服务启动
echo "等待Kafka服务启动..."
sleep 20

# 检查服务状态
echo "检查服务状态..."
docker-compose ps

# 等待Kafka完全启动
echo "等待Kafka完全启动..."
sleep 10

# 创建主题
echo "创建测试主题..."
docker exec sx-kafka-test kafka-topics.sh --create --topic test-topic --bootstrap-server localhost:29092 --partitions 3 --replication-factor 1
docker exec sx-kafka-test kafka-topics.sh --create --topic user-topic --bootstrap-server localhost:29092 --partitions 3 --replication-factor 1
docker exec sx-kafka-test kafka-topics.sh --create --topic order-topic --bootstrap-server localhost:29092 --partitions 3 --replication-factor 1
docker exec sx-kafka-test kafka-topics.sh --create --topic streams-plaintext-input --bootstrap-server localhost:29092 --partitions 3 --replication-factor 1
docker exec sx-kafka-test kafka-topics.sh --create --topic streams-wordcount-output --bootstrap-server localhost:29092 --partitions 3 --replication-factor 1

# 列出主题
echo "已创建的主题:"
docker exec sx-kafka-test kafka-topics.sh --list --bootstrap-server localhost:29092

echo "Kafka环境启动完成 (KRaft模式)!"
echo "Kafka UI: http://localhost:28081"
echo "Kafka服务器: localhost:29092"
echo "注意: 使用KRaft模式，无需Zookeeper"
