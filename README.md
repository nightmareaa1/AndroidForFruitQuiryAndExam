# 用户认证系统 (User Authentication System)

这是一个完整的用户认证系统，包含Android移动应用和Spring Boot后端服务，支持用户注册登录、权限管理、赛事评价和水果营养查询功能。

## 项目结构

```
user-auth-system/
├── android-app/          # Android应用（Kotlin + Jetpack Compose）
├── backend/              # Spring Boot后端服务
├── docs/                 # 文档和设计资料
├── scripts/              # 部署和工具脚本
└── .kiro/specs/          # 项目规格文档
```

## 快速开始

### 后端服务

1. 进入后端目录：
   ```bash
   cd backend
   ```

2. 配置数据库连接（编辑 `src/main/resources/application.yml`）

3. 运行后端服务：
   ```bash
   mvn spring-boot:run
   # 或
   gradle bootRun
   ```

4. 运行测试：
   ```bash
   mvn test
   # 或
   gradle test
   ```

### Android应用

1. 使用Android Studio打开 `android-app` 目录

2. 配置后端API地址（在代码中修改Base URL）

3. 同步Gradle依赖

4. 运行应用到设备或模拟器

5. 运行测试：
   ```bash
   ./gradlew test           # 单元测试
   ./gradlew connectedTest  # UI测试（需要设备）
   ```

## 文档

- **需求文档**: `.kiro/specs/user-auth-system/requirements.md`
- **设计文档**: `.kiro/specs/user-auth-system/design.md`
- **任务列表**: `.kiro/specs/user-auth-system/tasks.md`
- **API文档**: `docs/api/`
- **部署指南**: `docs/deployment/`
- **架构设计**: `docs/architecture/`

## 技术栈

### 后端
- Spring Boot 3.x
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.x
- Flyway/Liquibase（数据库迁移）
- jqwik（属性测试）

### Android
- Kotlin
- Jetpack Compose
- Retrofit（HTTP客户端）
- Hilt（依赖注入）
- Navigation Compose
- Espresso（UI测试）

## 主要功能

1. **用户认证**
   - 用户注册
   - 用户登录
   - JWT令牌认证

2. **权限管理**
   - 基于角色的访问控制（RBAC）
   - 系统管理员、任务管理员、普通评价员角色

3. **赛事评价系统**
   - 评价模型管理
   - 评价任务管理
   - 在线评价
   - 数据展示和导出

4. **水果营养查询**
   - 营养成分查询
   - 风味特征查询

## 测试策略

### 后端测试（可自动化）
- 单元测试：Service层、Repository层
- 集成测试：API端点、数据库（使用H2内存数据库）
- 属性测试：使用jqwik验证正确性属性

### Android测试
- 单元测试：ViewModel、Repository（可自动化）
- UI测试：使用Espresso（需要Android Studio手动执行）

## 开发指南

1. 查看 `tasks.md` 了解实现计划
2. 按照任务顺序逐步实现功能
3. 每个阶段完成后运行相关测试
4. 遵循设计文档中的架构和接口定义

## 部署

详细的部署指南请参考：
- 后端部署：`docs/deployment/backend-deploy.md`
- Android构建：`docs/deployment/android-build.md`

## 许可证

[待定]

## 联系方式

[待定]
