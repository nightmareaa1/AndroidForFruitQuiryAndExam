# Competition `getCompetitionById` Debug Summary

## 1. Debug Goal

定位并修复 Android 端在 `CompetitionManagementViewModel` 中调用
`repository.getCompetitionById(competitionId)` 失败的问题（日志表现为 `500`）。

## 2. Core Symptoms

- Android Logcat:
  - `getCompetitionById调用失败: Failed to fetch competition: 500`
  - Java exception from `CompetitionRepository.kt:49`
- HTTP response:
  - `GET /api/competitions/{id}` returns `500`
  - body empty (`Content-Length: 0`)

## 3. Files Inspected

### Android

- `android-app/app/src/main/java/com/example/userauth/viewmodel/CompetitionManagementViewModel.kt`
  - 查看 `loadCompetitionDetail()` 调用链
  - 添加临时日志（调用前、失败分支）
- `android-app/app/src/main/java/com/example/userauth/data/repository/CompetitionRepository.kt`
  - 查看 `getCompetitionById()` 的错误处理
- `android-app/app/src/main/java/com/example/userauth/data/api/CompetitionApi.kt`
  - 确认接口路径 `@GET("competitions/{id}")`
- `android-app/app/src/main/java/com/example/userauth/di/NetworkModule.kt`
  - 确认 Base URL、拦截器、Retrofit 配置
- `android-app/app/src/main/java/com/example/userauth/data/api/AuthInterceptor.kt`
  - 确认 JWT 注入逻辑
- `android-app/app/src/main/AndroidManifest.xml`
  - 确认 `API_BASE_URL` 的 meta-data 配置
- `android-app/app/build.gradle`
  - 确认 `manifestPlaceholders` 中 API 地址

### Backend

- `backend/src/main/java/com/example/userauth/controller/CompetitionController.java`
  - 查看 `GET /api/competitions/{id}` 控制器实现
- `backend/src/main/java/com/example/userauth/service/CompetitionService.java`
  - 查看 `getCompetitionById()` 与 `convertToResponse()`
- `backend/src/main/java/com/example/userauth/repository/CompetitionRepository.java`
  - 查看 `findActiveByIdWithDetails()` JPA 查询
- `backend/src/main/java/com/example/userauth/config/SecurityConfig.java`
  - 确认 `/api/competitions/**` 需要认证
- `backend/src/main/java/com/example/userauth/entity/Competition.java`
  - 检查 `judges`、`entries` 集合映射（`List`）
- `backend/src/main/java/com/example/userauth/entity/EvaluationModel.java`
  - 检查 `parameters` 集合映射（`List`）

## 4. Commands Used

### Build / Compile / Runtime

- Android compile check:
  - `./gradlew compileDebugJavaWithJavac`
  - `./gradlew compileDebugKotlin`
  - `./gradlew build --offline`
- Docker runtime check:
  - `docker ps`
  - `docker restart userauth-backend-dev`
  - `docker logs userauth-backend-dev --tail 50`

### API Validation

- Login to get JWT:
  - `curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d '{"username":"txy1","password":"tttt1111"}'`
- Endpoint check:
  - `curl -H "Authorization: Bearer <token>" http://localhost:8080/api/competitions`
  - `curl -H "Authorization: Bearer <token>" http://localhost:8080/api/competitions/1`
  - `curl -H "Authorization: Bearer <token>" http://localhost:8080/api/competitions/created`

### Database Validation

- Using provided DB credentials:
  - `docker exec userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev -e "SELECT id, name, status, deleted_at FROM competitions WHERE id = 1;"`
  - `docker exec userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev -e "... FROM competition_judges ... WHERE competition_id = 1;"`
  - `docker exec userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev -e "... FROM competition_entries ... WHERE competition_id = 1;"`

### Code Search

- `grep` / file search to locate:
  - `getCompetitionById`
  - `findActiveByIdWithDetails`
  - security matchers and related entity mappings

## 5. Functional Checks Performed

1. **网络层**：Base URL、AuthInterceptor、权限声明是否正确
2. **认证链路**：旧 token 是否失效，新 token 是否可用
3. **后端 endpoint 差异**：
   - `/api/competitions` 成功
   - `/api/competitions/created` 成功
   - `/api/competitions/{id}` 失败
4. **数据库数据存在性**：赛事、评委、参赛项均存在
5. **后端异常日志定位**：提取到 Hibernate 根因异常

## 6. Root Cause Located

后端报错（关键）：

`org.hibernate.loader.MultipleBagFetchException: cannot simultaneously fetch multiple bags`

触发点：`findActiveByIdWithDetails()` 查询中 `JOIN FETCH` 同时拉取多个 `List` 类型集合。

典型冲突组合：

- `Competition.judges` (`List`)
- `Competition.entries` (`List`)
- `EvaluationModel.parameters` (`List`)

## 7. Localization Method (How We Positioned the Bug)

采用“分层排除 + 对比端点 + 日志反推”方法：

1. **先证伪客户端问题**：认证、网络、DTO、路径全部通
2. **再做端点对比**：同一 token 下 list 接口正常，detail 接口失败
3. **验证数据层**：数据库有数据，不是空数据问题
4. **回到后端堆栈**：抓到 Hibernate `MultipleBagFetchException`
5. **回查 JPA 查询**：定位到 `findActiveByIdWithDetails` 的多 bag fetch

## 8. Recommended Fix Strategy

优先方案（推荐）：

1. 避免在同一 JPQL 里 `JOIN FETCH` 多个 `List` 集合
2. 拆成两段加载（主对象 + 子集合）或改为 `Set`
3. 对必要集合使用二次查询 / batch/subselect 方案

可执行方向：

- 保留 `model`、`creator` 的 fetch
- `judges` 与 `entries` 分开加载（至少一个不在主查询中 join fetch）
- `EvaluationModel.parameters` 也避免和 `judges/entries` 同查询 fetch

## 9. Extra Notes

- 本次也修复过一次 Android 编译问题：
  - `CompetitionManagementViewModel.kt` 中 `.onFailure { ... }` 缺少闭合 `}` 导致编译失败。
- 当前文档聚焦 `getCompetitionById` 的 `500` 主线定位过程。
