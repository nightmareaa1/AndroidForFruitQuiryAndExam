# 项目问题解决前置指南

## 项目结构速览

```
AndroidForFruitQuiryAndExam/
├── android-app/          # Android 客户端 (Kotlin + Jetpack Compose + Hilt)
├── backend/              # Spring Boot 后端 (Java + MySQL + Redis)
├── docs/                # 文档目录
└── .kiro/specs/        # 任务规格
```

---

## 问题类型速查表

| 问题表现 | 可能原因 | 处理位置 |
|---------|---------|---------|
| **API 请求失败 (400/500)** | 请求格式/验证问题 | `backend/src/main/java/com/example/userauth/controller/` |
| **Android 数据不显示** | Gson 反序列化问题 | `android-app/app/src/main/java/com/example/userauth/di/NetworkModule.kt` |
| **添加数据失败** | 后端验证规则 | `backend/src/main/java/com/example/userauth/service/` |
| **ViewModel 崩溃** | Hilt 注解缺失 | `android-app/app/src/main/java/com/example/userauth/viewmodel/` |
| **中文乱码/编码问题** | UTF-8 配置 | `backend/src/main/resources/application.yml` + `NetworkModule.kt` |
| **数据库数据不同步** | Repository 未调用 API | `android-app/app/src/main/java/com/example/userauth/data/repository/` |

---

## 关键文件速查

### Android 端 (按优先级)

| 文件路径 | 作用 |
|---------|------|
| `di/NetworkModule.kt` | Gson 配置、UTF-8 拦截器、Retrofit 注入 |
| `data/api/dto/*.kt` | DTO 定义 + 自定义反序列化器 |
| `data/repository/*.kt` | Repository 层，调用 API |
| `viewmodel/*.kt` | ViewModel，Hilt 注入 |
| `ui/screen/*.kt` | UI 界面 |
| `di/ApiModule.kt` | API 接口注入 |

### 后端

| 文件路径 | 作用 |
|---------|------|
| `src/main/resources/application.yml` | UTF-8 编码配置 |
| `src/main/java/com/example/userauth/config/WebConfig.java` | Jackson 配置 |
| `src/main/java/com/example/userauth/config/SecurityConfig.java` | Security + Encoding Filter |
| `src/main/java/com/example/userauth/service/*.java` | 业务逻辑 + 验证规则 |
| `src/main/java/com/example/userauth/dto/*.java` | 请求/响应 DTO |

---

## 问题定位流程

```
1. 查看 Logcat 确认错误
   └── HTTP 状态码 → 400(验证)/500(服务器)/401(认证)

2. API 问题 → 检查后端 controller/service
   └── GET/POST 参数格式
   └── 验证规则 (如: 权重必须=100)

3. 数据解析问题 → 检查 Android DTO
   └── 日期格式 [2026,2,6,...] vs "2026-02-06T12:58:43"
   └── 使用自定义 JsonDeserializer

4. 编码问题 → 检查 UTF-8 配置
   └── 后端: application.yml + WebConfig
   └── Android: NetworkModule CharsetInterceptor

5. 依赖注入问题 → 检查 Hilt
   └── @HiltViewModel 注解
   └── @Provides 注入
```

---

## 常见验证规则 (后端)

```java
// EvaluationModelService.java
validateTotalWeight() → 总权重必须=100
modelRepository.findByName() → 名称不能重复
@RequireAdmin → 需要管理员权限
```

### 当前测试账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| `android_admin` | `Android123!` | 管理员 |

---

## 快速调试命令

### 检查后端健康

```bash
curl http://localhost:8080/actuator/health
```

### 测试 API 创建评价模型 (权重必须=100)

```bash
# 1. 获取 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"android_admin","password":"Android123!"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# 2. 创建评价模型 (权重=100)
curl -X POST http://localhost:8080/api/evaluation-models \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","parameters":[{"name":"P1","weight":50},{"name":"P2","weight":50}]}'

# 3. 获取所有模型
curl http://localhost:8080/api/evaluation-models \
  -H "Authorization: Bearer $TOKEN"
```

### 查看后端日志

```bash
docker logs userauth-backend-dev --tail 50
```

### 检查数据库

```bash
docker exec userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev \
  -e "SELECT id, name FROM evaluation_models ORDER BY id DESC LIMIT 5;"
```

---

## 常见问题解决方案

### 1. 日期格式不匹配

**问题**：后端返回 `createdAt: [2026,2,6,12,58,43]`，Android 无法解析

**解决方案**：在 `data/api/dto/*.kt` 中添加自定义反序列化器

```kotlin
class EvaluationModelDtoDeserializer : JsonDeserializer<EvaluationModelDto> {
    override fun deserialize(json: JsonElement?, ...): EvaluationModelDto {
        // 处理数组格式的日期
        json.asJsonObject.get("createdAt")?.let { element ->
            if (element.isJsonArray) {
                val arr = element.asJsonArray
                return formatDateFromArray(arr)
            }
        }
    }
}
```

### 2. 中文编码问题

**问题**：后端返回 500 "Invalid UTF-8"

**解决方案**：确保两端都配置 UTF-8

**后端** `application.yml`:
```yaml
server:
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true
```

**Android** `NetworkModule.kt`:
```kotlin
fun provideCharsetInterceptor(): Interceptor {
    return Interceptor { chain ->
        // 强制 UTF-8 编码
        val newContentType = "application/json; charset=UTF-8"
        // ...
    }
}
```

### 3. Hilt 依赖注入问题

**问题**：`Cannot create an instance of class XxxViewModel`

**解决方案**：确保类上有 `@HiltViewModel` 注解

```kotlin
@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val apiService: EvaluationApiService
) : ViewModel() {
    // ...
}
```

### 4. API 路径重复

**问题**：请求变成 `/api/api/evaluation-models`

**解决方案**：移除 API 接口中的 `api/` 前缀 (Base URL 已包含)

```kotlin
// 错误
@GET("api/evaluation-models")  

// 正确
@GET("evaluation-models")
```

### 5. 后端验证失败

**问题**：400 Bad Request

**解决方案**：检查后端验证规则

```java
// 常见验证
validateTotalWeight() → 参数总权重必须=100
@NotBlank → 字段不能为空
@NotEmpty → 列表不能为空
@Min/Max → 数值范围
```

---

## 文档阅读顺序建议

1. **开始解决问题前**
   - 查看 `docs/README.md` 了解项目概述
   - 查看 `docs/development/android-backend-crud-integration-plan.md`

2. **遇到 API 相关问题**
   - 先用 curl 测试后端 API
   - 查看 `backend/src/main/java/com/example/userauth/controller/` 中的接口定义
   - 检查 `backend/src/main/java/com/example/userauth/service/` 中的验证逻辑

3. **遇到 Android 端问题**
   - 查看 Logcat 获取详细错误
   - 检查 `di/NetworkModule.kt` 中的 Gson/Retrofit 配置
   - 检查对应 Repository 是否正确调用 API

4. **修改前后端交互逻辑**
   - 后端: controller → service → repository → entity
   - Android: screen → viewModel → repository → api → dto
