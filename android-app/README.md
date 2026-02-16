# Android水果评测应用 (Android Fruit Evaluation App)

Android客户端应用，用于用户认证、赛事评价（水果评测）和水果营养查询。采用 Kotlin + Jetpack Compose 构建，遵循MVVM架构。

## 功能特性

### 已完成功能

1. **用户认证**
   - 用户注册
   - 用户登录
   - JWT令牌管理
   - 自动登录

2. **赛事评价系统**
   - 评价模型管理（查看、创建）
   - 赛事管理（创建、编辑、删除）
   - 参赛作品提交（支持图片上传）
   - 作品评审与评分
   - 评分数据可视化（饼图、雷达图）
   - CSV数据导出

3. **水果营养查询**
   - 水果列表查看
   - 营养成分查询
   - 风味特征查询
   - 数据可视化展示

4. **管理功能**
   - 水果数据管理
   - 营养数据管理
   - 风味数据管理

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 1.9+ | 编程语言 |
| Jetpack Compose | 2023.10+ | UI框架 |
| Hilt | 2.48+ | 依赖注入 |
| Retrofit | 2.9+ | HTTP客户端 |
| Navigation Compose | 2.7+ | 页面导航 |
| Coroutines | 1.7+ | 异步编程 |
| Flow | Kotlin标准库 | 响应式编程 |
| Coil | 2.x | 图片加载 |
| Gson | 2.10+ | JSON序列化 |

## 项目结构

