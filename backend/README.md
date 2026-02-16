# 后端服务 (Spring Boot Backend)

Spring Boot后端服务，提供RESTful API支持用户认证、赛事评价（水果评测）和水果营养查询功能。

## 功能特性

### 用户认证与授权
- ✅ 用户注册与登录
- ✅ JWT无状态认证
- ✅ 基于角色的访问控制 (RBAC)
- ✅ 密码BCrypt加密

### 赛事评价系统
- ✅ 评价模型管理（创建、查询、删除）
- ✅ 赛事管理（CRUD操作）
- ✅ 评委分配系统
- ✅ 参赛作品管理（提交、审核）
- ✅ 文件上传与存储
- ✅ 多维度评分系统
- ✅ 评分数据统计与CSV导出

### 水果数据管理
- ✅ 水果基础数据管理
- ✅ 营养成分数据管理
- ✅ 风味特征数据管理
- ✅ 数据查询与筛选

### 基础设施
- ✅ MySQL 8.0 主数据库
- ✅ Redis 7.0 缓存
- ✅ Flyway 数据库迁移
- ✅ Docker 容器化
- ✅ 健康检查与监控
- ✅ 结构化日志

## 技术栈

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
| TestContainers | 1.x | 集成测试 |

## 项目结构

```
backend/
├── src/main/java/com/example/userauth/
│   ├── UserAuthApplication.java           # 主应用类
│   │
│   ├── controller/                        # REST API控制器
│   │   ├── AuthController.java            # 认证接口
│   │   ├── CompetitionController.java     # 赛事管理接口
│   │   ├── EvaluationModelController.java # 评价模型接口
│   │   ├── RatingController.java          # 评分接口
│   │   ├── FruitController.java           # 水果查询接口
│   │   ├── FruitAdminController.java      # 水果管理接口
│   │   ├── FruitDataController.java       # 水果数据接口
│   │   ├── FruitDataAdminController.java  # 数据管理接口
│   │   ├── FileController.java            # 文件接口
│   │   └── HealthController.java          # 健康检查接口
│   │
│   ├── service/                           # 业务逻辑层
│   │   ├── UserService.java               # 用户服务
│   │   ├── CompetitionService.java        # 赛事服务
│   │   ├── EvaluationModelService.java    # 模型服务
│   │   ├── RatingService.java             # 评分服务
│   │   ├── RatingDataService.java         # 评分数据服务
│   │   ├── FruitQueryService.java         # 水果查询服务
│   │   ├── FileStorageService.java        # 文件存储服务
│   │   ├── FileValidationService.java     # 文件验证服务
│   │   ├── TokenService.java              # 令牌服务
│   │   └── PasswordService.java           # 密码服务
│   │
│   ├── repository/                        # 数据访问层
│   │   ├── UserRepository.java
│   │   ├── CompetitionRepository.java
│   │   ├── CompetitionEntryRepository.java
│   │   ├── CompetitionJudgeRepository.java
│   │   ├── CompetitionRatingRepository.java
│   │   ├── EvaluationModelRepository.java
│   │   ├── EvaluationParameterRepository.java
│   │   ├── FruitRepository.java
│   │   ├── FruitDataRepository.java
│   │   ├── FruitDataFieldRepository.java
│   │   ├── FruitFileRepository.java
│   │   ├── NutritionDataRepository.java
│   │   └── FlavorDataRepository.java
│   │
│   ├── entity/                            # JPA实体
│   │   ├── User.java                      # 用户实体
│   │   ├── Competition.java               # 赛事实体
│   │   ├── CompetitionEntry.java          # 参赛作品实体
│   │   ├── CompetitionJudge.java          # 赛事评委实体
│   │   ├── CompetitionRating.java         # 评分实体
│   │   ├── EvaluationModel.java           # 评价模型实体
│   │   ├── EvaluationParameter.java       # 评价参数实体
│   │   ├── Fruit.java                     # 水果实体
│   │   ├── FruitData.java                 # 水果数据实体
│   │   ├── FruitDataField.java            # 数据字段实体
│   │   ├── FruitFile.java                 # 文件实体
│   │   ├── NutritionData.java             # 营养数据实体
│   │   └── FlavorData.java                # 风味数据实体
│   │
│   ├── dto/                               # 数据传输对象
│   │   ├── RegisterRequest.java           # 注册请求
│   │   ├── LoginRequest.java              # 登录请求
│   │   ├── AuthResponse.java              # 认证响应
│   │   ├── UserResponse.java              # 用户响应
│   │   ├── CompetitionRequest.java        # 赛事请求
│   │   ├── CompetitionResponse.java       # 赛事响应
│   │   ├── EntryRequest.java              # 作品请求
│   │   ├── EntrySubmitResponse.java       # 提交响应
│   │   ├── ModelRequest.java              # 模型请求
│   │   ├── ModelResponse.java             # 模型响应
│   │   ├── RatingRequest.java             # 评分请求
│   │   ├── RatingResponse.java            # 评分响应
│   │   ├── CompetitionRatingDataResponse.java # 评分数据响应
│   │   ├── FruitRequest.java              # 水果请求
│   │   ├── FruitResponse.java             # 水果响应
│   │   ├── FruitQueryResponse.java        # 查询响应
│   │   ├── NutritionDataRequest.java      # 营养数据请求
│   │   ├── FlavorDataRequest.java         # 风味数据请求
│   │   └── EntryStatusUpdateRequest.java  # 状态更新请求
│   │
│   ├── security/                          # 安全配置
│   │   ├── JwtService.java                # JWT服务
│   │   ├── JwtAuthenticationFilter.java   # JWT认证过滤器
│   │   ├── CustomUserDetailsService.java  # 用户详情服务
│   │   ├── PasswordPolicyValidator.java   # 密码策略验证
│   │   ├── KeyManagementService.java      # 密钥管理服务
│   │   ├── RequireAdmin.java              # 管理员权限注解
│   │   └── AdminAccessAspect.java         # 管理员权限切面
│   │
│   ├── config/                            # 配置类
│   │   ├── SecurityConfig.java            # 安全配置
│   │   ├── WebConfig.java                 # Web配置
│   │   ├── RedisConfig.java               # Redis配置
│   │   ├── JacksonConfig.java             # Jackson配置
│   │   ├── MetricsConfig.java             # 指标配置
│   │   ├── AppProperties.java             # 应用属性
│   │   ├── DataInitializer.java           # 数据初始化
│   │   ├── EnvironmentValidator.java      # 环境验证
│   │   ├── DockerSecretsConfig.java       # Docker密钥配置
│   │   └── SensitiveDataMaskingFilter.java # 敏感数据掩码
│   │
│   └── exception/                         # 异常处理
│       └── GlobalExceptionHandler.java    # 全局异常处理器
│
├── src/main/resources/
│   ├── application.yml                    # 主配置
│   ├── application-dev.yml                # 开发环境配置
│   ├── application-test.yml               # 测试环境配置
│   └── db/migration/                      # 数据库迁移脚本
│       ├── V1__Initial_schema.sql
│       ├── V2__Add_competition_tables.sql
│       ├── V3__Add_evaluation_tables.sql
│       ├── V4__Add_fruit_tables.sql
│       └── ...
│
├── src/test/java/com/example/userauth/    # 测试代码
│   ├── service/                           # Service层测试
│   ├── controller/                        # Controller层测试
│   ├── repository/                        # Repository层测试
│   └── property/                          # 属性测试
│
└── pom.xml                                # Maven配置
```

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Docker and Docker Compose（可选）

