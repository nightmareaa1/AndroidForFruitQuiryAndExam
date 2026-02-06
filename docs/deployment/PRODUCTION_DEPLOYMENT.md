# 生产部署指南

本文档描述用户认证系统的生产环境部署配置，包括容器化架构、Nginx负载均衡、MySQL主从复制、Redis缓存等组件的配置和部署步骤。

## 架构概览

```
┌─────────────────────────────────────────────────────────────────┐
│                        负载均衡层                                │
│  Nginx (SSL终止, 速率限制, 反向代理)                              │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│   Backend Instance 1    │       │   Backend Instance 2    │
│   (Spring Boot)         │       │   (Spring Boot)         │
└─────────────────────────┘       └─────────────────────────┘
              │                               │
              └───────────────┬───────────────┘
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
┌─────────────────────────┐       ┌─────────────────────────┐
│   MySQL Primary        │       │   MySQL Replica         │
│   (读写)                │       │   (只读)                │
└─────────────────────────┘       └─────────────────────────┘
                              │
                              ▼
                    ┌───────────────────┐
                    │   Redis           │
                    │   (缓存/会话)      │
                    └───────────────────┘
```

## 目录结构

```
├── docker-compose.prod.yml    # 生产环境Docker Compose配置
├── nginx/
│   ├── nginx.conf            # Nginx主配置
│   ├── security-headers.conf  # 安全响应头配置
│   └── ssl/                  # SSL证书目录
├── mysql/
│   ├── my.cnf               # MySQL主库配置
│   └── replica.cnf           # MySQL从库配置
├── redis/
│   └── redis.conf            # Redis配置
├── scripts/
│   ├── deploy-prod.sh        # Linux/macOS部署脚本
│   ├── deploy-prod.bat       # Windows部署脚本
│   ├── deploy-teardown.sh    # Linux/macOS清理脚本
│   └── deploy-teardown.bat   # Windows清理脚本
└── .env.prod                 # 生产环境变量（需创建）
```

## 快速开始

### 1. 准备工作

```bash
# 克隆项目
git clone <repository-url>
cd user-auth-system

# 复制环境变量模板
cp .env.prod.template .env.prod

# 编辑环境变量
vim .env.prod
```

### 2. 配置环境变量

编辑 `.env.prod` 文件，设置以下关键配置：

```bash
# JWT密钥（必须修改）
JWT_SECRET=your-very-long-and-secure-secret-key-here

# 数据库配置
MYSQL_ROOT_PASSWORD=your-secure-root-password
MYSQL_PASSWORD=your-secure-app-password
MYSQL_REPLICATION_PASSWORD=your-secure-repl-password

# Redis密码
REDIS_PASSWORD=your-secure-redis-password

# Grafana管理员密码
GRAFANA_ADMIN_PASSWORD=your-secure-grafana-password

# 域名配置
DOMAIN_NAME=yourdomain.com
```

### 3. 配置SSL证书

```bash
# 创建SSL目录
mkdir -p nginx/ssl

# 使用Let's Encrypt生成证书
certbot certonly --nginx -d yourdomain.com -d www.yourdomain.com

# 复制证书
cp /etc/letsencrypt/live/yourdomain.com/fullchain.pem nginx/ssl/server.crt
cp /etc/letsencrypt/live/yourdomain.com/privkey.pem nginx/ssl/server.key

# 或者使用自签名证书（仅限测试）
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout nginx/ssl/server.key \
  -out nginx/ssl/server.crt
```

### 4. 创建必要目录

```bash
# Linux/macOS
./scripts/deploy-prod.sh

# Windows (会自动创建目录)
scripts\deploy-prod.bat
```

### 5. 启动服务

```bash
# Linux/macOS
chmod +x scripts/deploy-prod.sh
./scripts/deploy-prod.sh

# Windows
scripts\deploy-prod.bat
```

## 服务说明

### 核心服务

| 服务 | 端口 | 说明 |
|------|------|------|
| nginx | 80, 443 | 反向代理和负载均衡 |
| backend-1 | 8080 | Spring Boot实例1 |
| backend-2 | 8080 | Spring Boot实例2 |
| mysql-primary | 3306 | MySQL主库（读写） |
| mysql-replica | 3307 | MySQL从库（只读） |
| redis | 6379 | Redis缓存 |

### 监控服务

| 服务 | 端口 | 说明 |
|------|------|------|
| prometheus | 9090 | 指标收集 |
| grafana | 3000 | 监控面板 |
| alertmanager | 9093 | 告警管理 |

## 访问地址

| 服务 | 地址 | 凭据 |
|------|------|------|
| 应用API | http://localhost/api | - |
| Grafana | http://localhost:3000 | admin/配置的密码 |
| Prometheus | http://localhost:9090 | - |

## 负载均衡配置

### Nginx配置特点

