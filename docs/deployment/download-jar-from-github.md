# 从GitHub下载JAR包的方法

本文档介绍多种从GitHub下载JAR包到 `backend/target/userauth-backend-1.0.0.jar` 的方法。

## 前提条件

1. 你的GitHub仓库需要有Release，且JAR包已上传到Release Assets
2. 或者使用GitHub Actions自动构建并上传Artifact

## 方法1: 使用下载脚本（推荐）

最简单的方法是使用提供的脚本：

```bash
# 设置环境变量
export GITHUB_OWNER="your-github-username"
export GITHUB_REPO="your-repo-name"
export RELEASE_TAG="latest"  # 或指定版本如 "v1.0.0"

# 运行下载脚本
./scripts/download-jar.sh

# 下载完成后部署
docker-compose -f docker-compose.jar.yml up -d --build
```

### 脚本使用示例

```bash
# 从Release下载最新版本
./scripts/download-jar.sh releases

# 指定版本下载
./scripts/download-jar.sh -t v1.0.0 releases

# 使用GitHub CLI下载
./scripts/download-jar.sh gh

# 从Actions Artifact下载（私有仓库）
export GITHUB_TOKEN="ghp_xxxxxxxxxxxx"
./scripts/download-jar.sh actions
```

## 方法2: 手动使用curl/wget下载

### 从GitHub Releases下载

```bash
# 设置变量
OWNER="your-username"
REPO="your-repo"
VERSION="latest"  # 或 "v1.0.0"
JAR_NAME="userauth-backend-1.0.0.jar"

# 创建目录
mkdir -p backend/target

# 使用curl下载
curl -L -o backend/target/$JAR_NAME \
  https://github.com/$OWNER/$REPO/releases/$VERSION/download/$JAR_NAME

# 或使用wget下载
wget -O backend/target/$JAR_NAME \
  https://github.com/$OWNER/$REPO/releases/$VERSION/download/$JAR_NAME
```

### 从GitHub API获取并下载

```bash
# 获取最新Release的下载链接
OWNER="your-username"
REPO="your-repo"
JAR_NAME="userauth-backend-1.0.0.jar"

# 使用GitHub API获取下载URL
DOWNLOAD_URL=$(curl -s https://api.github.com/repos/$OWNER/$REPO/releases/latest | \
  grep -o '"browser_download_url": "[^"]*'$JAR_NAME'"' | \
  cut -d'"' -f4)

# 下载JAR包
curl -L -o backend/target/$JAR_NAME $DOWNLOAD_URL
```

## 方法3: 使用GitHub CLI (gh)

如果你安装了GitHub CLI：

```bash
# 登录GitHub
gh auth login

# 下载最新Release的JAR包
gh release download --repo your-username/your-repo \
  --pattern "userauth-backend-1.0.0.jar" \
  --dir backend/target

# 或下载指定版本
gh release download v1.0.0 \
  --repo your-username/your-repo \
  --pattern "userauth-backend-1.0.0.jar" \
  --dir backend/target
```

## 方法4: 使用GitHub Actions自动上传和下载

### 配置GitHub Actions自动构建

创建 `.github/workflows/build-and-release.yml`：

```yaml
name: Build and Release JAR

on:
  push:
    branches: [ main ]
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build with Maven
      run: |
        cd backend
        mvn clean package -DskipTests
    
    - name: Upload to Release
      if: startsWith(github.ref, 'refs/tags/')
      uses: softprops/action-gh-release@v1
      with:
        files: backend/target/userauth-backend-1.0.0.jar
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    
    - name: Upload Artifact
      if: github.ref == 'refs/heads/main'
      uses: actions/upload-artifact@v3
      with:
        name: backend-jar
        path: backend/target/userauth-backend-1.0.0.jar
        retention-days: 30
```

### 下载Actions Artifact

```bash
# 需要GitHub Token
export GITHUB_TOKEN="ghp_xxxxxxxxxxxx"

# 使用脚本下载
./scripts/download-jar.sh actions
```

## 方法5: 直接集成到部署脚本

创建一个一键部署脚本 `deploy-from-github.sh`：

```bash
#!/bin/bash

# 配置
GITHUB_OWNER="your-username"
GITHUB_REPO="your-repo"
JAR_FILE="userauth-backend-1.0.0.jar"
JAR_PATH="backend/target/$JAR_FILE"

echo "🚀 从GitHub下载并部署后端服务..."

# 1. 创建目录
mkdir -p backend/target

# 2. 下载JAR包
echo "📥 下载JAR包..."
curl -L -o "$JAR_PATH" \
  "https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/latest/download/$JAR_FILE"

# 3. 验证下载
if [ ! -f "$JAR_PATH" ]; then
    echo "❌ 下载失败"
    exit 1
fi

echo "✅ JAR包下载成功"
ls -lh "$JAR_PATH"

# 4. 部署
echo "🐳 启动Docker容器..."
docker-compose -f docker-compose.jar.yml down
docker-compose -f docker-compose.jar.yml up -d --build

# 5. 验证
echo "⏳ 等待服务启动..."
sleep 30

if curl -s http://localhost:8080/actuator/health | grep -q '"status":"UP"'; then
    echo "✅ 部署成功！"
    docker ps --format "table {{.Names}}\t{{.Status}}"
else
    echo "❌ 部署失败，请检查日志"
    docker logs userauth-backend --tail 50
fi
```

使用方法：

```bash
chmod +x deploy-from-github.sh
./deploy-from-github.sh
```

## 常见问题

### Q: 下载速度慢怎么办？

A: 可以尝试以下方法：
1. 使用代理：`export https_proxy=http://proxy:port`
2. 使用国内镜像（如果有）
3. 使用wget的断点续传：`wget -c <url>`

### Q: 私有仓库怎么下载？

A: 需要GitHub Personal Access Token：

```bash
# 创建Token: https://github.com/settings/tokens
export GITHUB_TOKEN="ghp_xxxxxxxxxxxx"

# 使用Token下载
curl -L -H "Authorization: token $GITHUB_TOKEN" \
  -o backend/target/userauth-backend-1.0.0.jar \
  https://github.com/owner/repo/releases/latest/download/userauth-backend-1.0.0.jar
```

### Q: 如何验证下载的JAR包？

```bash
# 检查文件大小
ls -lh backend/target/userauth-backend-1.0.0.jar

# 验证JAR格式
unzip -t backend/target/userauth-backend-1.0.0.jar

# 查看JAR内容
jar tf backend/target/userauth-backend-1.0.0.jar | head -20
```

### Q: 下载后如何部署？

```bash
# 确保JAR包存在
ls backend/target/userauth-backend-1.0.0.jar

# 使用docker-compose.jar.yml部署
docker-compose -f docker-compose.jar.yml up -d --build

# 验证
curl http://localhost:8080/actuator/health
```

## 快速参考

```bash
# 一键下载并部署（修改以下变量后执行）
OWNER="your-username"
REPO="your-repo"
mkdir -p backend/target
curl -L -o backend/target/userauth-backend-1.0.0.jar \
  https://github.com/$OWNER/$REPO/releases/latest/download/userauth-backend-1.0.0.jar
docker-compose -f docker-compose.jar.yml up -d --build
```

---

**注意**: 请将 `your-username` 和 `your-repo` 替换为你的实际GitHub用户名和仓库名。
