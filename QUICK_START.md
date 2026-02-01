# 快速开始指南

本指南帮助你快速搭建和运行用户认证系统。系统采用容器化开发环境，确保所有开发者环境一致。

## 前置要求

### 基础环境
- ✅ Docker 20.0+ 和 Docker Compose 2.0+
- ✅ Git 2.30+

### 后端开发（可选，用于本地调试）
- ✅ JDK 17 或更高版本
- ✅ Maven 3.8+ 或 Gradle 8.0+
- ✅ IDE: IntelliJ IDEA 或 Eclipse

### Android开发
- ✅ Android Studio Hedgehog (2023.1.1) 或更高版本
- ✅ JDK 17 或更高版本
- ✅ Android SDK API 24+ (最低支持)
- ✅ Android设备或模拟器

## 5分钟容器化快速启动

### 步骤1: 克隆项目

```bash
git clone <repository-url>
cd user-auth-system
```

### 步骤2: 一键启动开发环境

```bash
# 启动完整开发环境（MySQL + Redis + 后端服务）
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps
```

服务启动后：
- 后端服务：`http://localhost:8080`
- MySQL数据库：`localhost:3306`
- Redis缓存：`localhost:6379`
- 管理界面：`http://localhost:8080/actuator/health`

### 步骤3: 初始化数据（自动执行）

容器启动时会自动：
- 创建数据库表结构
- 插入测试数据
- 配置开发环境参数

### 步骤4: 配置Android应用

1. 使用Android Studio打开 `android-app` 目录

2. 应用会自动使用容器化后端：

```kotlin
// 已预配置，无需修改
object ApiConfig {
    const val BASE_URL = "http://10.0.2.2:8080/"  // 模拟器
    // const val BASE_URL = "http://localhost:8080/"  // 真机
}
```

3. 同步Gradle依赖

### 步骤5: 运行Android应用

1. 连接Android设备或启动模拟器
2. 点击 "Run" 按钮（Shift+F10）
3. 应用将安装并启动

## 验证安装

### 测试容器化环境

```bash
# 检查所有服务状态
docker-compose -f docker-compose.dev.yml ps

# 查看后端日志
docker-compose -f docker-compose.dev.yml logs backend

# 测试健康检查
curl http://localhost:8080/actuator/health

# 测试数据库连接
docker-compose -f docker-compose.dev.yml exec mysql mysql -u userauth -p userauth
```

### 测试API端点

```bash
# 测试注册API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'

# 测试登录API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### 测试Android应用

1. 打开应用
2. 点击"注册"
3. 输入用户名和密码
4. 注册成功后登录
5. 查看主界面功能

## 运行测试

### 容器化测试环境

```bash
# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 运行后端测试（使用MySQL容器）
cd backend && mvn test

# 运行Android测试
cd android-app && ./gradlew test

# 运行UI测试（需要模拟器）
cd android-app && ./gradlew connectedAndroidTest

# 清理测试环境
docker-compose -f docker-compose.test.yml down -v
```

### CI/CD测试（自动化）

```bash
# 模拟CI环境测试
.github/workflows/ci.yml  # GitHub Actions会自动执行

# 本地模拟CI测试
act  # 需要安装act工具
```

## 常见问题

### 1. Docker服务启动失败

**问题**: `docker-compose up` 失败

**解决**:
```bash
# 检查Docker是否运行
docker --version
docker-compose --version

# 检查端口占用
netstat -an | grep 8080
netstat -an | grep 3306

# 清理并重新启动
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d
```

### 2. 数据库连接失败

**问题**: `Communications link failure`

**解决**:
```bash
# 检查MySQL容器状态
docker-compose -f docker-compose.dev.yml ps mysql

# 查看MySQL日志
docker-compose -f docker-compose.dev.yml logs mysql

