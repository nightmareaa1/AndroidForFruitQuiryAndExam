# 项目结构总览

本文档提供用户认证系统的完整目录结构说明。

## 完整目录树

```
user-auth-system/
├── .kiro/                                    # Kiro配置和规格文档
│   └── specs/
│       └── user-auth-system/
│           ├── requirements.md               # 需求文档
│           ├── design.md                     # 设计文档
│           └── tasks.md                      # 任务列表
│
├── android-app/                              # Android应用
│   ├── app/
│   │   ├── src/
│   │   │   ├── main/
│   │   │   │   ├── java/com/example/userauth/
│   │   │   │   │   ├── ui/                  # Jetpack Compose UI
│   │   │   │   │   │   ├── auth/            # 认证界面
│   │   │   │   │   │   ├── main/            # 主导航
│   │   │   │   │   │   ├── evaluation/      # 评价系统界面
│   │   │   │   │   │   └── fruit/           # 水果查询界面
│   │   │   │   │   ├── viewmodel/           # ViewModels
│   │   │   │   │   │   ├── AuthViewModel.kt
│   │   │   │   │   │   ├── ModelViewModel.kt
│   │   │   │   │   │   ├── TaskViewModel.kt
│   │   │   │   │   │   ├── EvaluationViewModel.kt
│   │   │   │   │   │   └── FruitViewModel.kt
│   │   │   │   │   ├── data/                # 数据层
│   │   │   │   │   │   ├── api/             # API接口定义
│   │   │   │   │   │   ├── repository/      # Repository实现
│   │   │   │   │   │   └── model/           # 数据模型
│   │   │   │   │   └── domain/              # 业务逻辑
│   │   │   │   └── res/                     # 资源文件
│   │   │   │       ├── values/
│   │   │   │       ├── drawable/
│   │   │   │       └── layout/
│   │   │   ├── test/                        # 单元测试
│   │   │   │   └── java/com/example/userauth/
│   │   │   │       ├── viewmodel/           # ViewModel测试
│   │   │   │       └── repository/          # Repository测试
│   │   │   └── androidTest/                 # UI测试（Espresso）
│   │   │       └── java/com/example/userauth/
│   │   │           ├── LoginScreenTest.kt
│   │   │           ├── RegisterScreenTest.kt
│   │   │           └── EvaluationScreenTest.kt
│   │   ├── build.gradle                     # 应用级Gradle配置
│   │   └── proguard-rules.pro               # ProGuard规则
│   ├── gradle/                              # Gradle wrapper
│   ├── build.gradle                         # 项目级Gradle配置
│   ├── settings.gradle                      # Gradle设置
│   └── README.md                            # Android应用说明
│
├── backend/                                  # Spring Boot后端
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/userauth/
│   │   │   │   ├── controller/              # REST Controllers
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── EvaluationModelController.java
│   │   │   │   │   ├── EvaluationTaskController.java
│   │   │   │   │   ├── EvaluationSubmissionController.java
│   │   │   │   │   ├── FruitController.java
│   │   │   │   │   └── FileController.java
│   │   │   │   ├── service/                 # 业务逻辑
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── UserService.java
│   │   │   │   │   ├── ModelService.java
│   │   │   │   │   ├── TaskService.java
│   │   │   │   │   ├── EvaluationService.java
│   │   │   │   │   ├── FruitService.java
│   │   │   │   │   └── FileStorageService.java
│   │   │   │   ├── repository/              # 数据访问层
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   ├── UserRoleRepository.java
│   │   │   │   │   ├── ModelRepository.java
│   │   │   │   │   ├── TaskRepository.java
│   │   │   │   │   ├── SubmissionRepository.java
│   │   │   │   │   └── FruitRepository.java
│   │   │   │   ├── entity/                  # JPA实体
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── UserRole.java
│   │   │   │   │   ├── EvaluationModel.java
│   │   │   │   │   ├── EvaluationIndicator.java
│   │   │   │   │   ├── EvaluationTask.java
│   │   │   │   │   ├── Sample.java
│   │   │   │   │   ├── EvaluationSubmission.java
│   │   │   │   │   ├── EvaluationRating.java
│   │   │   │   │   ├── Fruit.java
│   │   │   │   │   ├── NutritionData.java
│   │   │   │   │   └── FlavorData.java
│   │   │   │   ├── dto/                     # 数据传输对象
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   ├── ModelRequest.java
│   │   │   │   │   ├── TaskRequest.java
│   │   │   │   │   ├── SubmissionRequest.java
│   │   │   │   │   └── FruitQueryResponse.java
│   │   │   │   ├── security/                # 安全配置
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   ├── SecurityConfig.java
│   │   │   │   │   └── PasswordEncoder.java
│   │   │   │   ├── config/                  # 配置类
│   │   │   │   │   ├── WebConfig.java
│   │   │   │   │   ├── JpaConfig.java
│   │   │   │   │   └── FileStorageConfig.java
│   │   │   │   └── UserAuthApplication.java # 主应用类
│   │   │   └── resources/
│   │   │       ├── application.yml          # 主配置文件
│   │   │       ├── application-dev.yml      # 开发环境配置
│   │   │       ├── application-test.yml     # 测试环境配置
│   │   │       └── db/migration/            # 数据库迁移脚本
│   │   │           ├── V1__create_users_table.sql
│   │   │           ├── V2__create_evaluation_tables.sql
│   │   │           └── V3__create_fruit_tables.sql
│   │   └── test/
│   │       ├── java/com/example/userauth/
│   │       │   ├── service/                 # Service层测试
│   │       │   │   ├── AuthServiceTest.java
│   │       │   │   ├── ModelServiceTest.java
│   │       │   │   └── TaskServiceTest.java
│   │       │   ├── controller/              # Controller集成测试
│   │       │   │   ├── AuthControllerTest.java
│   │       │   │   ├── ModelControllerTest.java
│   │       │   │   └── TaskControllerTest.java
│   │       │   ├── repository/              # Repository测试
│   │       │   │   ├── UserRepositoryTest.java
│   │       │   │   └── ModelRepositoryTest.java
│   │       │   └── property/                # 属性测试（jqwik）
│   │       │       ├── PasswordHashPropertyTest.java
│   │       │       ├── JwtTokenPropertyTest.java
│   │       │       ├── UserRegistrationPropertyTest.java
│   │       │       └── FileUploadPropertyTest.java
│   │       └── resources/
│   │           └── application-test.yml     # 测试配置（H2数据库）
│   ├── pom.xml                              # Maven配置
│   ├── build.gradle                         # Gradle配置（可选）
│   └── README.md                            # 后端服务说明
│
├── docs/                                     # 文档
│   ├── api/                                 # API文档
│   │   ├── openapi.yaml                     # OpenAPI规范
│   │   └── postman_collection.json          # Postman集合
│   ├── deployment/                          # 部署指南
│   │   ├── backend-deploy.md                # 后端部署
│   │   └── android-build.md                 # Android构建
│   ├── architecture/                        # 架构设计
│   │   ├── system-design.md                 # 系统设计
│   │   ├── database-schema.md               # 数据库设计
│   │   └── security-design.md               # 安全设计
│   └── README.md                            # 文档说明
│
├── scripts/                                  # 脚本
│   ├── init-db.sql                          # 数据库初始化
│   ├── test-data.sql                        # 测试数据
│   ├── deploy-backend.sh                    # 后端部署脚本（Linux/Mac）
│   ├── deploy-backend.bat                   # 后端部署脚本（Windows）
│   ├── backup-db.sh                         # 数据库备份
│   ├── restore-db.sh                        # 数据库恢复
│   ├── reset-dev-db.sh                      # 重置开发数据库
│   └── README.md                            # 脚本说明
│
├── .gitignore                               # Git忽略文件
├── README.md                                # 项目主README
└── PROJECT_STRUCTURE.md                     # 本文件
```

