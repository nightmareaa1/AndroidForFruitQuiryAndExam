# 测试覆盖率提升计划

## 当前状态

### 测试统计
- **总测试数**: 129
- **通过**: 129 (100%)
- **失败**: 0
- **跳过**: 0
- **构建状态**: ✅ BUILD SUCCESS

### 覆盖率现状 (JaCoCo)
| 指标 | 覆盖率 | 目标 | 差距 |
|------|--------|------|------|
| 指令覆盖 (Instructions) | 26% | 85% | -59% |
| 分支覆盖 (Branch) | 19% | 80% | -61% |
| 行覆盖 (Lines) | ~26% | 85% | -59% |
| 方法覆盖 (Methods) | - | - | - |

## 覆盖率低的原因分析

### 1. 缺失 Controller 层测试 (当前覆盖率 <10%)
需要测试的控制器：
- [ ] `AuthController` (已创建, 需完善安全测试)
- [ ] `FruitController` (已创建)
- [ ] `CompetitionController` (覆盖率 8.8%)
- [ ] `RatingController` (覆盖率 1.7%)
- [ ] `FruitAdminController` (覆盖率 1.4%)
- [ ] `FruitDataController` (覆盖率 1.8%)
- [ ] `FruitDataAdminController` (覆盖率 0.5%)
- [ ] `EvaluationModelController` (覆盖率 3.7%)
- [ ] `FileController` (覆盖率 2.5%)
- [ ] `HealthController` (覆盖率 21%)

### 2. 缺失 Security 层测试 (当前覆盖率 <15%)
- [ ] `JwtAuthenticationFilter` (覆盖率 11.1%)
- [ ] `JwtService` (覆盖率 1.4%)
- [ ] `AdminAccessAspect` (覆盖率 10.8%)
- [ ] `CustomUserDetailsService` (覆盖率 9.2%)
- [ ] `PasswordPolicyValidator` (覆盖率 18.6%)
- [ ] `KeyManagementService` (覆盖率 7.4%)

### 3. 缺失 Service 层测试 (部分已覆盖)
- [x] `UserService` (已有测试)
- [x] `TokenService` (已有测试)
- [x] `PasswordService` (已有测试)
- [x] `RatingDataService` (已修复)
- [ ] `RatingService` (覆盖率 0.5%)
- [ ] `CompetitionService` (覆盖率 1.9%)
- [ ] `FruitQueryService` (已有部分测试)
- [ ] `FileStorageService` (已有测试)
- [ ] `FileValidationService` (已有测试)
- [ ] `EvaluationModelService` (需检查)

### 4. 缺失 Entity/DTO 测试
- [ ] Entity 类 (大多数覆盖率 <15%)
  - `User` (覆盖率 13.9%)
  - `Competition` (覆盖率 36.6%)
  - `CompetitionEntry` (覆盖率 36.6%)
  - `Fruit` (覆盖率 13.9%)
  - `FruitData` (覆盖率 8.2%)
  - 等等

### 5. 缺失 Config 类测试
- [ ] `SecurityConfig` (覆盖率 21.4%)
- [ ] `WebConfig` (需测试 CORS 配置)
- [ ] `DataInitializer` (覆盖率 5%)
- [ ] `AppProperties` (覆盖率 0%)

## 优先级行动计划

### 🔴 优先级 1: Controller 层测试 (预计提升 25-30% 覆盖率)

每个控制器需要测试：
1. 成功场景 (所有端点)
2. 参数校验失败 (400)
3. 认证失败 (401/403)
4. 资源不存在 (404)
5. 服务端错误 (500)

**预计工作量**: 10-15 个测试类，约 150-200 个测试方法
**预计提升**: 25-30% 覆盖率

### 🟠 优先级 2: Security 层测试 (预计提升 15-20% 覆盖率)

重点测试：
1. JWT 生成和验证
2. 权限检查逻辑
3. 密码策略验证
4. 过滤器链

**预计工作量**: 5-8 个测试类，约 80-120 个测试方法
**预计提升**: 15-20% 覆盖率

### 🟡 优先级 3: Service 层补充测试 (预计提升 10-15% 覆盖率)

补充测试：
1. `RatingService` - 核心业务逻辑
2. `CompetitionService` - 赛事管理逻辑
3. `EvaluationModelService` - 评价模型逻辑

**预计工作量**: 3-5 个测试类，约 50-80 个测试方法
**预计提升**: 10-15% 覆盖率

### 🟢 优先级 4: Entity/DTO 测试 (预计提升 5-10% 覆盖率)

测试内容：
1. Getter/Setter
2. equals/hashCode
3. Builder 模式
4. Validation 注解

**预计工作量**: 简单测试，约 30-50 个测试方法
**预计提升**: 5-10% 覆盖率

## 测试策略

### 1. Controller 测试策略
```java
@WebMvcTest(ControllerClass.class)
@ContextConfiguration(classes = {ControllerClass.class, TestConfig.class})
class ControllerClassTest {
    // 测试所有端点
    // - 成功场景
    // - 参数校验
    // - 权限控制
    // - 异常处理
}
```

### 2. Security 测试策略
```java
@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    // 单元测试 - 使用 Mockito
}

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {
    // 集成测试 - 测试完整安全链
}
```

### 3. Service 测试策略
```java
@ExtendWith(MockitoExtension.class)
class ServiceClassTest {
    // 单元测试 - Mock 依赖
    // - 正常流程
    // - 边界条件
    // - 异常场景
}
```

## 时间估算

| 任务 | 估计时间 | 预期覆盖率提升 |
|------|----------|----------------|
| Controller 层测试 | 2-3 天 | 25-30% |
| Security 层测试 | 1-2 天 | 15-20% |
| Service 层补充 | 1 天 | 10-15% |
| Entity/DTO 测试 | 0.5 天 | 5-10% |
| **总计** | **4-6.5 天** | **55-75%** |

## 预期最终覆盖率

当前: 26%
提升: +55-75%
**目标: 80-100%** (达到 85% 目标)

## 注意事项

1. **测试质量优先于数量** - 确保测试有意义，不是简单的 getter/setter 测试
2. **使用 @WebMvcTest 而非 @SpringBootTest** 测试 Controller - 更快
3. **使用 Mockito 进行单元测试** - 隔离被测组件
4. **保留集成测试** - 用于验证整体工作流
5. **JaCoCo 配置** - 已添加 85% 覆盖率门禁，构建会失败如果未达到

## GitHub Actions 配置

已配置：
- ✅ JaCoCo 覆盖率报告生成
- ✅ 测试报告上传 (Artifacts)
- ✅ Codecov 覆盖率上传
- ✅ PR 检查 - 代码风格检查
- ✅ 测试门禁 - 失败时阻止合并

## 下一步行动

1. 立即开始创建 Controller 层测试（优先级 1）
2. 并行创建 Security 层测试
3. 每天运行 `mvn clean test jacoco:report` 检查进度
4. 达到 85% 后启用 JaCoCo 严格检查