# 重启MySQL容器
docker-compose -f docker-compose.dev.yml restart mysql
```

### 3. Android无法连接后端

**问题**: `Connection refused` 或 `Network error`

**解决**:
- **模拟器**: 使用 `http://10.0.2.2:8080/`（已预配置）
- **真机**: 确保手机和电脑在同一网络，使用电脑IP地址
- **防火墙**: 检查防火墙是否阻止8080端口

```bash
# 查看电脑IP
ipconfig          # Windows
ifconfig          # Mac/Linux
ip addr show      # Linux
```

### 4. 容器资源不足

**问题**: 容器启动慢或失败

**解决**:
```bash
# 检查Docker资源使用
docker stats

# 清理未使用的容器和镜像
docker system prune -a

# 增加Docker内存限制（Docker Desktop设置）
```

### 5. 测试环境问题

**问题**: 测试失败或环境不一致

**解决**:
```bash
# 重置测试环境
docker-compose -f docker-compose.test.yml down -v
docker-compose -f docker-compose.test.yml up -d

# 检查测试数据库
docker-compose -f docker-compose.test.yml exec mysql-test mysql -u test_user -p userauth_test
```

## 开发模式配置

### 容器化开发环境

默认的 `docker-compose.dev.yml` 已配置开发模式：

```yaml
# 自动配置的开发环境特性
services:
  backend:
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_JPA_SHOW_SQL=true
    volumes:
      - ./backend/src:/app/src  # 热重载支持
  
  mysql:
    environment:
      - MYSQL_ROOT_PASSWORD=dev_password
    volumes:
      - mysql_dev_data:/var/lib/mysql  # 数据持久化
```

### 本地开发调试

如需本地调试后端代码：

```bash
# 停止容器化后端，保留数据库
docker-compose -f docker-compose.dev.yml stop backend

# 本地启动后端（连接容器化数据库）
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Android开发配置

在 `android-app/app/build.gradle` 中：

```gradle
android {
    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
            applicationIdSuffix ".debug"
            buildConfigField "String", "BASE_URL", "\"http://10.0.2.2:8080/\""
        }
        release {
            minifyEnabled true
            buildConfigField "String", "BASE_URL", "\"https://your-production-domain.com/\""
        }
    }
}
```

## 测试数据

容器化环境自动创建的测试用户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |
| taskadmin | task123 | 任务管理员 |
| evaluator | eval123 | 普通评价员 |

## 环境管理

### 开发环境
```bash
# 启动开发环境
docker-compose -f docker-compose.dev.yml up -d

# 停止开发环境
docker-compose -f docker-compose.dev.yml down

# 重置开发环境（清除数据）
docker-compose -f docker-compose.dev.yml down -v
```

### 测试环境
```bash
# 启动测试环境
docker-compose -f docker-compose.test.yml up -d

# 运行测试
mvn test  # 后端测试
./gradlew test  # Android测试

# 清理测试环境
docker-compose -f docker-compose.test.yml down -v
```

### 生产环境
```bash
# 启动生产环境（需要配置环境变量）
docker-compose -f docker-compose.prod.yml up -d

# 查看生产环境状态
docker-compose -f docker-compose.prod.yml ps
```

## 下一步

1. ✅ **阅读文档**
   - [需求文档](.kiro/specs/user-auth-system/requirements.md)
   - [设计文档](.kiro/specs/user-auth-system/design.md)
   - [任务列表](.kiro/specs/user-auth-system/tasks.md)

2. ✅ **开始开发**
   - 查看 [任务列表](.kiro/specs/user-auth-system/tasks.md)
   - 按照任务顺序实现功能
   - 编写测试验证功能

3. ✅ **参考文档**
   - [Android README](android-app/README.md)
   - [后端README](backend/README.md)
   - [项目结构](PROJECT_STRUCTURE.md)

4. ✅ **部署上线**
   - [后端部署指南](docs/deployment/backend-deploy.md)
   - [Android构建指南](docs/deployment/android-build.md)

## 获取帮助

- 📖 查看 [项目文档](docs/)
- 🐛 提交 Issue
- 💬 联系团队

## 许可证

[待定]
