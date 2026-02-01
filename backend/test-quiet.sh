#!/bin/bash
# 安静模式运行Maven测试 - 只显示关键信息和错误

echo "开始运行测试（安静模式）..."
echo

# 运行测试并过滤输出
mvn test -q -Dspring.profiles.active=test -Dmaven.test.failure.ignore=false 2>&1 | grep -E "(Tests run:|FAILURE|ERROR|BUILD SUCCESS|BUILD FAILURE|Failed tests:|Errors:|Skipped:)"

echo
echo "测试完成。如需查看详细日志，请查看 target/surefire-reports/ 目录"