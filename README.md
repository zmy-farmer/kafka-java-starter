# Kafka 详细使用示例

这是一个完整的Java Kafka使用示例项目，展示了Kafka的各种功能和最佳实践。

## 项目结构

```
kafka/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
└── src/main/java/com/example/kafka/
    ├── KafkaApplication.java                  # 主应用类
    ├── config/
    │   └── KafkaConfig.java                   # Kafka配置类
    ├── model/
    │   ├── User.java                          # 用户实体类
    │   └── Order.java                         # 订单实体类
    ├── util/
    │   └── JsonUtil.java                      # JSON工具类
    ├── producer/
    │   ├── SimpleProducer.java                # 简单生产者示例
    │   └── AdvancedProducer.java              # 高级生产者示例
    ├── consumer/
    │   ├── SimpleConsumer.java                # 简单消费者示例
    │   └── AdvancedConsumer.java              # 高级消费者示例
    └── streams/
        ├── WordCountStream.java               # 单词计数流处理
        ├── OrderProcessingStream.java         # 订单处理流
        └── AdvancedStreamProcessor.java       # 高级流处理器
```

## 功能特性

### 1. 生产者功能
- **简单生产者**: 基本的消息发送功能
- **高级生产者**: 分区、压缩、事务等高级功能
- **批量发送**: 高效的批量消息发送
- **异步发送**: 非阻塞的消息发送

### 2. 消费者功能
- **简单消费者**: 基本的消息消费功能
- **高级消费者**: 多线程消费、手动提交偏移量
- **批量消费**: 高效的批量消息处理
- **分区消费**: 指定分区消费消息

### 3. 流处理功能
- **单词计数**: 经典的流处理示例
- **订单处理**: 复杂的业务流处理
- **窗口操作**: 时间窗口聚合
- **流连接**: 多流数据连接

## 环境要求

- Java 11+
- Maven 3.6+
- Apache Kafka 3.6+ (使用KRaft模式，无需Zookeeper)
- Docker (可选，用于快速启动Kafka环境)

## 快速开始

### 1. 安装和启动Kafka

#### 方式一：使用Docker (推荐)
```bash
# 启动Kafka (KRaft模式，无需Zookeeper)
docker-compose up -d

# 或使用脚本
./scripts/start-kafka.sh
```

**新配置特点：**
- 使用 `apache/kafka:3.9.0` 镜像
- 完全KRaft模式，无需Zookeeper
- 自动创建主题功能
- 数据持久化到 `./data/kafka-data/`
- Kafka服务器: localhost:29092
- Kafka UI: http://localhost:28081

#### 方式二：本地安装
```bash
# 下载Kafka 3.6.0
wget https://downloads.apache.org/kafka/3.6.0/kafka_2.13-3.6.0.tgz
tar -xzf kafka_2.13-3.6.0.tgz
cd kafka_2.13-3.6.0

# 格式化存储目录 (仅首次运行)
bin/kafka-storage.sh format -t MkU3OEVBNTcwNTJENDM2Qk -c config/kraft/server.properties

# 启动Kafka (KRaft模式)
bin/kafka-server-start.sh config/kraft/server.properties
```

**注意**: 新版本Kafka使用KRaft模式，不再需要Zookeeper！

### 关于KRaft模式

KRaft (Kafka Raft) 是Apache Kafka的新元数据管理方式，从Kafka 2.8.0开始引入，在Kafka 3.0+版本中成为推荐模式。

#### KRaft模式的优势：
- **简化架构**: 不再需要Zookeeper，减少组件依赖
- **更好的性能**: 元数据操作更快，启动时间更短
- **简化运维**: 减少一个需要维护的组件
- **更好的扩展性**: 支持更大的集群规模

#### 主要变化：
- 使用Raft协议进行元数据管理
- 控制器节点负责元数据管理
- 不再需要Zookeeper连接配置
- 使用`kafka-storage.sh`格式化存储目录

### 2. 编译项目

```bash
mvn clean compile
```

### 3. 运行示例

#### 运行主应用
```bash
mvn exec:java -Dexec.mainClass="com.example.kafka.KafkaApplication"
```

#### 单独运行生产者
```bash
mvn exec:java -Dexec.mainClass="com.example.kafka.producer.SimpleProducer"
```

#### 单独运行消费者
```bash
mvn exec:java -Dexec.mainClass="com.example.kafka.consumer.SimpleConsumer"
```

#### 运行流处理
```bash
mvn exec:java -Dexec.mainClass="com.example.kafka.streams.WordCountStream"
```

## 详细使用说明

### 生产者使用