### 方式一：Docker快速启动（推荐）

```bash
# 启动完整开发环境（MySQL + Redis + 后端）
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps

# 查看日志
docker-compose -f docker-compose.dev.yml logs -f backend
```

服务启动后：
- 后端API: `http://localhost:8080/api`
- 健康检查: `http://localhost:8080/actuator/health`
- MySQL: `localhost:3306`
- Redis: `localhost:6379`

### 方式二：本地开发

```bash
# 1. 启动MySQL和Redis（使用Docker）
docker-compose -f docker-compose.dev.yml up -d mysql redis

# 2. 编译
mvn clean compile

# 3. 运行（开发模式）
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 验证安装

```bash
# 测试健康检查
curl http://localhost:8080/actuator/health

# 测试注册API
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'

# 测试登录API
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Password123!"}'
```

## API文档

### 认证接口

| 方法 | 端点 | 描述 |
|------|------|------|
| POST | `/api/auth/register` | 用户注册 |
| POST | `/api/auth/login` | 用户登录 |

**注册请求示例：**
```json
{
  "username": "testuser",
  "password": "Password123!"
}
```

**登录响应示例：**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "testuser",
  "role": "USER"
}
```

### 赛事管理接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/competitions` | 获取所有赛事 | 所有用户 |
| GET | `/api/competitions/created` | 获取创建的赛事 | 所有用户 |
| GET | `/api/competitions/judged` | 获取评审的赛事 | 所有用户 |
| GET | `/api/competitions/{id}` | 获取赛事详情 | 所有用户 |
| POST | `/api/competitions` | 创建赛事 | 管理员 |
| PUT | `/api/competitions/{id}` | 更新赛事 | 创建者 |
| DELETE | `/api/competitions/{id}` | 删除赛事 | 创建者 |

