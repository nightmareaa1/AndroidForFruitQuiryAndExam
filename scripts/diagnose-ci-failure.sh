#!/bin/bash
# CI失败自动诊断脚本
# 用于快速收集和分析构建失败信息

set -e

echo "========================================"
echo "CI 失败自动诊断工具"
echo "========================================"
echo

TIMESTAMP=$(date +"%Y-%m-%d_%H-%M-%S")
REPORT_DIR="ci-diagnosis-$TIMESTAMP"

echo "创建诊断报告目录: $REPORT_DIR"
mkdir -p "$REPORT_DIR"

echo
echo "[1/7] 收集系统信息..."
{
    echo "=== 系统信息 ==="
    echo "时间: $(date)"
    echo "操作系统: $(uname -a)"
    echo
    echo "Java版本:"
    java -version 2>&1
    echo
    echo "Maven版本:"
    mvn -version 2>&1
    echo
    echo "Docker版本:"
    docker --version 2>&1 || echo "Docker未安装或不可用"
} > "$REPORT_DIR/system-info.txt"

echo "[2/7] 检查环境变量..."
{
    echo "=== 环境变量 ==="
    echo "JAVA_HOME=$JAVA_HOME"
    echo "MAVEN_HOME=$MAVEN_HOME"
    echo "PATH=$PATH"
    echo
    echo "=== 磁盘空间 ==="
    df -h
    echo
    echo "=== 内存使用 ==="
    free -h 2>/dev/null || echo "内存信息不可用"
} > "$REPORT_DIR/environment.txt"

echo "[3/7] 分析Maven依赖..."
cd backend
mvn dependency:tree > "../$REPORT_DIR/dependency-tree.txt" 2>&1 || echo "依赖树生成失败" > "../$REPORT_DIR/dependency-tree.txt"
mvn dependency:analyze > "../$REPORT_DIR/dependency-analysis.txt" 2>&1 || echo "依赖分析失败" > "../$REPORT_DIR/dependency-analysis.txt"

echo "[4/7] 收集最近的测试报告..."
if [ -d "target/surefire-reports" ]; then
    cp -r target/surefire-reports "../$REPORT_DIR/" 2>/dev/null || echo "复制surefire报告失败"
fi
if [ -d "target/failsafe-reports" ]; then
    cp -r target/failsafe-reports "../$REPORT_DIR/" 2>/dev/null || echo "复制failsafe报告失败"
fi

echo "[5/7] 检查Docker状态..."
cd ..
{
    echo "=== Docker 状态 ==="
    docker ps 2>&1 || echo "Docker不可用"
    echo
    echo "=== Docker 镜像 ==="
    docker images | grep userauth 2>&1 || echo "未找到userauth相关镜像"
    echo
    echo "=== 端口占用情况 ==="
    netstat -tulpn 2>/dev/null | grep -E ":(3306|6379|8080)" || echo "端口检查不可用"
} > "$REPORT_DIR/docker-status.txt"

echo "[6/7] 运行快速测试诊断..."
cd backend
{
    echo "=== 快速测试诊断 ==="
    echo "开始时间: $(date)"
    echo
    mvn test -q -Dmaven.test.failure.ignore=true 2>&1
    echo
    echo "结束时间: $(date)"
} > "../$REPORT_DIR/quick-test-diagnosis.txt"

echo "[7/7] 生成诊断摘要..."
cd ..

{
    echo "=== CI 诊断摘要 ==="
    echo "诊断时间: $(date)"
    echo "报告目录: $REPORT_DIR"
    echo
    
    echo "=== 测试结果摘要 ==="
    grep "Tests run:" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null || echo "未找到测试结果"
    
    echo
    echo "=== 构建状态 ==="
    if grep -q "BUILD SUCCESS" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "✅ 构建成功"
    elif grep -q "BUILD FAILURE" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "❌ 构建失败"
    else
        echo "⚠️  构建状态未知"
    fi
    
    echo
    echo "=== 错误摘要 ==="
    grep -i "error\|exception\|failed" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null | head -10 || echo "未发现明显错误"
    
    echo
    echo "=== 建议的下一步行动 ==="
    
    # 基于常见错误模式提供建议
    if grep -q "compilation failed\|cannot find symbol" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "🔧 编译错误检测到："
        echo "   - 检查依赖声明和导入语句"
        echo "   - 运行: mvn clean compile"
        echo "   - 检查Java版本兼容性"
    fi
    
    if grep -q "Connection refused\|connection timed out" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "🔌 连接错误检测到："
        echo "   - 检查Docker服务状态: docker ps"
        echo "   - 验证TestContainers配置"
        echo "   - 检查端口占用情况"
    fi
    
    if grep -q "Tests run:.*Failures: [1-9]" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "🧪 测试失败检测到："
        echo "   - 查看详细测试报告: $REPORT_DIR/surefire-reports/"
        echo "   - 单独运行失败的测试"
        echo "   - 检查测试数据和环境配置"
    fi
    
    if grep -q "OutOfMemoryError\|Java heap space" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "💾 内存问题检测到："
        echo "   - 增加JVM堆内存: export MAVEN_OPTS='-Xmx2g'"
        echo "   - 检查系统可用内存"
        echo "   - 优化测试用例"
    fi
    
    if ! grep -q "Tests run:\|BUILD" "$REPORT_DIR/quick-test-diagnosis.txt" 2>/dev/null; then
        echo "❓ 无法确定问题类型："
        echo "   - 查看完整日志: $REPORT_DIR/quick-test-diagnosis.txt"
        echo "   - 检查系统环境: $REPORT_DIR/system-info.txt"
        echo "   - 验证项目配置"
    fi
    
    echo
    echo "=== 有用的命令 ==="
    echo "查看详细错误: cat $REPORT_DIR/quick-test-diagnosis.txt"
    echo "检查依赖冲突: cat $REPORT_DIR/dependency-analysis.txt"
    echo "系统信息: cat $REPORT_DIR/system-info.txt"
    
} > "$REPORT_DIR/diagnosis-summary.txt"

echo
echo "========================================"
echo "诊断完成！"
echo "========================================"
echo
echo "诊断报告已保存到: $REPORT_DIR"
echo
echo "主要文件:"
echo "- diagnosis-summary.txt    : 诊断摘要和建议"
echo "- quick-test-diagnosis.txt : 快速测试结果"
echo "- system-info.txt         : 系统环境信息"
echo "- dependency-tree.txt     : Maven依赖树"
echo "- surefire-reports/       : 详细测试报告"
echo
echo "🔍 查看诊断摘要:"
echo "cat $REPORT_DIR/diagnosis-summary.txt"
echo
echo "📋 按照以下流程处理:"
echo "1. 查看诊断摘要获取建议"
echo "2. 根据建议执行修复操作"
echo "3. 重新运行测试验证修复"
echo "4. 如需帮助，提供诊断报告目录"