#### 简单生产者
```java
SimpleProducer producer = new SimpleProducer();

// 发送简单消息
producer.sendMessage("test-topic", "key", "Hello Kafka!");

// 发送用户对象
User user = new User("1", "张三", "zhangsan@example.com", 25);
producer.sendUserMessage("user-topic", "user-1", user);

// 批量发送
producer.sendBatchMessages("test-topic", 100);

producer.close();
```

#### 高级生产者
```java
AdvancedProducer producer = new AdvancedProducer();

// 发送到指定分区
producer.sendToPartition("test-topic", 0, "key", "message");

// 发送订单消息
Order order = new Order("ORDER-001", "USER-001", "笔记本电脑", 5999.99);
producer.sendOrderMessage("order-topic", order);

// 发送带时间戳的消息
producer.sendMessageWithTimestamp("test-topic", "key", "message");

producer.close();
```

### 消费者使用

#### 简单消费者
```java
SimpleConsumer consumer = new SimpleConsumer();

// 消费消息
consumer.consumeMessages("test-topic");

// 从指定偏移量消费
consumer.consumeFromOffset("test-topic", 0, 100);

consumer.close();
```

#### 高级消费者
```java
AdvancedConsumer consumer = new AdvancedConsumer();

// 多线程消费
consumer.consumeMessagesMultiThread("test-topic");

// 批量消费
consumer.consumeBatchMessages("test-topic", 100);

// 消费指定分区
consumer.consumeFromPartition("test-topic", 0);

consumer.close();
```

### 流处理使用

#### 单词计数流
```java
// 启动单词计数流处理
java -cp target/kafka-demo-1.0.0.jar com.example.kafka.streams.WordCountStream
```

#### 订单处理流
```java
// 启动订单处理流
java -cp target/kafka-demo-1.0.0.jar com.example.kafka.streams.OrderProcessingStream
```

## 配置说明

### 生产者配置
- `acks`: 确认机制 (all, 1, 0)
- `retries`: 重试次数
- `batch.size`: 批量大小
- `linger.ms`: 等待时间
- `compression.type`: 压缩类型

### 消费者配置
- `group.id`: 消费者组ID
- `auto.offset.reset`: 偏移量重置策略
- `enable.auto.commit`: 自动提交偏移量
- `max.poll.records`: 每次拉取的最大记录数

### 流处理配置
- `application.id`: 应用ID
- `bootstrap.servers`: Kafka服务器地址
- `default.key.serde`: 默认键序列化器
- `default.value.serde`: 默认值序列化器

## 最佳实践

### 1. 生产者最佳实践
- 使用批量发送提高吞吐量
- 配置适当的重试机制
- 使用幂等性保证消息不重复
- 合理设置缓冲区大小

### 2. 消费者最佳实践
- 使用消费者组实现负载均衡
- 手动提交偏移量保证消息处理
- 合理设置拉取超时时间
- 处理消费异常和重试

### 3. 流处理最佳实践
- 合理设置窗口大小
- 使用状态存储维护状态
- 处理流处理异常
- 监控流处理性能

## 故障排除

### 常见问题

1. **连接失败**
   - 检查Kafka服务器是否启动
   - 验证bootstrap.servers配置
   - 检查网络连接

2. **消息发送失败**
   - 检查主题是否存在
   - 验证序列化器配置
   - 检查权限设置

3. **消费不到消息**
   - 检查消费者组配置
   - 验证偏移量设置
   - 检查主题分区

### 日志配置

项目使用Logback作为日志框架，可以通过修改`src/main/resources/logback.xml`来调整日志级别和输出格式。

## KRaft模式说明

本项目使用Kafka 3.6.0的KRaft模式，这是Apache Kafka的新架构，不再依赖Zookeeper。

### KRaft模式的优势：
- **简化架构**: 不再需要Zookeeper，减少组件依赖
- **更好性能**: 元数据操作更快，启动时间更短
- **简化运维**: 减少一个需要维护的组件
- **更好扩展性**: 支持更大的集群规模

### 详细对比文档：
查看 [docs/kraft-vs-zookeeper.md](docs/kraft-vs-zookeeper.md) 了解KRaft与Zookeeper模式的详细对比。

## 扩展功能

### 1. 添加新的消息类型
- 创建新的实体类
- 添加相应的序列化器
- 更新生产者和消费者

### 2. 添加新的流处理逻辑
- 创建新的流处理类
- 实现业务逻辑
- 配置输入输出主题

### 3. 监控和指标
- 集成JMX监控
- 添加自定义指标
- 使用Kafka监控工具

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 联系方式

如有问题，请通过以下方式联系：
- 邮箱: example@example.com
- GitHub: https://github.com/example/kafka-demo