### 参赛作品接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/competitions/{id}/entries` | 批量添加作品 | 管理员 |
| POST | `/api/competitions/{id}/submit` | 提交作品 | 所有用户 |
| GET | `/api/competitions/{id}/entries` | 获取作品列表 | 管理员 |
| PUT | `/api/competitions/{competitionId}/entries/{entryId}` | 更新作品 | 所有者 |
| DELETE | `/api/competitions/{competitionId}/entries/{entryId}` | 删除作品 | 所有者 |

### 评委管理接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/competitions/{id}/judges` | 添加评委 | 创建者 |
| DELETE | `/api/competitions/{id}/judges/{judgeId}` | 移除评委 | 创建者 |

### 评分接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| POST | `/api/ratings` | 提交评分 | 评委 |
| GET | `/api/ratings/{competitionId}` | 获取评分数据 | 管理员 |

### 评价模型接口

| 方法 | 端点 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/evaluation-models` | 获取所有模型 | 所有用户 |
| POST | `/api/evaluation-models` | 创建模型 | 管理员 |
| DELETE | `/api/evaluation-models/{id}` | 删除模型 | 管理员 |

### 水果数据接口

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/fruits` | 获取水果列表 |
| GET | `/api/fruits/{id}` | 获取水果详情 |
| GET | `/api/fruitdata/query` | 查询水果数据 |
| POST | `/api/fruits` | 创建水果 |
| PUT | `/api/fruits/{id}` | 更新水果 |
| DELETE | `/api/fruits/{id}` | 删除水果 |

### 文件接口

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/api/files/{filename}` | 获取文件 |

### 健康检查接口

| 方法 | 端点 | 描述 |
|------|------|------|
| GET | `/actuator/health` | 健康检查 |
| GET | `/actuator/info` | 应用信息 |
| GET | `/actuator/metrics` | 指标数据 |

## 测试

### 测试结构

```
src/test/java/com/example/userauth/
├── service/                    # Service层单元测试
│   ├── UserServiceTest.java
│   ├── CompetitionServiceTest.java
│   └── ...
│
├── controller/                 # Controller层集成测试
│   ├── AuthControllerTest.java
│   ├── CompetitionControllerTest.java
│   └── ...
│
├── repository/                 # Repository层测试
│   ├── UserRepositoryTest.java
│   └── ...
│
└── property/                   # 属性测试（jqwik）
    ├── PasswordHashPropertyTest.java
    ├── JwtTokenPropertyTest.java
    └── ...
```

### 运行测试

```bash
# 运行所有单元测试
mvn test

# 运行集成测试
mvn verify -P integration-test

