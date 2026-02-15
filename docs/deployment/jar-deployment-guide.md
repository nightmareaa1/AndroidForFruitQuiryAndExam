# JAR包部署方案说明

本文档说明两种Docker部署方案的区别和使用场景。

## 方案对比

| 特性 | Dockerfile(构建版) | Dockerfile.jar(预编译版) |
|------|-------------------|-------------------------|
| **构建时间** | 5-10分钟(需下载依赖+编译) | 10-30秒(直接复制JAR) |
| **镜像大小** | ~500MB(包含Maven缓存) | ~280MB(仅JRE+JAR) |
| **适合场景** | 开发环境、CI/CD流水线 | 生产环境、快速部署 |
| **网络要求** | 需要下载Maven依赖 | 仅需下载基础镜像 |
| **可重复性** | 高(每次从源码构建) | 中(依赖预编译JAR) |
| **服务器资源** | 需要较多内存构建 | 轻量级部署 |

## 文件说明

### 1. Dockerfile.jar
**用途**: 直接使用预编译的JAR包创建镜像
**位置**: `backend/Dockerfile.jar`

**特点**:
- 使用 `eclipse-temurin:17-jre-jammy` 作为基础镜像(更轻量)
- 直接从 `target/` 目录复制JAR包
- 无需Maven构建过程
- 镜像体积小约40%

### 2. docker-compose.jar.yml
**用途**: 使用JAR包部署的完整配置
**位置**: `docker-compose.jar.yml`

**与 docker-compose.light.yml 的区别**:
```yaml
# light版本 - 构建时编译
backend:
  build:
    context: ./backend
    dockerfile: Dockerfile  # 多阶段构建,编译源码

# jar版本 - 直接使用预编译JAR
backend:
  build:
    context: ./backend
    dockerfile: Dockerfile.jar  # 仅复制JAR包
```

### 3. scripts/deploy-jar.sh
**用途**: 自动化部署脚本
**位置**: `scripts/deploy-jar.sh`

**功能**:
- ✅ 检查Docker环境
- ✅ 自动创建目录结构
- ✅ 支持从GitHub Releases下载JAR包
- ✅ 环境变量配置检查
- ✅ 自动等待服务启动
- ✅ 部署状态验证
- ✅ 彩色输出,友好的用户体验

## 使用流程

### 方式1: 本地构建JAR包后上传

**步骤1: 在本地构建JAR包**
```bash
# 在本地开发机
cd backend
mvn clean package -DskipTests

# 确认JAR包生成
ls -lh target/userauth-backend-1.0.0.jar
```

**步骤2: 上传JAR包到服务器**
```bash
# 使用scp上传到服务器
scp backend/target/userauth-backend-1.0.0.jar user@server:/opt/userauth/backend/target/

# 同时上传docker-compose配置
scp docker-compose.jar.yml user@server:/opt/userauth/
```

**步骤3: 在服务器上部署**
```bash
ssh user@server
cd /opt/userauth
docker compose -f docker-compose.jar.yml up -d
```

### 方式2: 使用自动部署脚本

```bash
# 1. 上传部署脚本到服务器
scp scripts/deploy-jar.sh user@server:/opt/userauth/

# 2. 同时上传JAR包
scp backend/target/userauth-backend-1.0.0.jar user@server:/opt/userauth/backend/target/

# 3. SSH登录并执行部署
ssh user@server
cd /opt/userauth
chmod +x deploy-jar.sh
./deploy-jar.sh
```

### 方式3: 集成GitHub Actions自动部署

```yaml
# .github/workflows/deploy.yml
name: Deploy to Server

on:
  push:
    branches: [ main ]

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Build JAR
      run: |
        cd backend
        mvn clean package -DskipTests
    
    - name: Upload to server and deploy
      uses: appleboy/scp-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        source: "backend/target/userauth-backend-1.0.0.jar,docker-compose.jar.yml"
        target: "/opt/userauth"
    
    - name: Deploy
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        script: |
          cd /opt/userauth
          docker compose -f docker-compose.jar.yml up -d --build
```

