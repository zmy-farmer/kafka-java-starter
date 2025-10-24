@echo off
echo === Kafka 详细使用示例 ===

REM 检查Java环境
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装Java 11+
    pause
    exit /b 1
)

REM 检查Maven环境
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven
    pause
    exit /b 1
)

REM 编译项目
echo 正在编译项目...
mvn clean compile

if %errorlevel% neq 0 (
    echo 编译失败，请检查代码
    pause
    exit /b 1
)

echo 编译成功！

REM 显示菜单
echo.
echo 请选择要运行的程序:
echo 1. 主应用 (交互式菜单)
echo 2. 简单生产者
echo 3. 简单消费者
echo 4. 高级生产者
echo 5. 高级消费者
echo 6. 单词计数流处理
echo 7. 订单处理流
echo 8. 高级流处理器
echo.

set /p choice=请输入选择 (1-8): 

if "%choice%"=="1" (
    echo 启动主应用...
    mvn exec:java -Dexec.mainClass="com.example.kafka.KafkaApplication"
) else if "%choice%"=="2" (
    echo 启动简单生产者...
    mvn exec:java -Dexec.mainClass="com.example.kafka.producer.SimpleProducer"
) else if "%choice%"=="3" (
    echo 启动简单消费者...
    mvn exec:java -Dexec.mainClass="com.example.kafka.consumer.SimpleConsumer"
) else if "%choice%"=="4" (
    echo 启动高级生产者...
    mvn exec:java -Dexec.mainClass="com.example.kafka.producer.AdvancedProducer"
) else if "%choice%"=="5" (
    echo 启动高级消费者...
    mvn exec:java -Dexec.mainClass="com.example.kafka.consumer.AdvancedConsumer"
) else if "%choice%"=="6" (
    echo 启动单词计数流处理...
    mvn exec:java -Dexec.mainClass="com.example.kafka.streams.WordCountStream"
) else if "%choice%"=="7" (
    echo 启动订单处理流...
    mvn exec:java -Dexec.mainClass="com.example.kafka.streams.OrderProcessingStream"
) else if "%choice%"=="8" (
    echo 启动高级流处理器...
    mvn exec:java -Dexec.mainClass="com.example.kafka.streams.AdvancedStreamProcessor"
) else (
    echo 无效选择
    pause
    exit /b 1
)

pause