# 运行属性测试
mvn test -Dtest=**/*Property

# 生成测试覆盖率报告
mvn clean verify jacoco:report

# 运行特定测试类
mvn test -Dtest=UserServiceTest
```

### 测试覆盖率

覆盖率报告生成在 `target/site/jacoco/index.html`

## 数据库

### 数据库迁移（Flyway）

```bash
# 运行迁移
mvn flyway:migrate

# 清理数据库（仅开发环境）
mvn flyway:clean

# 验证迁移
mvn flyway:validate

# 查看迁移信息
mvn flyway:info
```

### 核心表结构

#### 用户相关
- `users` - 用户表
- `user_roles` - 用户角色表

#### 赛事相关
- `competitions` - 赛事表
- `competition_entries` - 参赛作品表
- `competition_judges` - 赛事评委表
- `competition_ratings` - 赛事评分表
- `scores` - 分数详情表

#### 评价模型相关
- `evaluation_models` - 评价模型表
- `evaluation_parameters` - 评价参数表

#### 水果相关
- `fruits` - 水果表
- `fruit_data` - 水果数据表
- `fruit_data_fields` - 数据字段表
- `fruit_files` - 文件表
- `nutrition_data` - 营养数据表
- `flavor_data` - 风味数据表

## 配置

### 应用配置（application.yml）

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/userauth_dev
    username: userauth
    password: password
  
  redis:
    host: localhost
    port: 6379
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true

jwt:
  secret: your-secret-key-here
  expiration: 86400000  # 24小时

file:
  upload-path: ./uploads
  max-size: 10MB
```

### 环境变量

| 变量名 | 描述 | 默认值 |
|--------|------|--------|
| `SPRING_PROFILES_ACTIVE` | 激活的配置文件 | `dev` |
| `SPRING_DATASOURCE_URL` | 数据库URL | `jdbc:mysql://localhost:3306/userauth_dev` |
| `SPRING_DATASOURCE_USERNAME` | 数据库用户名 | `userauth` |
| `SPRING_DATASOURCE_PASSWORD` | 数据库密码 | `password` |
| `SPRING_REDIS_HOST` | Redis主机 | `localhost` |
| `SPRING_REDIS_PORT` | Redis端口 | `6379` |
| `JWT_SECRET` | JWT密钥 | 随机生成 |
| `UPLOAD_PATH` | 文件上传路径 | `./uploads` |

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t userauth-backend .

# 生产环境部署
docker-compose -f docker-compose.prod.yml up -d
```

### 生产环境检查清单

- [ ] 配置强JWT密钥
- [ ] 配置生产数据库
- [ ] 配置Redis集群
- [ ] 启用SSL/TLS
- [ ] 配置日志聚合
- [ ] 设置监控告警
- [ ] 配置备份策略
- [ ] 配置防火墙规则

## 安全特性

- **认证**: JWT无状态认证
- **授权**: 基于角色的访问控制
- **密码**: BCrypt加密存储（strength 12）
- **传输**: 支持HTTPS
- **文件上传**: 
  - 文件类型验证（JPG, PNG, WEBP）
  - 文件大小限制（10MB）
  - 安全文件名生成
- **输入验证**: Bean Validation
- **SQL注入防护**: JPA参数化查询
- **敏感数据**: 日志脱敏

## 性能优化

- **数据库**: HikariCP连接池
- **缓存**: Redis缓存支持
- **异步**: @Async异步处理
- **压缩**: GZIP响应压缩
- **分页**: 大数据集分页加载

## 监控

### 健康检查

- `/actuator/health` - 应用健康状态
- `/actuator/info` - 应用信息
- `/actuator/metrics` - 指标数据

### 日志

- 开发环境: 控制台输出
- 生产环境: JSON格式结构化日志
- 日志级别: INFO (可配置)

## 常见问题

### Q: 数据库连接失败？

**A**: 
```bash
# 检查MySQL容器
docker-compose -f docker-compose.dev.yml ps mysql

# 查看MySQL日志
docker-compose -f docker-compose.dev.yml logs mysql

# 测试连接
mysql -h localhost -u userauth -p userauth_dev
```

### Q: Redis连接失败？

**A**:
```bash
# 检查Redis容器
docker-compose -f docker-compose.dev.yml ps redis

# 测试连接
redis-cli -h localhost ping
```

### Q: JWT令牌无效？

**A**:
- 检查JWT密钥配置
- 验证令牌是否过期
- 检查系统时间同步

### Q: 文件上传失败？

**A**:
- 检查上传目录权限
- 确认文件大小不超过限制
- 验证文件类型

### Q: 编译失败？

**A**:
```bash
# 清理并重新编译
mvn clean compile

# 跳过测试编译
mvn clean compile -DskipTests
```

## 开发指南

### 代码规范

- 遵循Google Java Style Guide
- 使用Spotless格式化: `mvn spotless:apply`
- 遵循Spring Boot最佳实践

### Git工作流

1. 从 `develop` 分支创建特性分支
2. 编写功能代码和测试
3. 提交Pull Request
4. 代码审查后合并

## 参考文档

- [项目主README](../README.md)
- [快速开始](../QUICK_START.md)
- [项目结构](../PROJECT_STRUCTURE.md)
- [Android README](../android-app/README.md)

---

**最后更新**: 2026年2月

**状态**: 生产就绪 ✅