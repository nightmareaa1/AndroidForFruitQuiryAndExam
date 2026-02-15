# 后端服务部署指南

本文档提供完整的后端服务部署流程,支持从本地开发环境到生产服务器的部署。适用于2GB内存及以上配置的云服务器。

## 目录

- [部署方案概述](#部署方案概述)
- [环境要求](#环境要求)
- [快速部署(2GB服务器)](#快速部署2gb服务器)
- [生产环境部署](#生产环境部署)
- [部署验证](#部署验证)
- [常见问题](#常见问题)
- [进阶配置](#进阶配置)

## 部署方案概述

本项目提供三种部署方案:

| 方案 | 适用场景 | 内存需求 | 配置复杂度 | 高可用 |
|------|----------|----------|------------|--------|
| **Light** | 2GB服务器/测试环境 | 2GB | 低 | ❌ |
| **Standard** | 4-8GB服务器/生产环境 | 4GB+ | 中 | ⚠️ |
| **Production** | 16GB+服务器/企业级生产 | 16GB+ | 高 | ✅ |

### Light方案架构

```
┌─────────────────────────────────────┐
│  Nginx (可选) - 反向代理             │
└──────────────┬──────────────────────┘
               │
       ┌───────▼────────┐
       │  Backend       │ 400-500MB
       │  (Spring Boot) │
       └───────┬────────┘
               │
       ┌───────▼────────┐
       │  MySQL         │ 300MB
       └───────┬────────┘
               │
       ┌───────▼────────┐
       │  Redis         │ 64MB
       └────────────────┘
```

## 环境要求

### 服务器配置

**最低配置(2GB服务器)**:
- CPU: 2核
- 内存: 2GB RAM
- 磁盘: 20GB+
- 系统: Ubuntu 20.04+/CentOS 7+/Debian 10+
- 网络: 公网IP,开放端口8080,3306(可选),6379(可选)

**推荐配置(生产环境)**:
- CPU: 4核+
- 内存: 4GB+ RAM
- 磁盘: 50GB+ SSD
- 系统: Ubuntu 22.04 LTS
- 网络: 域名+HTTPS

### 软件依赖

- Docker 20.10+
- Docker Compose 2.0+
- Git
- curl/wget

## 快速部署(2GB服务器)

### 步骤1: 服务器准备

**1.1 安装Docker**

```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
docker compose version
```

**1.2 创建项目目录**

```bash
mkdir -p /opt/userauth
cd /opt/userauth
```

### 步骤2: 获取项目代码

```bash
# 方式1: 使用Git克隆(推荐)
git clone <your-repo-url> .

# 方式2: 手动上传项目文件
# 使用scp或rsync上传项目到服务器
scp -r backend docker-compose.light.yml .env user@server:/opt/userauth/
```

### 步骤3: 配置环境变量

创建 `.env` 文件:

```bash
cat > .env << 'EOF'
# ============================================
# 数据库配置
# ============================================
DB_ROOT_PASSWORD=YourSecureRootPassword123!
DB_PASSWORD=YourSecureDBPassword456!

# ============================================
# JWT安全配置(必须修改!)
# ============================================
# 生成32位以上随机字符串:
# openssl rand -base64 48
JWT_SECRET=YourVeryLongAndSecureJWTSecretKeyHereAtLeast32Chars!

# ============================================
# CORS配置(根据实际前端地址修改)
# ============================================
# Android模拟器: http://10.0.2.2:8080
# 本地开发: http://localhost:8080
# 生产环境: http://your-domain.com
CORS_ALLOWED_ORIGINS=http://localhost:8080,http://10.0.2.2:8080

# ============================================
# 日志级别
# ============================================
LOGGING_LEVEL_ROOT=INFO
EOF
```

**重要安全提示**:
- ⚠️ 务必修改JWT_SECRET,使用随机生成的强密钥
- ⚠️ 生产环境使用强数据库密码
- ⚠️ 不要提交.env文件到版本控制

### 步骤4: 部署服务

**4.1 使用Light配置部署(推荐2GB服务器)**

```bash
# 进入项目目录
cd /opt/userauth

# 启动所有服务(首次启动会下载镜像,可能需要几分钟)
docker compose -f docker-compose.light.yml up -d

# 查看启动状态
docker compose -f docker-compose.light.yml ps

# 查看日志
docker compose -f docker-compose.light.yml logs -f
```

**4.2 验证服务状态**

等待30-60秒后检查:

```bash
# 查看所有容器状态
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

# 预期输出:
# NAMES              STATUS                        PORTS
# userauth-backend   Up 2 minutes (healthy)        0.0.0.0:8080->8080/tcp
# userauth-mysql     Up 2 minutes (healthy)        0.0.0.0:3306->3306/tcp
# userauth-redis     Up 2 minutes (healthy)        0.0.0.0:6379->6379/tcp
```

### 步骤5: 验证API可用性

**5.1 本地测试(服务器上)**

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 预期输出:
# {"status":"UP","components":{"db":{"status":"UP"},"redis":{"status":"UP"}}}

# 测试水果查询API
curl "http://localhost:8080/api/fruit/query?type=nutrition&fruit=mango"

# 测试用户注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test1234!"}'
```

**5.2 外网访问测试**

在本地电脑上测试:

```bash
# 替换your-server-ip为实际服务器IP
curl http://your-server-ip:8080/actuator/health
```

## 生产环境部署

### 方案1: 使用Nginx反向代理

如果需要域名和HTTPS访问,配置Nginx:

```bash
# 安装Nginx
sudo apt update
sudo apt install nginx -y

# 创建Nginx配置文件
sudo tee /etc/nginx/sites-available/userauth << 'EOF'
server {
    listen 80;
    server_name your-domain.com;  # 替换为你的域名

    location / {
        proxy_pass http://localhost:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
        
        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }
}
EOF

# 启用配置
sudo ln -s /etc/nginx/sites-available/userauth /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### 方案2: 使用Docker Compose Production配置

适用于更高配置的服务器(4GB+):

```bash
# 使用生产配置(包含监控、日志聚合等)
docker compose -f docker-compose.prod.yml up -d

# 查看所有服务
docker compose -f docker-compose.prod.yml ps
```

## 部署验证

### 自动验证脚本

创建验证脚本 `verify-deployment.sh`:

```bash
#!/bin/bash

echo "🔍 开始部署验证..."

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 检查容器状态
echo -e "\n📦 检查容器状态..."
if docker ps | grep -q "userauth-backend"; then
    echo -e "${GREEN}✅ Backend容器运行中${NC}"
else
    echo -e "${RED}❌ Backend容器未运行${NC}"
    exit 1
fi

if docker ps | grep -q "userauth-mysql"; then
    echo -e "${GREEN}✅ MySQL容器运行中${NC}"
else
    echo -e "${RED}❌ MySQL容器未运行${NC}"
    exit 1
fi

if docker ps | grep -q "userauth-redis"; then
    echo -e "${GREEN}✅ Redis容器运行中${NC}"
else
    echo -e "${RED}❌ Redis容器未运行${NC}"
    exit 1
fi

# 检查健康状态
echo -e "\n🏥 检查服务健康状态..."
HEALTH=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"UP"')
if [ ! -z "$HEALTH" ]; then
    echo -e "${GREEN}✅ 服务健康检查通过${NC}"
else
    echo -e "${RED}❌ 服务健康检查失败${NC}"
    exit 1
fi

# 检查数据库连接
echo -e "\n🗄️  检查数据库连接..."
DB_STATUS=$(curl -s http://localhost:8080/actuator/health | grep -o '"db":{"status":"UP"}')
if [ ! -z "$DB_STATUS" ]; then
    echo -e "${GREEN}✅ 数据库连接正常${NC}"
else
    echo -e "${RED}❌ 数据库连接异常${NC}"
    exit 1
fi

# 测试API
echo -e "\n🌐 测试API接口..."
API_TEST=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/fruit/query?type=nutrition&fruit=mango)
if [ "$API_TEST" = "200" ]; then
    echo -e "${GREEN}✅ API接口响应正常${NC}"
else
    echo -e "${RED}❌ API接口响应异常 (HTTP $API_TEST)${NC}"
    exit 1
fi

# 检查内存使用
echo -e "\n💾 检查内存使用..."
docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}\t{{.MemPerc}}"

echo -e "\n${GREEN}🎉 所有验证通过!服务部署成功!${NC}"
echo -e "\n📋 访问信息:"
echo "   - API地址: http://your-server-ip:8080/api"
echo "   - 健康检查: http://your-server-ip:8080/actuator/health"
echo "   - 如果配置了Nginx: http://your-domain.com/api"
```

使用验证脚本:

```bash
chmod +x verify-deployment.sh
./verify-deployment.sh
```

## 常见问题

### 问题1: MySQL容器健康检查失败

**症状**: `userauth-mysql is unhealthy`

**解决**:

```bash
# 查看MySQL日志
docker logs userauth-mysql

# 常见原因1: 内存不足,增加swap
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile

# 重启服务
docker compose -f docker-compose.light.yml restart mysql
```

### 问题2: Backend启动后立即退出

**症状**: `userauth-backend` 容器状态为 `Exited`

**解决**:

```bash
# 查看日志
docker logs userauth-backend

# 常见原因1: JWT密钥太短或使用了默认值
# 检查.env文件中的JWT_SECRET是否已修改

# 常见原因2: 数据库连接失败
# 确保MySQL已完全启动后再启动backend
docker compose -f docker-compose.light.yml up -d mysql
sleep 30
docker compose -f docker-compose.light.yml up -d backend
```

### 问题3: 端口被占用

**症状**: `bind: address already in use`

**解决**:

```bash
# 查找占用端口的进程
sudo lsof -i :8080
sudo lsof -i :3306
sudo lsof -i :6379

# 停止冲突服务或修改docker-compose端口映射
# 编辑docker-compose.light.yml,修改ports部分
```

### 问题4: 无法从外网访问

**症状**: 服务器上curl正常,外网无法访问

**解决**:

```bash
# 检查防火墙
sudo ufw status
sudo ufw allow 8080/tcp

# 检查云服务器安全组
# 需要在云平台控制台开放8080端口

# 检查Docker端口映射
docker port userauth-backend
```

### 问题5: 磁盘空间不足

**症状**: `no space left on device`

**解决**:

```bash
# 查看磁盘使用
df -h

# 清理Docker未使用资源
docker system prune -a

# 清理日志
docker compose -f docker-compose.light.yml logs --tail 100
```

## 进阶配置

### 自动启动配置

确保服务器重启后服务自动启动:

```bash
# Docker服务开机自启
sudo systemctl enable docker

# 容器自动重启已在docker-compose中配置
# restart: unless-stopped
```

### 日志管理

```bash
# 查看实时日志
docker compose -f docker-compose.light.yml logs -f

# 查看特定服务日志
docker compose -f docker-compose.light.yml logs -f backend

# 清理日志文件
docker exec userauth-backend sh -c "find /app/logs -name '*.log' -mtime +7 -delete"
```

### 备份策略

**数据库备份**:

```bash
# 创建备份脚本
cat > backup.sh << 'EOF'
#!/bin/bash
BACKUP_DIR="/backup/mysql"
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
docker exec userauth-mysql mysqldump -u root -p$DB_ROOT_PASSWORD userauth_dev > $BACKUP_DIR/backup_$DATE.sql
# 保留最近7天的备份
find $BACKUP_DIR -name "backup_*.sql" -mtime +7 -delete
EOF
chmod +x backup.sh

# 添加到cron,每天凌晨2点执行
echo "0 2 * * * /opt/userauth/backup.sh" | sudo crontab -
```

### 性能监控

```bash
# 查看容器资源使用
docker stats

# 查看后端JVM内存使用
docker exec userauth-backend ps aux | grep java

# 查看数据库连接数
docker exec userauth-mysql mysql -u root -p -e "SHOW PROCESSLIST;"
```

## 更新部署

### 更新代码

```bash
cd /opt/userauth
git pull origin main

# 重新构建并启动
docker compose -f docker-compose.light.yml down
docker compose -f docker-compose.light.yml up -d --build
```

### 数据库迁移

```bash
# Flyway会自动执行迁移
# 查看迁移状态
docker exec userauth-backend java -jar app.jar flyway info
```

## 附录

### 环境变量参考

| 变量名 | 说明 | 默认值 | 必需 |
|--------|------|--------|------|
| `JWT_SECRET` | JWT签名密钥 | - | ✅ |
| `DB_ROOT_PASSWORD` | MySQL root密码 | - | ✅ |
| `DB_PASSWORD` | MySQL应用密码 | SecureDbPass456$%^ | ⚠️ |
| `CORS_ALLOWED_ORIGINS` | 允许跨域来源 | * | ⚠️ |
| `LOGGING_LEVEL_ROOT` | 日志级别 | INFO | ❌ |

### 端口说明

| 端口 | 服务 | 说明 |
|------|------|------|
| 8080 | Backend | API服务端口 |
| 3306 | MySQL | 数据库端口(可选暴露) |
| 6379 | Redis | 缓存端口(可选暴露) |

### 性能基准

**2GB服务器配置下**:
- 启动时间: ~30-60秒
- 内存占用: ~900MB (Backend:400MB + MySQL:300MB + Redis:64MB + 系统开销)
- 并发能力: 50-100 QPS

---

## 快速命令参考

```bash
# 启动服务
docker compose -f docker-compose.light.yml up -d

# 停止服务
docker compose -f docker-compose.light.yml down

# 查看日志
docker compose -f docker-compose.light.yml logs -f

# 重启服务
docker compose -f docker-compose.light.yml restart

# 查看状态
docker compose -f docker-compose.light.yml ps

# 进入容器
docker exec -it userauth-backend sh
docker exec -it userauth-mysql mysql -u root -p
```

---

**文档版本**: 1.0  
**最后更新**: 2026-02-15  
**维护者**: Development Team
