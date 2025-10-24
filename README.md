# Kafka 详细使用示例

这是一个完整的Java Kafka使用示例项目，展示了Kafka的各种功能和最佳实践。项目采用分层架构设计，支持交互式和命令行两种使用模式。

## 项目结构

```
kafka/
├── pom.xml                                    # Maven配置文件
├── README.md                                  # 项目说明文档
├── docker-compose.yml                         # Docker Compose配置
├── src/main/java/com/example/kafka/
│   ├── ApplicationLauncher.java               # 应用启动器（主入口）
│   ├── config/
│   │   └── KafkaConfig.java                   # Kafka配置类
│   ├── model/
│   │   ├── User.java                          # 用户实体类
│   │   └── Order.java                         # 订单实体类
│   ├── util/
│   │   ├── JsonUtil.java                      # JSON工具类
│   │   ├── InputManager.java                  # 输入管理器
│   │   └── StreamsConfigHelper.java           # 流处理配置助手
│   ├── producer/
│   │   ├── SimpleProducer.java                # 简单生产者示例
│   │   └── AdvancedProducer.java              # 高级生产者示例
│   ├── consumer/
│   │   ├── SimpleConsumer.java                # 简单消费者示例
│   │   ├── AdvancedConsumer.java              # 高级消费者示例
│   │   └── ImprovedMultiThreadConsumer.java   # 改进的多线程消费者
│   ├── streams/
│   │   ├── WordCountStream.java               # 单词计数流处理
│   │   ├── OrderProcessingStream.java         # 订单处理流
│   │   └── AdvancedStreamProcessor.java      # 高级流处理器
│   ├── menu/
│   │   └── MenuManager.java                   # 菜单管理器
│   └── manager/
│       ├── ProducerManager.java               # 生产者管理器
│       ├── ConsumerManager.java               # 消费者管理器
│       └── StreamProcessorManager.java        # 流处理器管理器
├── src/main/resources/
│   └── logback.xml                            # 日志配置
└── data/
    └── kafka-data/                            # Kafka数据持久化目录
```

## 功能特性

### 🏗️ 架构特性
- **分层架构**: 采用管理器模式，职责分离清晰
- **交互式界面**: 友好的菜单系统，支持用户交互
- **命令行支持**: 支持脚本化和自动化运行
- **模块化设计**: 各功能模块独立，便于扩展和维护

### 1. 生产者功能
- **简单生产者**: 基本的消息发送功能
- **高级生产者**: 分区、压缩、事务等高级功能
- **交互式生产者**: 支持实时输入和发送消息
- **批量发送**: 高效的批量消息发送
- **异步发送**: 非阻塞的消息发送

### 2. 消费者功能
- **简单消费者**: 基本的消息消费功能
- **高级消费者**: 多线程消费、手动提交偏移量
- **多线程消费者**: 改进的多线程消费实现
- **批量消费**: 高效的批量消息处理
- **分区消费**: 指定分区消费消息

### 3. 流处理功能
- **单词计数**: 经典的流处理示例
- **订单处理**: 复杂的业务流处理
- **高级流处理**: 包含窗口操作、流连接等高级功能
- **状态管理**: 支持流处理状态存储

### 4. 管理功能
- **菜单管理**: 统一的用户界面管理
- **生产者管理**: 统一的生产者操作管理
- **消费者管理**: 统一的消费者操作管理
- **流处理器管理**: 统一的流处理操作管理

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

#### 方式一：交互式模式（推荐）
```bash
# 启动交互式菜单
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher"
```

#### 方式二：命令行模式
```bash
# 生产者模式
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="producer simple"
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="producer advanced"
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="producer interactive"

# 消费者模式
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="consumer simple"
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="consumer advanced"

# 流处理模式
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="stream wordcount"
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="stream order"
mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher" -Dexec.args="stream advanced"
```

#### 方式三：直接运行编译后的类
```bash
# 交互式模式
java -cp target/classes com.example.kafka.ApplicationLauncher

# 命令行模式
java -cp target/classes com.example.kafka.ApplicationLauncher producer simple
java -cp target/classes com.example.kafka.ApplicationLauncher consumer advanced
java -cp target/classes com.example.kafka.ApplicationLauncher stream wordcount
```

## 详细使用说明

### 🎯 交互式模式使用

启动交互式模式后，您将看到以下菜单：

```
=== Kafka 详细使用示例 ===
1. 运行简单生产者
2. 运行简单消费者
3. 运行高级生产者
4. 运行高级消费者
5. 运行多线程消费者
6. 运行流处理器
7. 退出
==========================
```

### 生产者使用

#### 通过管理器使用
```java
// 简单生产者
ProducerManager producerManager = new ProducerManager();
producerManager.runSimpleProducer();

// 高级生产者
producerManager.runAdvancedProducer();

// 交互式生产者
producerManager.runInteractiveProducer();
```

#### 直接使用生产者类
```java
// 简单生产者
SimpleProducer producer = new SimpleProducer();
producer.sendMessage("test-topic", "key", "Hello Kafka!");
producer.close();

// 高级生产者
AdvancedProducer producer = new AdvancedProducer();
producer.sendMessageWithTimestamp("test-topic", "key", "message");
producer.close();
```

### 消费者使用

#### 通过管理器使用
```java
// 简单消费者
ConsumerManager consumerManager = new ConsumerManager();
consumerManager.runSimpleConsumer();

// 高级消费者
consumerManager.runAdvancedConsumer();

// 多线程消费者
consumerManager.runMultiThreadConsumer("test-topic", 3);
```

