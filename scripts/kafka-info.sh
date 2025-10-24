#!/bin/bash

# Kafka配置信息脚本

echo "=== Kafka 3.9.0 KRaft模式配置信息 ==="
echo ""

echo "镜像信息："
echo "- 镜像: apache/kafka:3.9.0"
echo "- 模式: KRaft (无需Zookeeper)"
echo "- 容器名: sx-kafka"
echo ""

echo "端口配置："
echo "- Kafka客户端: localhost:29092"
echo "- 控制器端口: localhost:29093"
echo "- Kafka UI: http://localhost:28081"
echo ""

echo "数据持久化："
echo "- 数据目录: ./data/kafka-data/"
echo "- 权限: root用户"
echo ""

echo "主要特性："
echo "1. 完全KRaft模式，无需Zookeeper"
echo "2. 自动创建主题功能"
echo "3. 数据持久化存储"
echo "4. 多监听器配置 (PLAINTEXT, CONTROLLER, INTERNAL)"
echo "5. 事务日志配置"
echo ""

echo "启动命令："
echo "docker-compose up -d"
echo "或"
echo "./scripts/start-kafka.sh"
echo ""

echo "验证命令："
echo "docker exec sx-kafka-test kafka-topics.sh --list --bootstrap-server localhost:29092"
echo ""

echo "停止命令："
echo "docker-compose down"
echo "或"
echo "./scripts/stop-kafka.sh"
