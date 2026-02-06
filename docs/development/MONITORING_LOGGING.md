# 监控和日志系统配置

本文档描述用户认证系统的监控和日志系统配置，包括结构化日志、敏感信息脱敏、Prometheus指标收集等内容。

## 概述

系统采用以下监控和日志方案：

- **日志框架**：Logback + Logstash Encoder
- **指标收集**：Micrometer + Prometheus
- **健康检查**：Spring Boot Actuator
- **日志格式**：
  - 开发环境：彩色控制台输出
  - 生产环境：JSON结构化格式

## 文件结构

```
backend/
├── src/main/
│   ├── resources/
│   │   ├── logback-spring.xml    # 日志配置
│   │   └── application.yml       # 应用配置（含监控配置）
│   └── java/com/example/userauth/
│       └── config/
│           └── MetricsConfig.java # 自定义指标配置
```

## 日志配置

### logback-spring.xml

主日志配置文件，位于 `backend/src/main/resources/logback-spring.xml`。

#### 主要特性

1. **结构化JSON输出**
   - 使用 LogstashEncoder 格式
   - 包含自定义字段：`application`, `environment`

2. **日志轮转策略**
   - 按日期+大小滚动
   - 最大文件大小：100MB
   - 保留策略：
     - JSON日志：30天（最多10GB）
     - Plain文本日志：7天（最多1GB）

3. **异步写入**
   - 使用 AsyncAppender 提升性能
   - 队列大小：512
   - 非阻塞模式

4. **多环境支持**
   - `dev`: 彩色控制台输出
   - `prod`: JSON文件 + 异步
   - `test`: 最小化输出

### 日志输出格式

#### 开发环境（控制台）
```
2024-01-15 10:30:45.123 [main] INFO  com.example.userauth.AuthService - User logged in: john
```

#### 生产环境（JSON格式）
```json
{
  "@timestamp": "2024-01-15T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.example.userauth.AuthService",
  "message": "User logged in: john",
  "thread": "main",
  "application": "userauth-backend",
  "environment": "prod"
}
```

## 配置项说明

### 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `SPRING_PROFILES_ACTIVE` | 激活的配置文件 | `dev` |
| `LOG_STRUCTURED_FORMAT` | 启用结构化日志 | `false` |
| `MASK_SENSITIVE_DATA` | 启用敏感信息脱敏 | `true` |
| `LOG_FILE` | 日志文件名 | `logs/userauth-backend.log` |
| `LOG_PATH` | 日志目录 | `logs` |
| `LOGGING_LEVEL_ROOT` | 根日志级别 | `INFO` |
| `LOG_LEVEL_APP` | 应用日志级别 | `INFO` |

### application.yml 日志配置

```yaml
logging:
  level:
    root: ${LOGGING_LEVEL_ROOT:INFO}
    com.example.userauth: ${LOGGING_LEVEL_COM_EXAMPLE_USERAUTH:INFO}
    org.springframework.security: ${LOGGING_LEVEL_SECURITY:INFO}
  file:
    name: ${LOG_FILE:logs/userauth-backend.log}
    path: ${LOG_PATH:logs}
  structure:
    enabled: ${LOG_STRUCTURED_FORMAT:false}
    include-application-name: true
    include-environment: true
    include-profile: true
  security:
    mask-sensitive-data: ${MASK_SENSITIVE_DATA:true}
    mask-fields: password,passwd,pwd,secret,token,api_key,apikey,access_key,credit_card,ssn,bank_account
```

## 敏感信息脱敏

### 脱敏字段

以下字段在日志中会被自动脱敏：

| 字段类型 | 脱敏示例 |
|---------|---------|
| 信用卡号 | `****-****-****-****` |
| 邮箱 | `*****@****.***` |
| 电话 | `***-***-****` |
| SSN | `***-**-****` |
| JWT Token | `eyJ*****.****.*****` |
| Password | `[REDACTED]` |
| API Key | `[API_KEY_REDACTED]` |
| Bank Account | `[ACCOUNT_REDACTED]` |

