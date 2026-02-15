# 服务器环境准备指南

本文档说明在2GB云服务器上部署后端服务需要安装的软件。

## 核心依赖（必须安装）

### 1. Docker
**用途**: 运行容器化应用（后端、MySQL、Redis）

**安装命令**:
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
```

### 2. Docker Compose
**用途**: 编排多个容器

**安装命令**:
```bash
# Docker Compose 现在包含在Docker中
docker compose version

# 如果旧版本需要单独安装
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 3. curl 或 wget
**用途**: 下载JAR包、测试API

**安装命令**:
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install -y curl wget

# CentOS/RHEL
sudo yum install -y curl wget
```

---

## 推荐工具（建议安装）

### 4. Git
**用途**: 克隆项目代码（可选，也可以手动上传）

**安装命令**:
```bash
# Ubuntu/Debian
sudo apt install -y git

# CentOS/RHEL
sudo yum install -y git

# 验证
git --version
```

### 5. unzip
**用途**: 解压文件

**安装命令**:
```bash
sudo apt install -y unzip
```

### 6. jq
**用途**: 解析JSON（查看API响应）

**安装命令**:
```bash
sudo apt install -y jq
```

---

## 不需要安装的软件 ❌

使用JAR包部署方案，以下软件**不需要**在服务器上安装：

| 软件 | 为什么不需要 |
|------|-------------|
| **Maven** | JAR包已预编译，无需在服务器编译 |
| **JDK/JRE** | 已包含在Docker镜像中 |
| **MySQL** | 使用Docker容器运行 |
| **Redis** | 使用Docker容器运行 |
| **Nginx** | 可选，如需使用也建议用Docker |

---

## 一键安装脚本

创建 `setup-server.sh`:

```bash
#!/bin/bash

echo "=========================================="
echo "🚀 服务器环境准备脚本"
echo "=========================================="

# 更新系统
echo "📦 更新系统包..."
sudo apt update && sudo apt upgrade -y

# 安装基础工具
echo "🔧 安装基础工具..."
sudo apt install -y \
    curl \
    wget \
    git \
    unzip \
    vim \
    htop \
    net-tools

# 安装Docker
echo "🐳 安装Docker..."
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com -o get-docker.sh
    sudo sh get-docker.sh
    sudo usermod -aG docker $USER
    sudo systemctl enable docker
    sudo systemctl start docker
    echo "✅ Docker安装完成"
else
    echo "✅ Docker已安装"
fi

# 验证Docker
docker --version
docker compose version

# 创建项目目录
echo "📁 创建项目目录..."
sudo mkdir -p /opt/userauth
sudo chown $USER:$USER /opt/userauth

echo ""
echo "=========================================="
echo "✅ 服务器环境准备完成！"
echo "=========================================="
echo ""
echo "请重新登录或执行: newgrp docker"
echo "然后运行: cd /opt/userauth && ./deploy-jar.sh"
```

使用:
```bash
chmod +x setup-server.sh
./setup-server.sh
```

---

## 验证安装

运行以下命令检查所有软件是否安装成功：

```bash
# 检查Docker
docker --version
docker compose version

# 检查curl
curl --version | head -1

# 检查git
git --version

# 检查磁盘空间
df -h

# 检查内存
free -h
```

---

## 快速开始（2GB服务器）

```bash
# 1. 连接到服务器
ssh user@your-server-ip

# 2. 运行环境准备（如果还没安装Docker）
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER
newgrp docker

# 3. 创建项目目录
mkdir -p /opt/userauth
cd /opt/userauth

# 4. 下载部署脚本和JAR包
# （从GitHub下载或使用脚本）

# 5. 启动服务
docker-compose -f docker-compose.jar.yml up -d

# 6. 验证
curl http://localhost:8080/actuator/health
```

---

## 常见问题

### Q: 需要安装Java吗？
**A**: 不需要！Java已包含在Docker镜像中（eclipse-temurin:17-jre）

### Q: 需要安装Maven吗？
**A**: 不需要！JAR包已在本地/CI环境编译好，服务器直接运行

### Q: 需要安装MySQL吗？
**A**: 不需要！使用Docker运行MySQL容器

### Q: 磁盘空间要求？
**A**: 建议至少20GB，Docker镜像和日志会占用空间

---

## 总结

**服务器最低要求**:
- ✅ Docker
- ✅ Docker Compose
- ✅ curl/wget
- ✅ 20GB+ 磁盘空间
- ✅ 2GB+ 内存

**不需要安装**:
- ❌ Maven
- ❌ JDK/JRE
- ❌ MySQL
- ❌ Redis

使用JAR包部署，服务器只需运行Docker容器即可！🚀
