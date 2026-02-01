# 开发环境设置指南

## 概述

本项目使用Docker Compose提供统一的容器化开发环境，确保所有开发者拥有一致的开发体验。

## 前置要求

- Docker Desktop (Windows/Mac) 或 Docker Engine (Linux)
- Docker Compose v2.0+
- Git

## 快速启动

### Windows
```bash
# 启动开发环境
scripts\dev-start.bat

# 停止开发环境
scripts\dev-stop.bat
```

### Linux/Mac
```bash
# 启动开发环境
./scripts/dev-start.sh

# 停止开发环境
./scripts/dev-stop.sh
```

### 手动启动
```bash
# 启动所有服务
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps

# 查看日志
docker-compose -f docker-compose.dev.yml logs -f

# 停止服务
docker-compose -f docker-compose.dev.yml down
```

## 服务说明

### MySQL 数据库
- **端口**: 3306
- **数据库**: userauth_dev
- **用户名**: userauth
- **密码**: password
- **Root密码**: rootpassword

### Redis 缓存
- **端口**: 6379
- **用途**: 会话存储、缓存

### Spring Boot 后端
- **端口**: 8080
- **健康检查**: http://localhost:8080/actuator/health
- **API文档**: http://localhost:8080/swagger-ui.html (配置后可用)

## 环境变量

开发环境变量在 `.env.dev` 文件中配置：

```bash
# 数据库配置
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=userauth_dev
MYSQL_USER=userauth
MYSQL_PASSWORD=password

# JWT配置
JWT_SECRET=dev-secret-key-change-in-production

# 应用配置
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=dev
```

## 热重载支持

- **后端**: 使用Spring Boot DevTools，代码修改后自动重启
- **数据库**: 数据持久化到Docker卷，重启不丢失数据
- **配置**: 修改配置文件后需要重启后端服务

## 常用命令

```bash
# 重启后端服务
docker-compose -f docker-compose.dev.yml restart backend

# 查看后端日志
docker-compose -f docker-compose.dev.yml logs -f backend

# 进入MySQL容器
docker-compose -f docker-compose.dev.yml exec mysql mysql -u userauth -p

# 进入Redis容器
docker-compose -f docker-compose.dev.yml exec redis redis-cli

# 清理所有数据（谨慎使用）
docker-compose -f docker-compose.dev.yml down -v
```

## 故障排除

### CI/CD 构建失败
遇到持续集成构建失败？按照以下流程处理：

1. **🚨 紧急响应** (2分钟内)
   ```bash
   # 快速查看错误摘要
   ./backend/test-summary.bat    # Windows
   ./backend/test-quiet.sh       # Linux/Mac
   ```

2. **🔍 自动诊断** (推荐)
   ```bash
   # 运行自动诊断脚本
   ./scripts/diagnose-ci-failure.bat  # Windows
   ./scripts/diagnose-ci-failure.sh   # Linux/Mac
   ```

3. **📖 详细指南**
   - [CI错误处理流程规范](./ci-error-troubleshooting-guide.md) - 完整的错误处理流程
   - [CI错误快速参考](./ci-error-quick-reference.md) - 常见错误快速查找
   - [故障排除日志](./troubleshooting-log.md) - 历史问题和解决方案

### 测试相关问题
- [数据库集成测试问题](./database-integration-test-issues.md) - 数据库测试常见问题
- [Spring Boot测试配置问题](./spring-boot-test-issues.md) - Web层测试和集成测试配置问题
- 运行简化测试: `./backend/test-quiet.sh` 减少日志输出

### 端口冲突
如果遇到端口冲突，可以修改 `docker-compose.dev.yml` 中的端口映射：
```yaml
ports:
  - "3307:3306"  # 将MySQL映射到3307端口
```

### 服务启动失败
1. 检查Docker是否正在运行
2. 检查端口是否被占用
3. 查看服务日志：`docker-compose -f docker-compose.dev.yml logs [service-name]`

### 数据库连接问题
1. 确保MySQL服务健康：`docker-compose -f docker-compose.dev.yml ps`
2. 检查数据库配置是否正确
3. 等待MySQL完全启动（首次启动可能需要更长时间）

## 开发工作流

1. **启动环境**: 运行启动脚本
2. **开发代码**: 修改后端代码，自动热重载
3. **测试API**: 使用Postman或curl测试API端点
4. **查看日志**: 使用Docker Compose查看服务日志
5. **停止环境**: 开发完成后运行停止脚本

## 数据持久化

- **MySQL数据**: 存储在 `mysql_data` Docker卷中
- **Redis数据**: 存储在 `redis_data` Docker卷中
- **上传文件**: 存储在 `backend_uploads` Docker卷中

数据在容器重启后保持不变，除非显式删除卷。