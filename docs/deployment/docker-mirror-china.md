# Docker 国内镜像加速指南

解决 Docker 镜像拉取超时、速度慢的问题。

## 问题症状

```
ERROR: failed to resolve reference "docker.io/library/mysql:8.0": 
dial tcp 108.160.167.30:443: i/o timeout
```

或

```
Error response from daemon: Get "https://registry-1.docker.io/v2/": net/http: request canceled
```

## 解决方案

### 方案1: 配置国内镜像源（推荐）

#### 阿里云镜像加速（推荐）

1. **登录阿里云获取加速地址**
   - 访问: https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors
   - 登录阿里云账号
   - 复制你的专属加速地址（格式: `https://xxxxx.mirror.aliyuncs.com`）

2. **配置Docker使用镜像**

**Linux (Ubuntu/CentOS):**
```bash
# 创建或编辑daemon.json
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://你的阿里云镜像地址.mirror.aliyuncs.com",
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
EOF

# 重启Docker
sudo systemctl daemon-reload
sudo systemctl restart docker

# 验证配置
docker info | grep -A 5 "Registry Mirrors"
```

**Windows (Docker Desktop):**
1. 打开 Docker Desktop
2. 点击 Settings (齿轮图标)
3. 选择 Docker Engine
4. 添加以下内容到JSON配置：
```json
{
  "registry-mirrors": [
    "https://你的阿里云镜像地址.mirror.aliyuncs.com"
  ]
}
```
5. 点击 Apply & Restart

**macOS (Docker Desktop):**
1. 打开 Docker Desktop
2. 点击 Preferences
3. 选择 Docker Engine
4. 添加镜像配置（同上）

---

#### 其他免费镜像源

如果没有阿里云账号，可以使用这些公共镜像：

```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",      // 中科大
    "https://hub-mirror.c.163.com",            // 网易云
    "https://mirror.baidubce.com",             // 百度云
    "https://docker.m.daocloud.io"             // DaoCloud
  ]
}
```

---

### 方案2: 使用代理

如果你已有代理工具：

```bash
# 为Docker配置HTTP代理
sudo mkdir -p /etc/systemd/system/docker.service.d

sudo tee /etc/systemd/system/docker.service.d/http-proxy.conf <<EOF
[Service]
Environment="HTTP_PROXY=http://你的代理地址:端口"
Environment="HTTPS_PROXY=http://你的代理地址:端口"
Environment="NO_PROXY=localhost,127.0.0.1"
EOF

# 重启Docker
sudo systemctl daemon-reload
sudo systemctl restart docker
```

---

### 方案3: 手动下载镜像（离线环境）

在有网络的环境下载镜像，然后导出导入：

```bash
# 在有网络的服务器上
docker pull mysql:8.0
docker pull redis:7-alpine
docker pull eclipse-temurin:17-jre-jammy

# 保存镜像为tar文件
docker save mysql:8.0 > mysql-8.0.tar
docker save redis:7-alpine > redis-7-alpine.tar
docker save eclipse-temurin:17-jre-jammy > java-17.tar

# 传输到目标服务器（使用scp或U盘）
scp *.tar user@target-server:/opt/images/

# 在目标服务器导入
docker load < mysql-8.0.tar
docker load < redis-7-alpine.tar
docker load < java-17.tar
```

---

### 方案4: 修改 docker-compose 使用国内镜像

直接修改 `docker-compose.jar.yml`，使用国内仓库的镜像：

```yaml
services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile.jar
    # 使用阿里云镜像仓库（需要提前拉取或构建）
    
  mysql:
    # 使用阿里云镜像仓库
    image: registry.cn-hangzhou.aliyuncs.com/acs-sample/mysql:8.0
    # 或者使用其他国内源
    # image: docker.mirrors.ustc.edu.cn/library/mysql:8.0
    
  redis:
    # 使用阿里云镜像仓库
    image: registry.cn-hangzhou.aliyuncs.com/acs-sample/redis:7-alpine
```

---

## 一键配置脚本

创建 `setup-docker-mirror.sh`:

