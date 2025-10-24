# Kafka 项目结构文档

## 📁 项目结构

```
kafka/
├── src/main/java/com/example/kafka/
│   ├── KafkaApplication.java          # 主应用类（重构后）
│   ├── ApplicationLauncher.java       # 应用启动器
│   ├── config/
│   │   └── KafkaConfig.java           # Kafka配置
│   ├── model/
│   │   ├── User.java                  # 用户模型
│   │   └── Order.java                 # 订单模型
│   ├── producer/
│   │   ├── SimpleProducer.java        # 简单生产者
│   │   └── AdvancedProducer.java      # 高级生产者
│   ├── consumer/
│   │   ├── SimpleConsumer.java        # 简单消费者
│   │   └── AdvancedConsumer.java      # 高级消费者
│   ├── streams/
│   │   ├── WordCountStream.java       # 单词计数流处理器
│   │   ├── OrderProcessingStream.java # 订单处理流处理器
│   │   └── AdvancedStreamProcessor.java # 高级流处理器
│   ├── menu/
│   │   └── MenuManager.java           # 菜单管理器
│   ├── manager/
│   │   ├── ProducerManager.java       # 生产者管理器
│   │   ├── ConsumerManager.java        # 消费者管理器
│   │   └── StreamProcessorManager.java # 流处理器管理器
│   └── util/
│       └── JsonUtil.java              # JSON工具类
├── src/main/resources/
│   └── logback.xml                    # 日志配置
├── scripts/
│   ├── start-kafka.sh                # 启动Kafka脚本
│   ├── stop-kafka.sh                 # 停止Kafka脚本
│   └── create-topics.bat              # 创建主题脚本
├── docs/
│   ├── project-structure.md          # 项目结构文档
│   └── kraft-vs-zookeeper.md         # KRaft vs Zookeeper文档
├── docker-compose.yml                 # Docker Compose配置
├── pom.xml                           # Maven配置
├── README.md                         # 项目说明
├── run-refactored.bat                # 重构后运行脚本
└── .gitignore                        # Git忽略文件
```

## 🏗️ 架构设计

### **分层架构**

```
┌─────────────────────────────────────┐
│           应用层 (Application)        │
├─────────────────────────────────────┤
│  KafkaApplication                   │
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
│  StreamProcessor (WordCount/Order)  │
└─────────────────────────────────────┘
┌─────────────────────────────────────┐
│           配置层 (Config)            │
├─────────────────────────────────────┤
│  KafkaConfig                       │
│  JsonUtil                          │
└─────────────────────────────────────┘
```

### **设计模式**

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

## 🚀 使用方式

### **1. 交互式模式**
```bash
java -cp target/classes com.example.kafka.KafkaApplication
```

### **2. 命令行模式**
```bash
# 生产者模式
java -cp target/classes com.example.kafka.ApplicationLauncher producer simple
java -cp target/classes com.example.kafka.ApplicationLauncher producer advanced
java -cp target/classes com.example.kafka.ApplicationLauncher producer interactive

# 消费者模式
java -cp target/classes com.example.kafka.ApplicationLauncher consumer simple
java -cp target/classes com.example.kafka.ApplicationLauncher consumer advanced

# 流处理模式
java -cp target/classes com.example.kafka.ApplicationLauncher stream wordcount
java -cp target/classes com.example.kafka.ApplicationLauncher stream order
java -cp target/classes com.example.kafka.ApplicationLauncher stream advanced
```

### **3. 使用脚本**
```bash
# Windows
run-refactored.bat

# Linux/Mac
./run-refactored.sh
```

## 📋 功能模块

### **MenuManager**
- 显示各种菜单
- 处理用户输入
- 输入验证和错误处理

### **ProducerManager**
- 简单生产者操作
- 高级生产者操作
- 交互式生产者操作

### **ConsumerManager**
- 简单消费者操作
- 高级消费者操作
- 多主题消费者操作

### **StreamProcessorManager**
- 单词计数流处理
- 订单处理流处理
- 高级流处理

## 🔧 扩展性

### **添加新的管理器**
1. 在`manager`包下创建新的管理器类
2. 在主应用类中注入新的管理器
3. 在菜单中添加新的选项

### **添加新的功能**
1. 在相应的管理器类中添加新方法
2. 更新菜单选项
3. 添加命令行参数支持

## 📝 维护建议

1. **保持单一职责**：每个类只负责一个功能
2. **使用依赖注入**：便于测试和扩展
3. **错误处理**：每个管理器都应该有适当的错误处理
4. **日志记录**：使用SLF4J进行日志记录
5. **文档更新**：及时更新项目结构文档