#### 直接使用消费者类
```java
// 简单消费者
SimpleConsumer consumer = new SimpleConsumer();
consumer.consumeMessages("test-topic");
consumer.close();

// 高级消费者
AdvancedConsumer consumer = new AdvancedConsumer();
consumer.consumeMessagesMultiThread("test-topic");
consumer.close();
```

### 流处理使用

#### 通过管理器使用
```java
// 流处理器管理器
StreamProcessorManager streamManager = new StreamProcessorManager();

// 单词计数流
streamManager.runWordCountStream();

// 订单处理流
streamManager.runOrderProcessingStream();

// 高级流处理器
streamManager.runAdvancedStreamProcessor();
```

#### 直接使用流处理器类
```java
// 单词计数流
WordCountStream wordCountStream = new WordCountStream();
wordCountStream.start();

// 订单处理流
OrderProcessingStream orderStream = new OrderProcessingStream();
orderStream.start();
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

## 架构设计

### 🏗️ 分层架构

```
┌─────────────────────────────────────┐
│           应用层 (Application)        │
├─────────────────────────────────────┤
│  ApplicationLauncher                │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│           管理层 (Manager)           │
├─────────────────────────────────────┤
│  MenuManager                        │
│  ProducerManager                    │
│  ConsumerManager                    │
│  StreamProcessorManager             │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│           业务层 (Business)          │
├─────────────────────────────────────┤
│  Producer (Simple/Advanced)         │
│  Consumer (Simple/Advanced)         │
│  StreamProcessor (WordCount/Order) │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│           配置层 (Config)            │
├─────────────────────────────────────┤
│  KafkaConfig                       │
│  JsonUtil                          │
│  InputManager                      │
└─────────────────────────────────────┘
```

### 🎯 设计模式

1. **管理器模式 (Manager Pattern)**
   - `ProducerManager` - 管理生产者相关操作
   - `ConsumerManager` - 管理消费者相关操作
   - `StreamProcessorManager` - 管理流处理器相关操作

2. **单一职责原则 (Single Responsibility Principle)**
   - 每个类只负责一个特定的功能
   - 菜单管理、生产者管理、消费者管理分离

3. **依赖注入 (Dependency Injection)**
   - 主应用类通过构造函数注入管理器实例
   - 便于测试和维护

## 最佳实践

### 1. 架构最佳实践
- 使用管理器模式统一管理相关功能
- 保持单一职责原则，每个类只负责一个功能
- 使用依赖注入提高代码的可测试性
- 合理使用日志记录，便于问题排查

### 2. 生产者最佳实践
- 使用批量发送提高吞吐量
- 配置适当的重试机制
- 使用幂等性保证消息不重复
- 合理设置缓冲区大小
- 使用交互式生产者进行实时测试

### 3. 消费者最佳实践
- 使用消费者组实现负载均衡
- 手动提交偏移量保证消息处理
- 合理设置拉取超时时间
- 处理消费异常和重试
- 使用多线程消费者提高处理能力

### 4. 流处理最佳实践
- 合理设置窗口大小
- 使用状态存储维护状态
- 处理流处理异常
- 监控流处理性能
- 使用管理器统一管理流处理器

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

## 新特性

### 🚀 交互式界面
- **友好菜单**: 直观的菜单系统，支持数字选择
- **实时交互**: 支持实时输入和发送消息
- **错误处理**: 完善的输入验证和错误提示
- **用户友好**: 清晰的操作提示和状态反馈

### 🎛️ 管理器模式
- **统一管理**: 通过管理器类统一管理相关功能
- **职责分离**: 每个管理器负责特定的功能模块
- **易于扩展**: 新增功能只需添加对应的管理器方法
- **代码复用**: 避免重复代码，提高维护性

### 🔧 命令行支持
- **脚本化运行**: 支持命令行参数，便于自动化
- **灵活配置**: 支持不同的运行模式和参数
- **批量操作**: 支持批量运行和测试
- **CI/CD集成**: 便于集成到持续集成流程

## 扩展功能

### 1. 添加新的管理器
```java
// 1. 创建新的管理器类
public class NewFeatureManager {
    public void runNewFeature() {
        // 实现新功能
    }
}

// 2. 在ApplicationLauncher中注入
private final NewFeatureManager newFeatureManager;

// 3. 在菜单中添加新选项
// 4. 在switch语句中添加新的case
```

### 2. 添加新的消息类型
- 创建新的实体类
- 添加相应的序列化器
- 更新生产者和消费者
- 在管理器中添加新的方法

### 3. 添加新的流处理逻辑
- 创建新的流处理类
- 实现业务逻辑
- 配置输入输出主题
- 在StreamProcessorManager中添加新方法

### 4. 监控和指标
- 集成JMX监控
- 添加自定义指标
- 使用Kafka监控工具
- 在管理器中添加监控功能

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 快速开始

### 🚀 一键启动（推荐）

1. **启动Kafka环境**
   ```bash
   docker-compose up -d
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **启动应用**
   ```bash
   # 交互式模式（推荐新手使用）
   mvn exec:java -Dexec.mainClass="com.example.kafka.ApplicationLauncher"
   
   # 或直接运行
   java -cp target/classes com.example.kafka.ApplicationLauncher
   ```

4. **访问Kafka UI**
   - 打开浏览器访问: http://localhost:28081
   - 查看主题、消息、消费者组等信息

### 📋 使用步骤

1. 选择菜单选项 1-6 来运行不同的功能
2. 观察控制台输出，了解Kafka的工作原理
3. 在Kafka UI中查看消息和主题信息
4. 尝试不同的生产者和消费者组合

## 联系方式

如有问题，请通过以下方式联系：
- 邮箱: example@example.com
- GitHub: https://github.com/example/kafka-demo
- 项目文档: [docs/](docs/) 目录
