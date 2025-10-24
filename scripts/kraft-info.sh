#!/bin/bash

# KRaft模式信息脚本

echo "=== Kafka KRaft模式信息 ==="
echo ""

echo "KRaft (Kafka Raft) 是Apache Kafka的新元数据管理方式"
echo "从Kafka 2.8.0开始引入，在Kafka 3.0+版本中成为推荐模式"
echo ""

echo "主要优势："
echo "1. 简化架构 - 不再需要Zookeeper"
echo "2. 更好的性能 - 元数据操作更快"
echo "3. 简化运维 - 减少组件依赖"
echo "4. 更好的扩展性 - 支持更大集群"
echo ""

echo "主要变化："
echo "- 使用Raft协议进行元数据管理"
echo "- 控制器节点负责元数据管理"
echo "- 不再需要Zookeeper连接配置"
echo "- 使用kafka-storage.sh格式化存储目录"
echo ""

echo "当前项目配置："
echo "- Kafka版本: 3.6.0"
echo "- 模式: KRaft (无需Zookeeper)"
echo "- 集群ID: MkU3OEVBNTcwNTJENDM2Qk"
echo "- 控制器端口: 9093"
echo "- 客户端端口: 9092"
echo ""

echo "启动命令："
echo "docker-compose up -d"
echo "或"
echo "./scripts/start-kafka.sh"
echo ""

echo "验证KRaft模式："
echo "docker exec kafka kafka-broker-api-versions.sh --bootstrap-server localhost:9092"