- **连接池**：最多4096连接/worker
- **负载均衡算法**：最少连接数（least_conn）
- **健康检查**：自动检测后端可用性
- **速率限制**：
  - API请求：10请求/秒
  - 登录接口：5请求/分钟
- **超时设置**：
  - 连接超时：30秒
  - 发送超时：60秒
  - 读取超时：60秒

### 后端健康检查

```yaml
healthcheck:
  test: ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://localhost:8080/api/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 5
  start_period: 60s
```

## 数据库配置

### MySQL主从复制

**主库配置** (`mysql/my.cnf`)：
- 二进制日志：ROW格式
- GTID复制
- 同步写入（sync_binlog=1）
- 缓冲区池：1GB

**从库配置** (`mysql/replica.cnf`)：
- 只读模式
- 延迟应用日志
- 继承主库配置

### 复制状态检查

```sql
-- 在主库上
SHOW MASTER STATUS\G
SHOW SLAVE HOSTS\G

-- 在从库上
SHOW SLAVE STATUS\G
```

## Redis配置

### 特点

- AOF持久化：每秒同步
- 最大内存：512MB
- 淘汰策略：LRU
- 认证密码保护

### 性能优化

```conf
tcp-backlog 511
tcp-keepalive 300
maxmemory-policy allkeys-lru
```

## 滚动更新

### 零停机部署

```bash
# 使用Docker Compose的滚动更新
docker-compose -f docker-compose.prod.yml up -d --no-deps backend

# 或者使用蓝绿部署
# 1. 启动新版本backend-3
# 2. 将流量切换到新版本
# 3. 停止旧版本
```

### 健康检查回滚

如果健康检查连续失败，Docker会自动重启容器：

```yaml
restart: unless-stopped
healthcheck:
  retries: 5
```

## 备份策略

### 自动备份（需配置）

建议使用cron或外部备份工具：

```bash
# 每日备份MySQL
0 2 * * * docker exec userauth-mysql-primary mysqldump -u root -p$PASS db > /backup/mysql/daily_$(date +\%Y\%m\%d).sql

# 每周备份Redis
0 3 * * 0 docker exec userauth-redis redis-cli BGSAVE
```

### 数据恢复

```bash
# 恢复MySQL
docker exec -i userauth-mysql-primary mysql -u root -p db < backup.sql

# 恢复Redis
docker cp backup.rdb userauth-redis:/data/dump.rdb
docker restart userauth-redis
```

## 监控配置

### Prometheus指标

所有后端服务暴露 `/actuator/prometheus` 端点：

```yaml
metrics:
  tags:
    application: userauth-backend
    environment: prod
```

### Grafana仪表板

首次访问Grafana时：
1. 添加Prometheus数据源
2. 导入监控仪表板
3. 配置告警规则

### 告警配置

编辑 `prometheus/alertmanager.yml`：

```yaml
route:
  group_by: ['alertname']
  receiver: 'default-receiver'
receivers:
  - name: 'default-receiver'
    webhook_configs:
      - url: 'http://your-webhook-url'
```

## 安全配置

### Nginx安全头

配置文件：`nginx/security-headers.conf`

- X-Frame-Options
- X-Content-Type-Options
- X-XSS-Protection
- Strict-Transport-Security
- Content-Security-Policy

### SSL/TLS配置

```nginx
ssl_protocols TLSv1.2 TLSv1.3;
ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256;
ssl_prefer_server_ciphers off;
```

### 速率限制

```nginx
limit_req_zone $binary_remote_addr zone=api_limit:10m rate=10r/s;
limit_conn_zone $binary_remote_addr zone=conn_limit:10m;
```

## 故障排查

### 查看服务状态

```bash
# 所有服务状态
docker-compose -f docker-compose.prod.yml ps

# 查看特定服务日志
docker-compose -f docker-compose.prod.yml logs -f backend-1

# 查看服务资源使用
docker stats
```

### 常见问题

**MySQL连接失败**
```bash
# 检查MySQL日志
docker logs userauth-mysql-primary

# 检查连接数
docker exec userauth-mysql-primary mysql -u root -p -e "SHOW PROCESSLIST"
```

**后端服务不健康**
```bash
# 检查健康检查端点
curl http://localhost:9080/api/actuator/health

# 检查后端日志
docker logs userauth-backend-1
```

**Redis连接问题**
```bash
# 测试Redis连接
docker exec userauth-redis redis-cli -a $PASS ping
```

## 资源调整

### 内存限制

根据服务器配置调整 `docker-compose.prod.yml`：

```yaml
deploy:
  resources:
    limits:
      memory: 2G
    reservations:
      memory: 1G
```

### MySQL缓冲区池

```ini
innodb_buffer_pool_size=2G  # 调整为可用内存的50-75%
```

## 相关文档

- [监控和日志配置](../development/MONITORING_LOGGING.md)
- [安全配置](../security/SECURITY.md)
- [API文档](../api/README.md)