## 性能对比

### 镜像构建时间

| 方案 | 首次构建 | 后续构建(有缓存) |
|------|---------|----------------|
| Dockerfile(多阶段) | 8-12分钟 | 3-5分钟 |
| Dockerfile.jar | 15-30秒 | 10-15秒 |

### 镜像大小

```bash
# 查看镜像大小
docker images userauth-backend

# 预期结果:
# REPOSITORY          TAG       SIZE
# userauth-backend    latest    520MB  (构建版)
# userauth-backend    jar       280MB  (JAR版)
```

### 内存占用

两种方案运行时的内存占用相同:
- Backend: 400-500MB
- MySQL: 300MB
- Redis: 64MB

## 推荐用法

### 开发环境
使用 `docker-compose.dev.yml` 或 `docker-compose.light.yml`:
- 自动编译最新代码
- 支持热重载
- 方便调试

### 测试环境
使用 `docker-compose.jar.yml`:
- 快速部署特定版本
- 与生产环境一致
- 便于版本回滚

### 生产环境
强烈推荐使用 `docker-compose.jar.yml`:
- 部署速度快
- 镜像体积小
- 不依赖外部Maven仓库
- 更好的版本控制

## 版本管理建议

### JAR包命名规范

```
userauth-backend-1.0.0.jar          # 正式发布版本
userauth-backend-1.0.1-SNAPSHOT.jar # 开发版本
userauth-backend-1.0.0-20240215.jar # 带日期版本
```

### 备份策略

```bash
# 部署前备份旧版本
mkdir -p backup
cp target/userauth-backend-1.0.0.jar backup/userauth-backend-1.0.0.jar.$(date +%Y%m%d_%H%M%S)

# 保留最近5个版本
ls -t backup/*.jar | tail -n +6 | xargs rm -f
```

### 快速回滚

```bash
# 查看最新备份
ls -lh backup/

# 回滚到上一版本
cp backup/userauth-backend-1.0.0.jar.20240215_120000 target/userauth-backend-1.0.0.jar
docker compose -f docker-compose.jar.yml up -d --build
```

## 常见问题

### Q: JAR包从哪里获取?
**A**: 三种方式:
1. 本地构建: `mvn clean package`
2. CI/CD构建: GitHub Actions/Jenkins等
3. GitHub Releases: 下载预发布版本

### Q: 如何确保JAR包最新?
**A**: 在部署脚本中添加版本检查:
```bash
# 对比本地和远程JAR包MD5
LOCAL_MD5=$(md5sum target/userauth-backend-1.0.0.jar | cut -d' ' -f1)
REMOTE_MD5=$(curl -s https://your-cdn.com/jar.md5)

if [ "$LOCAL_MD5" != "$REMOTE_MD5" ]; then
    echo "发现新版本,正在下载..."
    curl -O https://your-cdn.com/userauth-backend-1.0.0.jar
fi
```

### Q: 多服务器如何同步部署?
**A**: 使用Ansible或脚本批量部署:
```bash
# servers.txt 包含所有服务器IP
for server in $(cat servers.txt); do
    scp target/userauth-backend-1.0.0.jar user@$server:/opt/userauth/backend/target/
    ssh user@$server "cd /opt/userauth && docker compose -f docker-compose.jar.yml up -d"
done
```

## 总结

**使用JAR包部署的优势**:
- ✅ 部署速度快(10秒 vs 10分钟)
- ✅ 不依赖外部网络(Maven仓库)
- ✅ 镜像体积小(280MB vs 520MB)
- ✅ 更好的版本控制
- ✅ 适合自动化部署

**推荐使用场景**:
- 🚀 生产环境部署
- 🧪 测试环境快速搭建
- 📦 离线环境部署
- 🤖 CI/CD自动化流程

---

**文档版本**: 1.0  
**最后更新**: 2026-02-15