```
android-app/
├── app/src/main/java/com/example/userauth/
│   ├── MainActivity.kt                 # 主Activity
│   ├── UserAuthApplication.kt          # Application类
│   │
│   ├── ui/                             # UI层
│   │   ├── screen/                     # 页面
│   │   │   ├── LoginScreen.kt          # 登录页
│   │   │   ├── RegisterScreen.kt       # 注册页
│   │   │   ├── MainScreen.kt           # 主页面
│   │   │   ├── CompetitionScreen.kt    # 赛事列表
│   │   │   ├── CompetitionAddScreen.kt # 创建赛事
│   │   │   ├── CompetitionEditScreen.kt# 编辑赛事
│   │   │   ├── CompetitionManagementScreen.kt # 赛事管理
│   │   │   ├── EntryAddScreen.kt       # 添加作品
│   │   │   ├── EntryEditScreen.kt      # 编辑作品
│   │   │   ├── EntryReviewScreen.kt    # 作品评审
│   │   │   ├── RatingScreen.kt         # 评分页
│   │   │   ├── ScoreScreen.kt          # 分数展示
│   │   │   ├── DataDisplayScreen.kt    # 数据展示
│   │   │   ├── DataDisplayDetailScreen.kt # 数据详情
│   │   │   ├── ModelManagementScreen.kt # 模型管理
│   │   │   ├── FruitNutritionScreen.kt # 水果营养
│   │   │   ├── FruitManagementScreen.kt # 水果管理
│   │   │   └── AdminScreen.kt          # 管理页面
│   │   │
│   │   ├── components/                 # 可复用组件
│   │   │   ├── Buttons.kt              # 按钮组件
│   │   │   ├── Cards.kt                # 卡片组件
│   │   │   ├── Chips.kt                # 标签组件
│   │   │   ├── LoadingStates.kt        # 加载状态
│   │   │   ├── Decoration.kt           # 装饰组件
│   │   │   ├── PieChart.kt             # 饼图组件
│   │   │   └── RadarChartView.kt       # 雷达图组件
│   │   │
│   │   ├── navigation/                 # 导航
│   │   │   ├── Screen.kt               # 路由定义
│   │   │   └── NavGraph.kt             # 导航图
│   │   │
│   │   └── theme/                      # 主题
│   │       ├── Color.kt                # 颜色定义
│   │       ├── Type.kt                 # 字体定义
│   │       ├── Dimens.kt               # 尺寸定义
│   │       └── Theme.kt                # 主题配置
│   │
│   ├── viewmodel/                      # ViewModel层
│   │   ├── AuthViewModel.kt            # 认证状态
│   │   ├── CompetitionViewModel.kt     # 赛事状态
│   │   ├── CompetitionManagementViewModel.kt # 赛事管理
│   │   ├── EntryAddViewModel.kt        # 作品添加
│   │   ├── EntryEditViewModel.kt       # 作品编辑
│   │   ├── EntryReviewViewModel.kt     # 作品评审
│   │   ├── RatingViewModel.kt          # 评分状态
│   │   ├── ScoreViewModel.kt           # 分数状态
│   │   ├── DataDisplayViewModel.kt     # 数据展示
│   │   ├── ModelViewModel.kt           # 模型管理
│   │   ├── FruitDataViewModel.kt       # 水果数据
│   │   ├── FruitManagementViewModel.kt # 水果管理
│   │   ├── FruitDataManagementViewModel.kt # 数据管理
│   │   └── UserViewModel.kt            # 用户状态
│   │
│   ├── data/                           # 数据层
│   │   ├── api/                        # API接口
│   │   │   ├── AuthApiService.kt       # 认证API
│   │   │   ├── CompetitionApi.kt       # 赛事API
│   │   │   ├── EvaluationApiService.kt # 评价API
│   │   │   ├── EvaluationModelApi.kt   # 模型API
│   │   │   ├── RatingApi.kt            # 评分API
│   │   │   ├── FruitApiService.kt      # 水果API
│   │   │   ├── FruitAdminApi.kt        # 水果管理API
│   │   │   ├── FruitDataApi.kt         # 水果数据API
│   │   │   ├── FruitDataAdminApi.kt    # 数据管理API
│   │   │   ├── ApiService.kt           # 通用API
│   │   │   ├── AuthInterceptor.kt      # 认证拦截器
│   │   │   └── dto/                    # DTO定义
│   │   │       ├── AuthDto.kt
│   │   │       ├── CompetitionDtos.kt
│   │   │       ├── CompetitionEntryDtos.kt
│   │   │       ├── EvaluationModelDtos.kt
│   │   │       ├── RatingDtos.kt
│   │   │       ├── FruitDto.kt
│   │   │       └── EntryStatusUpdateRequest.kt
│   │   │
│   │   ├── repository/                 # 数据仓库
│   │   │   ├── AuthRepository.kt
│   │   │   ├── CompetitionRepository.kt
│   │   │   ├── EvaluationModelRepository.kt
│   │   │   ├── RatingRepository.kt
│   │   │   ├── FruitRepository.kt
│   │   │   ├── FruitDataRepository.kt
│   │   │   └── FruitDataAdminRepository.kt
│   │   │
│   │   ├── model/                      # 数据模型
│   │   │   ├── Competition.kt
│   │   │   ├── EvaluationModel.kt
│   │   │   ├── SubmissionScore.kt
│   │   │   ├── ScoreParameter.kt
│   │   │   ├── Fruit.kt
│   │   │   ├── FruitNutrition.kt
│   │   │   └── QueryDataItem.kt
│   │   │
│   │   └── local/                      # 本地存储
│   │       ├── PreferencesManager.kt   # SharedPreferences
│   │       └── JwtTokenParser.kt       # JWT解析
│   │
│   ├── domain/                         # 领域层
│   │   └── model/
│   │       ├── AuthToken.kt
│   │       └── User.kt
│   │
│   └── di/                             # 依赖注入
│       ├── NetworkModule.kt            # 网络模块
│       └── ApiModule.kt                # API模块
│
├── app/src/test/                       # 单元测试
│   └── java/com/example/userauth/
│       ├── viewmodel/                  # ViewModel测试
│       └── repository/                 # Repository测试
│
└── app/src/androidTest/                # UI测试
    └── java/com/example/userauth/
        └── ui/screen/                  # 界面测试
```

## 环境配置

### 前置要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17+
- Android SDK API 24+

### 配置步骤

1. **复制配置文件**
   ```bash
   cp local.properties.template local.properties
   ```

2. **配置Android SDK路径**（在 `local.properties` 中）
   ```properties
   sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk  # Windows
   # sdk.dir=/Users/yourname/Library/Android/sdk  # macOS
   # sdk.dir=/home/yourname/Android/Sdk  # Linux
   ```

3. **使用Android Studio打开项目**
   - 打开 `android-app` 目录
   - 同步Gradle依赖

4. **配置后端API地址**

   在 `di/NetworkModule.kt` 中：
   ```kotlin
   // 模拟器使用
   private const val BASE_URL = "http://10.0.2.2:8080/"
   
   // 真机使用（替换为电脑IP）
   // private const val BASE_URL = "http://192.168.1.xxx:8080/"
   ```

5. **运行应用**
   - 连接设备或启动模拟器
   - 点击 "Run" 按钮

## 构建命令

```bash
# 编译项目
./gradlew build

# 清理构建
./gradlew clean

# 运行单元测试
./gradlew test

# 运行UI测试（需要设备）
./gradlew connectedAndroidTest

# 生成调试APK
./gradlew assembleDebug

# 生成发布APK
./gradlew assembleRelease

# 生成测试覆盖率报告
./gradlew testWithCoverage
```