### 配置文件中的脱敏

在 `application.yml` 中配置：

```yaml
logging:
  security:
    mask-sensitive-data: true
    mask-fields: password,passwd,pwd,secret,token,api_key,credit_card,ssn
```

## 监控配置

### Actuator 端点

| 端点 | 路径 | 说明 |
|------|------|------|
| Health | `/actuator/health` | 应用健康状态 |
| Info | `/actuator/info` | 应用信息 |
| Metrics | `/actuator/metrics` | 性能指标 |
| Prometheus | `/actuator/prometheus` | Prometheus格式指标 |
| Env | `/actuator/env` | 环境变量 |
| Loggers | `/actuator/loggers` | 日志级别配置 |
| ThreadDump | `/actuator/threaddump` | 线程转储 |
| HeapDump | `/actuator/heapdump` | 堆转储 |

### 配置示例

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,env,beans,configprops,loggers
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
  health:
    redis:
      enabled: true
    db:
      enabled: true
    diskspace:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
      environment: ${SPRING_PROFILES_ACTIVE:dev}
```

### Prometheus 指标

系统自动收集以下指标：

- **JVM指标**：内存、线程、GC
- **HTTP请求指标**：请求时间、状态码
- **数据库连接池指标**：连接数、等待时间
- **自定义业务指标**：通过 `@Timed` 注解

## 使用说明

### 启动应用

```bash
# 开发环境
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 生产环境
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### 查看日志

```bash
# 实时查看日志
tail -f logs/userauth-backend.log

# 查看JSON格式日志
cat logs/userauth-backend.log | jq '.'

# 搜索错误日志
grep "ERROR" logs/userauth-backend.log
```

### 查看监控指标

```bash
# 获取Prometheus格式指标
curl http://localhost:8080/api/actuator/prometheus

# 查看健康状态
curl http://localhost:8080/api/actuator/health

# 查看所有指标
curl http://localhost:8080/api/actuator/metrics
```

### 动态调整日志级别

```bash
# 查看当前日志级别
curl http://localhost:8080/api/actuator/loggers

# 临时调整日志级别
curl -X POST http://localhost:8080/api/actuator/loggers/com.example.userauth \
  -H "Content-Type: application/json" \
  -d '{"configuredLevel": "DEBUG"}'
```

## 日志分析

### 日志文件

| 文件 | 格式 | 说明 |
|------|------|------|
| `userauth-backend.log` | JSON | 主应用日志 |
| `userauth-backend-plain.log` | Plain | 备用文本日志 |
| `access.log` | JSON | HTTP访问日志 |

### 推荐的ELK配置

如果需要使用ELK Stack，可以配置Filebeat收集日志：

```yaml
# filebeat.yml
filebeat.inputs:
  - type: log
    enabled: true
    paths:
      - /var/log/userauth-backend/*.log
    json:
      key: message
      overwrite_keys: true
    fields:
      app: userauth-backend
```

## 故障排查

### 日志不输出

1. 检查日志级别配置
2. 验证日志文件路径权限
3. 确认profile配置正确

### JSON格式无效

```bash
# 验证JSON格式
cat logs/userauth-backend.log | jq '.'

# 如果 jq 报错，检查日志文件编码
file logs/userauth-backend.log
```

### 性能问题

如果日志影响性能：

1. 确认使用异步Appender
2. 调整队列大小
3. 减少日志输出级别
4. 考虑使用采样日志

## 最佳实践

1. **生产环境必须启用JSON格式**
2. **敏感信息脱敏默认开启**
3. **日志级别建议**：
   - 开发：DEBUG
   - 测试：INFO
   - 生产：INFO/WARN
4. **定期归档和清理日志**
5. **监控日志文件大小**

## 相关文档

- [部署指南](../deployment/backend-deploy.md)
- [安全配置](../security/SECURITY.md)
- [API文档](../api/README.md)
