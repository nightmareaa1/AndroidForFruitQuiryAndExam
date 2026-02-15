#!/bin/bash

# Docker 镜像拉取修复脚本
# 解决: unable to fetch descriptor which reports content size of zero

echo "🛠️  Docker镜像拉取修复工具"
echo "================================"

# 步骤1: 清理Docker缓存
echo "🧹 步骤1: 清理Docker缓存..."
docker system prune -f
docker image prune -f

# 步骤2: 删除损坏的镜像（如果存在）
echo "🗑️  步骤2: 删除可能损坏的镜像..."
docker rmi mysql:8.0 2>/dev/null || true
docker rmi redis:7-alpine 2>/dev/null || true
docker rmi eclipse-temurin:17-jre-jammy 2>/dev/null || true

# 步骤3: 重启Docker服务
echo "🔄 步骤3: 重启Docker服务..."
sudo systemctl restart docker
sleep 3

# 步骤4: 测试拉取
echo "🧪 步骤4: 测试拉取mysql镜像..."
docker pull mysql:8.0

if [ $? -eq 0 ]; then
    echo "✅ 修复成功！"
else
    echo "❌ 修复失败，尝试其他方案..."
    echo ""
    echo "建议尝试以下方案："
    echo "1. 更换Docker镜像源"
    echo "2. 使用Docker Hub官方源（关闭镜像加速）"
    echo "3. 手动下载镜像文件"
fi