## 目录说明

### 根目录文件

| 文件 | 说明 |
|------|------|
| `README.md` | 项目主文档，包含快速开始、技术栈、功能介绍 |
| `.gitignore` | Git忽略规则，排除构建产物、配置文件等 |
| `PROJECT_STRUCTURE.md` | 项目结构说明（本文件） |

### `.kiro/` - Kiro配置

包含项目的规格文档，这是项目的核心文档：

- **requirements.md**: 详细的功能需求和验收标准
- **design.md**: 技术设计、架构、API接口、数据模型
- **tasks.md**: 分阶段的实现任务列表

### `android-app/` - Android应用

完整的Android项目，使用Kotlin和Jetpack Compose：

- **ui/**: 所有UI界面（Compose）
- **viewmodel/**: MVVM架构的ViewModel层
- **data/**: 数据层，包括API客户端和Repository
- **domain/**: 业务逻辑层
- **test/**: 单元测试
- **androidTest/**: UI测试（Espresso）

### `backend/` - Spring Boot后端

完整的Spring Boot项目：

- **controller/**: REST API控制器
- **service/**: 业务逻辑服务
- **repository/**: JPA数据访问层
- **entity/**: JPA实体类
- **dto/**: 数据传输对象
- **security/**: JWT认证和授权
- **config/**: 配置类
- **test/**: 所有测试（单元、集成、属性测试）

### `docs/` - 文档

项目文档集合：

- **api/**: API文档（OpenAPI规范）
- **deployment/**: 部署指南
- **architecture/**: 架构设计文档

### `scripts/` - 脚本

数据库和部署脚本：

- **init-db.sql**: 创建所有数据库表
- **test-data.sql**: 插入测试数据
- **deploy-*.sh/bat**: 部署脚本
- **backup-db.sh**: 数据库备份脚本

## 关键文件位置速查

### 配置文件

| 文件 | 位置 |
|------|------|
| 后端主配置 | `backend/src/main/resources/application.yml` |
| 后端测试配置 | `backend/src/test/resources/application-test.yml` |
| Android Gradle | `android-app/app/build.gradle` |
| Maven配置 | `backend/pom.xml` |

### 测试文件

| 类型 | 位置 |
|------|------|
| 后端单元测试 | `backend/src/test/java/.../service/` |
| 后端集成测试 | `backend/src/test/java/.../controller/` |
| 后端属性测试 | `backend/src/test/java/.../property/` |
| Android单元测试 | `android-app/app/src/test/` |
| Android UI测试 | `android-app/app/src/androidTest/` |

### 文档文件

| 文档 | 位置 |
|------|------|
| 需求文档 | `.kiro/specs/user-auth-system/requirements.md` |
| 设计文档 | `.kiro/specs/user-auth-system/design.md` |
| 任务列表 | `.kiro/specs/user-auth-system/tasks.md` |
| API文档 | `docs/api/openapi.yaml` |
| 部署指南 | `docs/deployment/` |

## 开发工作流

1. **查看需求**: 阅读 `requirements.md`
2. **理解设计**: 阅读 `design.md`
3. **执行任务**: 按照 `tasks.md` 实现功能
4. **编写测试**: 在相应的test目录编写测试
5. **运行测试**: 使用Maven/Gradle或Android Studio
6. **提交代码**: 遵循Git提交规范

## 测试位置说明

**重要**: 测试代码位于各自项目内部，不使用独立的test文件夹

- **后端测试**: `backend/src/test/`
  - 包括单元测试、集成测试、属性测试
  - 使用H2内存数据库，可独立运行
  
- **Android单元测试**: `android-app/app/src/test/`
  - ViewModel和Repository测试
  - 可通过Gradle命令独立运行
  
- **Android UI测试**: `android-app/app/src/androidTest/`
  - Espresso UI测试
  - 需要在Android Studio中手动执行

## 下一步

1. 查看 [项目主README](README.md) 了解快速开始
2. 阅读 [需求文档](.kiro/specs/user-auth-system/requirements.md)
3. 查看 [任务列表](.kiro/specs/user-auth-system/tasks.md) 开始实现
4. 参考各子目录的README了解详细信息
