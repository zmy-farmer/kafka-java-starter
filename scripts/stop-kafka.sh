#!/bin/bash

# Kafka停止脚本

echo "停止Kafka环境 (KRaft模式)..."

# 停止服务
docker-compose down

echo "Kafka环境已停止"
echo "数据目录: ./data/kafka-data/ (如需完全清理，可删除此目录)"
