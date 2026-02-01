# 持续集成出错流程规范

## 概述

本文档定义了在持续集成（CI）过程中遇到错误时的标准化处理流程，确保问题能够被快速定位和解决。

## 错误处理流程图

```
CI 构建失败
    ↓
1. 快速错误分析（2分钟内）
    ↓
2. 基础错误修复尝试（最多2次）
    ↓
3. 详细日志分析
    ↓
4. 深度问题诊断
    ↓
5. 解决方案实施
    ↓
6. 验证和文档记录
```

## 详细流程步骤

### 第一步：快速错误分析（时间限制：2分钟）

#### 1.1 查看构建摘要
```bash
# 使用简化脚本快速查看错误摘要
./backend/test-summary.bat  # Windows
./backend/test-quiet.sh     # Linux/Mac
```

#### 1.2 识别错误类型
根据输出快速分类错误：

**编译错误**
- 关键词：`compilation failed`, `cannot find symbol`, `package does not exist`
- 优先级：🔴 高
- 预期解决时间：5-10分钟

**测试失败**
- 关键词：`Tests run:`, `Failures:`, `Errors:`
- 优先级：🟡 中
- 预期解决时间：10-30分钟

**依赖问题**
- 关键词：`dependency`, `artifact not found`, `version conflict`
- 优先级：🟠 中高
- 预期解决时间：15-45分钟

**环境问题**
- 关键词：`connection refused`, `timeout`, `permission denied`
- 优先级：🔴 高
- 预期解决时间：20-60分钟

### 第二步：基础错误修复尝试（最多2次）

#### 2.1 常见问题快速修复清单

**编译错误快速修复**
```bash
# 清理并重新编译
mvn clean compile

# 检查导入语句
grep -r "import.*\*" src/  # 查找可能的导入问题
```

**测试失败快速修复**
```bash
# 单独运行失败的测试
mvn test -Dtest=FailedTestClass

# 检查测试环境
mvn test -Dspring.profiles.active=test
```

**依赖问题快速修复**
```bash
# 强制更新依赖
mvn clean install -U

# 检查依赖冲突
mvn dependency:tree | grep -i conflict
```

#### 2.2 修复尝试记录
每次修复尝试都要记录：
- 尝试时间
- 采取的行动
- 结果（成功/失败）
- 观察到的变化

### 第三步：详细日志分析

如果基础修复失败，进入详细分析阶段：

#### 3.1 收集完整日志
```bash
# 生成详细测试报告
mvn test -X > full-test-log.txt 2>&1

# 查看特定测试的详细输出
mvn test -Dtest=FailedTestClass -X
```

#### 3.2 日志分析检查点

**按优先级检查以下内容：**

1. **异常堆栈跟踪**
   - 查找根本原因异常（Caused by）
   - 识别第一个非框架代码的堆栈帧

2. **配置问题**
   - 数据库连接配置
   - Spring配置加载问题
   - 环境变量缺失

3. **资源问题**
   - 内存不足
   - 文件权限问题
   - 网络连接问题

4. **时序问题**
   - 测试间的依赖关系
   - 异步操作超时
   - 数据库事务问题

#### 3.3 日志文件位置
```
backend/target/surefire-reports/          # 单元测试报告
backend/target/failsafe-reports/          # 集成测试报告
backend/logs/                             # 应用日志
backend/target/surefire-reports/*.txt     # 详细测试输出
```

### 第四步：深度问题诊断

#### 4.1 环境验证
```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -version

# 检查环境变量
echo $JAVA_HOME
echo $MAVEN_HOME

# 检查Docker环境（如果使用TestContainers）
docker --version
docker ps
```

#### 4.2 依赖分析
```bash
# 分析依赖树
mvn dependency:tree > dependency-tree.txt

# 检查版本冲突
mvn dependency:analyze

# 验证类路径
mvn dependency:build-classpath
```

#### 4.3 数据库相关问题
```bash
# 检查数据库连接
mvn flyway:info

# 验证数据库迁移
mvn flyway:validate

# 重置测试数据库
mvn flyway:clean flyway:migrate -Dspring.profiles.active=test
```

### 第五步：解决方案实施

#### 5.1 解决方案分类

**代码修复**
- 修复业务逻辑错误
- 更新测试用例
- 调整配置参数

**环境修复**
- 更新依赖版本
- 修改配置文件
- 调整环境变量

**基础设施修复**
- 重启服务
- 清理缓存
- 更新Docker镜像

#### 5.2 修复验证步骤
1. 本地验证修复
2. 运行相关测试套件
3. 执行完整构建
4. 检查是否引入新问题

### 第六步：验证和文档记录

#### 6.1 验证清单
- [ ] 所有测试通过
- [ ] 构建成功
- [ ] 没有引入新的警告
- [ ] 相关功能正常工作
- [ ] 性能没有明显下降

#### 6.2 文档记录
在 `docs/development/troubleshooting-log.md` 中记录：
- 问题描述
- 错误症状
- 根本原因
- 解决方案
- 预防措施

## 常见错误模式和解决方案

### 编译错误

**问题**: `package does not exist`
```bash
# 解决方案
mvn clean install
# 检查pom.xml中的依赖声明
```

**问题**: `cannot find symbol`
```bash
# 解决方案
# 1. 检查导入语句
# 2. 验证类路径
# 3. 重新生成IDE项目文件
```

### 测试错误

**问题**: `Connection refused`
```bash
# 解决方案
# 1. 检查TestContainers配置
# 2. 验证Docker服务状态
# 3. 检查端口占用
docker ps
netstat -tulpn | grep :3306
```

**问题**: `Test timeout`
```bash
# 解决方案
# 1. 增加测试超时时间
# 2. 检查异步操作
# 3. 优化测试数据
```

### 依赖问题

**问题**: `Version conflict`
```bash
# 解决方案
mvn dependency:tree | grep -i conflict
# 在pom.xml中明确指定版本
```

## 升级策略

### 何时升级到下一级别

**从快速分析到详细分析**
- 2次基础修复尝试失败
- 错误信息不明确
- 涉及多个组件

**从详细分析到深度诊断**
- 日志分析无法定位根因
- 涉及环境或基础设施问题
- 需要专业知识领域支持

## 工具和脚本

### 快速诊断脚本
```bash
# 创建诊断脚本
./scripts/diagnose-ci-failure.sh
```

### 日志分析工具
```bash
# 使用grep快速过滤关键信息
grep -E "(ERROR|FAIL|Exception)" full-test-log.txt

# 提取测试摘要
grep -A 5 -B 5 "Tests run:" full-test-log.txt
```

## 联系和升级路径

### 内部升级
1. 团队成员互助（15分钟内）
2. 技术负责人介入（30分钟内）
3. 架构师支持（1小时内）

### 外部支持
1. 社区论坛搜索
2. 官方文档查阅
3. 技术支持联系

## 性能指标

### 目标解决时间
- 编译错误：< 10分钟
- 单元测试失败：< 20分钟
- 集成测试失败：< 45分钟
- 环境问题：< 60分钟

### 成功率指标
- 第一次修复成功率：> 60%
- 第二次修复成功率：> 85%
- 最终解决率：> 95%

---

**最后更新**: 2026-02-01
**版本**: 1.0
**维护者**: 开发团队