```bash
#!/bin/bash

echo "🚀 Docker 国内镜像加速配置脚本"
echo "================================"

# 检测操作系统
if [ -f /etc/os-release ]; then
    . /etc/os-release
    OS=$NAME
else
    OS=$(uname -s)
fi

# 选择镜像源
echo ""
echo "请选择镜像源:"
echo "1) 阿里云（推荐，需自己申请）"
echo "2) 中科大"
echo "3) 网易云"
echo "4) 百度云"
echo "5) DaoCloud"
echo "6) 多源配置（推荐）"
read -p "请输入选项 (1-6): " choice

case $choice in
    1)
        read -p "请输入你的阿里云镜像地址 (如: https://abc123.mirror.aliyuncs.com): " mirror
        MIRRORS="\"$mirror\""
        ;;
    2)
        MIRRORS="\"https://docker.mirrors.ustc.edu.cn\""
        ;;
    3)
        MIRRORS="\"https://hub-mirror.c.163.com\""
        ;;
    4)
        MIRRORS="\"https://mirror.baidubce.com\""
        ;;
    5)
        MIRRORS="\"https://docker.m.daocloud.io\""
        ;;
    6)
        MIRRORS='[
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com",
    "https://docker.m.daocloud.io"
  ]'
        ;;
    *)
        echo "❌ 无效选项"
        exit 1
        ;;
esac

# 备份原有配置
if [ -f /etc/docker/daemon.json ]; then
    sudo cp /etc/docker/daemon.json /etc/docker/daemon.json.bak.$(date +%Y%m%d_%H%M%S)
    echo "✅ 已备份原有配置"
fi

# 创建配置
sudo mkdir -p /etc/docker

if [ "$choice" = "6" ]; then
    sudo tee /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": $MIRRORS
}
EOF
else
    sudo tee /etc/docker/daemon.json <<EOF
{
  "registry-mirrors": [$MIRRORS]
}
EOF
fi

echo "✅ 镜像配置已写入"

# 重启Docker
echo "🔄 重启Docker服务..."
if command -v systemctl &> /dev/null; then
    sudo systemctl daemon-reload
    sudo systemctl restart docker
else
    sudo service docker restart
fi

# 验证
echo ""
echo "🔍 验证配置..."
sleep 2
if docker info | grep -q "Registry Mirrors"; then
    echo "✅ 配置成功！当前镜像源:"
    docker info | grep -A 10 "Registry Mirrors"
    echo ""
    echo "🧪 测试拉取镜像..."
    docker pull hello-world
    if [ $? -eq 0 ]; then
        echo "✅ 镜像拉取测试成功！"
        docker rmi hello-world > /dev/null 2>&1
    else
        echo "⚠️ 测试拉取失败，请检查网络或更换镜像源"
    fi
else
    echo "❌ 配置未生效，请手动检查 /etc/docker/daemon.json"
fi

echo ""
echo "================================"
echo "配置完成！"
echo ""
echo "配置文件路径: /etc/docker/daemon.json"
echo ""
echo "现在可以运行: docker-compose -f docker-compose.jar.yml up -d"
```

使用：
```bash
chmod +x setup-docker-mirror.sh
./setup-docker-mirror.sh
```

---

## 验证镜像加速是否生效

```bash
# 查看当前镜像源
docker info | grep -A 10 "Registry Mirrors"

# 预期输出：
# Registry Mirrors:
#  https://docker.mirrors.ustc.edu.cn/
#  https://hub-mirror.c.163.com/

# 测试拉取速度
time docker pull mysql:8.0
```

---

## 常见问题

### Q: 配置了镜像源还是慢？
**A**: 
1. 检查配置是否生效: `docker info | grep Mirrors`
2. 尝试其他镜像源
3. 可能是网络问题，检查能否ping通镜像地址

### Q: 阿里云镜像地址在哪里获取？
**A**: 
1. 访问 https://cr.console.aliyun.com/
2. 登录后点击左侧 "镜像加速器"
3. 复制你的专属地址

### Q: 镜像源失效了怎么办？
**A**: 尝试其他镜像源，推荐使用多源配置（方案6）

### Q: 公司内网无法访问外网？
**A**: 使用方案3（手动下载导入镜像）或搭建私有Docker Registry

---

## 推荐的镜像源组合

```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com",
    "https://mirror.baidubce.com",
    "https://docker.m.daocloud.io"
  ]
}
```

---

**配置完成后，重新运行部署命令：**
```bash
docker-compose -f docker-compose.jar.yml up -d
```

现在应该可以正常拉取镜像了！🚀