## 测试

### 测试结构

```
app/src/
├── test/                           # 单元测试（JVM）
│   └── java/com/example/userauth/
│       ├── viewmodel/              # ViewModel测试
│       │   ├── AuthViewModelTest.kt
│       │   ├── CompetitionViewModelTest.kt
│       │   └── ...
│       └── repository/             # Repository测试
│           ├── AuthRepositoryTest.kt
│           └── ...
│
└── androidTest/                    # UI测试（Android设备）
    └── java/com/example/userauth/
        └── ui/screen/
            ├── LoginScreenTest.kt
            ├── RegisterScreenTest.kt
            └── EvaluationScreenTest.kt
```

### 运行测试

```bash
# 运行所有单元测试
./gradlew test

# 运行特定测试类
./gradlew test --tests="AuthViewModelTest"

# 运行UI测试（需要设备）
./gradlew connectedAndroidTest

# 运行所有测试并生成覆盖率报告
./gradlew testWithCoverage
```

## 导航结构

```
登录/注册
    ↓
主页面 (MainScreen)
    ├── 赛事管理
    │   ├── 赛事列表
    │   ├── 创建赛事
    │   ├── 编辑赛事
    │   └── 赛事详情
    ├── 作品管理
    │   ├── 作品列表
    │   ├── 提交作品
    │   ├── 编辑作品
    │   └── 作品评审
    ├── 评分系统
    │   ├── 评分页面
    │   └── 分数展示
    ├── 数据展示
    │   ├── 数据总览
    │   └── 数据详情
    ├── 水果查询
    │   └── 水果营养
    └── 管理功能
        ├── 水果管理
        ├── 模型管理
        └── 系统管理
```

## API接口

### 认证相关

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/auth/register` | POST | 用户注册 |
| `/api/auth/login` | POST | 用户登录 |

### 赛事相关

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/competitions` | GET/POST | 获取/创建赛事 |
| `/api/competitions/{id}` | GET/PUT/DELETE | 赛事详情/更新/删除 |
| `/api/competitions/{id}/entries` | POST | 添加作品 |
| `/api/competitions/{id}/submit` | POST | 提交作品 |

### 评分相关

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/ratings` | POST | 提交评分 |
| `/api/ratings/{competitionId}` | GET | 获取评分数据 |

### 水果数据

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/fruits` | GET | 获取水果列表 |
| `/api/fruitdata/query` | GET | 查询水果数据 |

## 依赖注入配置

使用Hilt进行依赖注入，主要模块：

### NetworkModule
- 提供Retrofit实例
- 配置OkHttp客户端
- 添加认证拦截器

### ApiModule
- 提供各API接口实例
- AuthApiService
- CompetitionApi
- RatingApi
- FruitApiService
- 等等

## 常见问题

### Q: 无法连接后端？

**A**: 
1. 检查后端服务是否启动：`http://localhost:8080/actuator/health`
2. 确认Base URL配置正确：
   - 模拟器: `http://10.0.2.2:8080/`
   - 真机: 使用电脑IP地址
3. 确保手机和电脑在同一网络

### Q: 图片无法上传？

**A**:
1. 检查文件权限（Android 10+需要特殊处理）
2. 确认文件大小不超过限制（默认10MB）
3. 查看后端日志确认接收情况

### Q: 编译失败？

**A**:
1. 检查Android SDK路径配置
2. 清理并重新构建：`./gradlew clean build`
3. 更新Gradle和插件版本

### Q: 测试失败？

**A**:
1. 确保后端服务运行
2. 检查测试配置文件
3. 查看详细错误日志：`./gradlew test --info`

## 调试技巧

### 网络调试

```kotlin
// 在NetworkModule中启用日志
val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}
```

### 日志查看

```bash
# 查看应用日志
adb logcat -s UserAuth:D

# 查看特定TAG日志
adb logcat -s Retrofit:D
```

### 数据库查看

使用Android Studio的Database Inspector查看本地数据库。

## 贡献指南

1. Fork项目
2. 创建特性分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add some amazing feature'`
4. 推送分支：`git push origin feature/amazing-feature`
5. 创建Pull Request

## 参考文档

- [项目主README](../README.md)
- [快速开始](../QUICK_START.md)
- [项目结构](../PROJECT_STRUCTURE.md)
- [后端README](../backend/README.md)

---

**最后更新**: 2026年2月

**状态**: 生产就绪 ✅