# 用户认证与水果评测系统 (User Authentication & Fruit Evaluation System)

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green.svg)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-API%2024+-brightgreen.svg)](https://developer.android.com/)

一个完整的企业级应用，包含 **Android移动端** 和 **Spring Boot后端服务**，支持用户认证、权限管理、赛事评价（水果评测）和水果营养查询功能。

## 项目概述

本项目是一个综合性的水果评测与管理系统，主要服务于农业领域的产品质量评估。系统支持多角色用户管理、赛事创建与评价、作品提交与评分、数据可视化展示等功能。

### 核心功能模块

1. **用户认证与权限管理**
   - JWT安全认证
   - 基于角色的访问控制 (RBAC)
   - 密码加密存储 (BCrypt)

2. **赛事评价系统**
   - 评价模型管理（指标、权重配置）
   - 赛事创建与评委分配
   - 作品提交与文件上传
   - 多维度评分与数据分析
   - CSV数据导出

3. **水果营养查询**
   - 营养成分数据查询
   - 风味特征数据管理
   - 数据可视化展示

4. **数据管理**
   - 水果基础数据管理
   - 营养与风味数据管理
   - 评分数据可视化（饼图、雷达图）

## 技术栈

### 后端 (Backend)

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 17+ | 编程语言 |
| Spring Boot | 3.2.1 | Web框架 |
| Spring Security | 6.x | 安全认证与授权 |
| Spring Data JPA | 3.x | 数据持久化 |
| MySQL | 8.0 | 主数据库 |
| Redis | 7.0 | 缓存与会话存储 |
| Flyway | 10.x | 数据库迁移 |
| JWT | 0.12.x | 令牌认证 |
| Maven | 3.8+ | 构建工具 |
| JUnit 5 | 5.x | 单元测试 |
| jqwik | 1.x | 属性测试 |

### Android端

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9+ | 编程语言 |
| Jetpack Compose | 2023.10+ | UI框架 |
| Hilt | 2.48+ | 依赖注入 |
| Retrofit | 2.9+ | HTTP客户端 |
| OkHttp | 4.x | 网络请求 |
| Navigation Compose | 2.7+ | 页面导航 |
| Coroutines | 1.7+ | 异步编程 |
| Flow | Kotlin标准库 | 响应式编程 |
| Coil | 2.x | 图片加载 |

## 项目结构

```
AndroidForFruitQuiryAndExam/
├── android-app/               # Android应用 (Kotlin + Jetpack Compose)
│   ├── app/src/main/java/...  # 主代码
│   │   ├── ui/               # Compose UI界面
│   │   │   ├── screen/       # 页面（登录、赛事、评分等）
│   │   │   ├── components/   # 可复用组件
│   │   │   └── navigation/   # 导航配置
│   │   ├── viewmodel/        # ViewModels (状态管理)
│   │   ├── data/             # 数据层
│   │   │   ├── api/          # Retrofit API接口
│   │   │   ├── repository/   # 数据仓库
│   │   │   └── model/        # 数据模型
│   │   └── di/               # Hilt依赖注入
│   ├── app/src/test/          # 单元测试
│   └── app/src/androidTest/   # UI测试
│
├── backend/                   # Spring Boot后端
│   ├── src/main/java/...      # 主代码
│   │   ├── controller/       # REST API控制器
│   │   ├── service/          # 业务逻辑层
│   │   ├── repository/       # 数据访问层
│   │   ├── entity/           # JPA实体
│   │   ├── dto/              # 数据传输对象
│   │   ├── security/         # 安全配置 (JWT)
│   │   └── config/           # 配置类
│   └── src/test/              # 测试代码
│
├── docs/                      # 项目文档
│   ├── deployment/           # 部署指南
│   ├── development/          # 开发文档
│   └── security/             # 安全文档
│
├── scripts/                   # 自动化脚本
│   ├── dev-start.sh          # 启动开发环境
│   ├── dev-stop.sh           # 停止开发环境
│   └── deploy-*.sh           # 部署脚本
│
└── docker-compose*.yml        # Docker编排文件
```

## 快速开始

### 环境要求

- **Docker** 20.0+ 和 Docker Compose 2.0+
- **JDK** 17+
- **Maven** 3.8+
- **Android Studio** Hedgehog (2023.1.1) 或更高版本
- **Android SDK** API 24+

### 5分钟快速启动

#### 1. 启动后端服务（Docker方式）

```bash
# 启动完整开发环境（MySQL + Redis + 后端）
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps
```

服务启动后：
- 后端API: `http://localhost:8080/api`
- 健康检查: `http://localhost:8080/actuator/health`
- MySQL: `localhost:3306`
- Redis: `localhost:6379`

#### 2. 启动Android应用

1. 使用Android Studio打开 `android-app` 目录
2. 同步Gradle依赖
3. 连接设备或启动模拟器
4. 点击 "Run" 按钮运行应用

> **注意**: 应用已预配置连接容器化后端（模拟器使用 `http://10.0.2.2:8080/`）

### 本地开发模式

#### 后端本地开发

```bash
cd backend

# 编译
mvn clean compile

# 运行（开发模式）
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 运行测试
mvn test

# 生成测试覆盖率报告
mvn clean verify jacoco:report
```

#### Android本地开发

```bash
cd android-app

# 编译
./gradlew build

# 运行单元测试
./gradlew test

# 运行UI测试（需要设备）
./gradlew connectedAndroidTest

# 生成APK
./gradlew assembleDebug
```

## API文档

### 认证接口

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |

### 赛事管理接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/competitions` | 获取所有赛事 | 所有用户 |
| GET | `/api/competitions/{id}` | 获取赛事详情 | 所有用户 |
| POST | `/api/competitions` | 创建赛事 | 管理员 |
| PUT | `/api/competitions/{id}` | 更新赛事 | 创建者 |
| DELETE | `/api/competitions/{id}` | 删除赛事 | 创建者 |
| POST | `/api/competitions/{id}/entries` | 添加参赛作品 | 管理员 |
| POST | `/api/competitions/{id}/submit` | 提交参赛作品 | 所有用户 |
| POST | `/api/competitions/{id}/judges` | 添加评委 | 创建者 |

### 评分接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/ratings/{competitionId}` | 获取评分数据 | 管理员 |
| POST | `/api/ratings` | 提交评分 | 评委 |

### 水果数据接口

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/fruits` | 获取水果列表 |
| GET | `/api/fruits/{id}` | 获取水果详情 |
| GET | `/api/fruitdata/query` | 查询水果数据 |

完整API文档请参考: [docs/api/](docs/api/)

## 测试账号

开发环境预配置的测试用户：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 系统管理员 |
| taskadmin | task123 | 任务管理员 |
| evaluator | eval123 | 普通评价员 |

## 测试策略

### 后端测试

```bash
# 单元测试
cd backend && mvn test

# 集成测试
mvn verify -P integration-test

# 属性测试
mvn test -Dtest=**/*Property

# 测试覆盖率
mvn clean verify jacoco:report
```

### Android测试

```bash
# 单元测试
cd android-app && ./gradlew test

# UI测试（需要设备）
./gradlew connectedAndroidTest

# 覆盖率报告
./gradlew testWithCoverage
```

## 部署指南

### Docker部署

```bash
# 构建镜像
docker build -t userauth-backend ./backend

# 生产环境部署
docker-compose -f docker-compose.prod.yml up -d
```

### 生产环境检查清单

- [ ] 配置强密码的JWT密钥
- [ ] 配置生产数据库
- [ ] 启用SSL/TLS
- [ ] 配置日志聚合
- [ ] 设置监控告警
- [ ] 配置备份策略

详细部署指南请参考: [docs/deployment/](docs/deployment/)

## 项目文档

- **[QUICK_START.md](QUICK_START.md)** - 5分钟快速开始指南
- **[PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)** - 详细项目结构说明
- **[PROJECT_SUMMARY.md](PROJECT_SUMMARY.md)** - 项目功能总结
- **[CONTRIBUTING.md](CONTRIBUTING.md)** - 贡献指南
- **[docs/](docs/)** - 完整文档目录

### 核心规格文档

- [需求文档](.kiro/specs/user-auth-system/requirements.md) - 详细功能需求
- [设计文档](.kiro/specs/user-auth-system/design.md) - 技术架构设计
- [任务列表](.kiro/specs/user-auth-system/tasks.md) - 实现任务清单

## 安全特性

- **认证**: JWT无状态认证
- **授权**: 基于角色的访问控制 (RBAC)
- **密码**: BCrypt加密存储
- **传输**: 支持HTTPS
- **文件上传**: 类型验证、大小限制、安全存储
- **输入验证**: Bean Validation
- **SQL注入防护**: JPA参数化查询

## 性能特性

- **数据库连接池**: HikariCP优化配置
- **缓存**: Redis缓存支持
- **异步处理**: 文件上传异步处理
- **分页查询**: 大数据集分页加载
- **图片加载**: Coil异步图片加载与缓存

## 贡献指南

1. Fork项目仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 创建Pull Request

## 常见问题

### Q: Android无法连接后端？

**A**: 确保使用正确的Base URL：
- 模拟器: `http://10.0.2.2:8080/`
- 真机: 使用电脑IP地址，确保手机和电脑在同一网络

### Q: 数据库连接失败？

**A**: 检查Docker容器状态：
```bash
docker-compose -f docker-compose.dev.yml ps
docker-compose -f docker-compose.dev.yml logs mysql
```

### Q: 如何重置开发环境？

**A**: 
```bash
docker-compose -f docker-compose.dev.yml down -v
docker-compose -f docker-compose.dev.yml up -d
```

## 许可证

本项目采用 MIT License 开源许可证。

## 联系方式

- 📧 Email: [待添加]
- 🐛 Issues: [GitHub Issues]
- 📖 Wiki: [GitHub Wiki]

---

**最后更新**: 2026年2月

**项目状态**: 生产就绪 ✅
