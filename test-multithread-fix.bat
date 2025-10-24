@echo off
echo 测试多线程消费者修复...

echo 编译项目...
call mvn clean compile

if %errorlevel% neq 0 (
    echo Maven编译失败
    pause
    exit /b %errorlevel%
)

echo.
echo 启动Kafka应用测试多线程消费者...
echo 请选择选项5（多线程消费者）进行测试
echo 按Enter键停止消费者，然后应该返回主菜单
echo.

java -cp target/classes com.example.kafka.KafkaApplication

pause
