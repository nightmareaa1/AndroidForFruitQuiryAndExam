# Android Fruit Query & Exam 项目总规划

> **项目目标：** 构建完整的水果查询和考试系统，实现Android端与Spring Boot后端的无缝集成，达到85%以上的测试覆盖率。

**当前状态：** 核心功能开发完成，测试覆盖率60%，正在进行测试增强阶段

**技术栈：** 
- 前端：Kotlin + Jetpack Compose + Hilt
- 后端：Spring Boot 3.2 + MySQL + Redis
- 测试：JUnit 5 + Mockito + JaCoCo + Property-based Testing

---

## 📊 项目总体进度

### 已完成阶段 ✅

#### Phase 1: 项目基础架构 (100%)
- [x] 项目结构设计
- [x] 后端Spring Boot框架搭建
- [x] Android Jetpack Compose框架搭建
- [x] 数据库设计和Flyway迁移脚本
- [x] Docker开发环境配置
- [x] CI/CD GitHub Actions配置

#### Phase 2: 核心功能开发 (100%)
- [x] 用户认证系统（JWT）
- [x] 水果数据查询功能
- [x] 赛事管理（CRUD）
- [x] 评分系统
- [x] 文件上传功能
- [x] Android UI界面
- [x] API集成

#### Phase 3: 测试框架建立 (85%)
- [x] Controller层测试（10个文件，78个测试）
- [x] RatingService单元测试（18个测试）
- [x] 测试覆盖率门禁配置（JaCoCo 85%阈值）
- [x] GitHub Actions测试报告上传
- [x] 测试模板和最佳实践文档

### 进行中阶段 🔄

#### Phase 4: 测试覆盖率提升 (进行中 - 64% → 85%)
- [x] Service层补充测试 (80% ✅)
- [x] Security层测试 (68% ✅)
- [x] Entity层测试 (66% ✅)
- [x] DTO层测试 (81% ✅)
- [ ] Controller层补充测试 (50% → 80%)
- [ ] 集成测试增强
- [ ] 边界情况测试

#### Phase 5: 性能优化和监控 (计划中)
- [ ] 性能测试
- [ ] 监控和日志完善
- [ ] 生产环境部署

---

## 🎯 当前任务详细状态

### 任务1: 修复后端403权限问题 ✅
**状态：** 已完成  
**完成时间：** 2026-02-12  
**成果：**
- 修改 `SecurityConfig.java` 添加 `/api/admin/fruit-data/**` 权限规则
- 修复 `FruitDataAdminController` 权限配置
- 验证JWT角色解析正确

**相关文件：**
- `backend/src/main/java/com/example/userauth/config/SecurityConfig.java`
- `backend/src/main/java/com/example/userauth/controller/FruitDataAdminController.java`

---

### 任务2: 测试框架和门禁配置 ✅
**状态：** 已完成  
**完成时间：** 2026-02-13  
**成果：**
- JaCoCo覆盖率门禁配置（85%阈值）
- GitHub Actions测试报告上传优化
- Codecov集成
- 测试模板创建

**相关文件：**
- `backend/pom.xml` - JaCoCo插件配置
- `.github/workflows/ci.yml` - CI配置
- `backend/src/test/CONTROLLER_TEST_TEMPLATE.txt` - 测试模板
- `backend/src/test/CONTROLLER_TEST_GUIDE.md` - 测试指南

---

### 任务3: Controller层测试创建 ✅
**状态：** 已完成  
**测试统计：** 10个测试文件，78个测试方法，全部通过  
**成果：**

| 测试文件 | 测试数 | 状态 |
|---------|--------|------|
| AuthControllerTest | 10 | ✅ |
| CompetitionControllerTest | 9 | ✅ |
| FruitControllerTest | 11 | ✅ |
| RatingControllerTest | 10 | ✅ |
| FruitAdminControllerTest | 9 | ✅ |
| FruitDataControllerTest | 6 | ✅ |
| FruitDataAdminControllerTest | 8 | ✅ |
| EvaluationModelControllerTest | 7 | ✅ |
| FileControllerTest | 4 | ✅ |
| HealthControllerTest | 4 | ✅ |

**覆盖率提升：** Controller层从 <10% → 45%

---

### 任务4: Service层测试创建 🔄
**状态：** 部分完成  
**当前进度：**

| 服务类 | 当前覆盖率 | 测试状态 | 优先级 |
|--------|-----------|---------|--------|
| RatingService | 0.5% → 60% | ✅ 18个测试 | 高 |
| CompetitionService | 1.9% → 78% | ✅ 37个测试 | 高 |
| EvaluationModelService | 56.9% → 82% | ✅ 15个测试 | 中 |
| UserService | 94.4% | ✅ 已有测试 | - |
| FruitQueryService | 99.0% | ✅ 已有测试 | - |

**已创建文件：**
- `backend/src/test/java/com/example/userauth/service/RatingServiceTest.java` (18个测试)
- `backend/src/test/java/com/example/userauth/service/CompetitionServiceTest.java` (37个测试)
- `backend/src/test/java/com/example/userauth/service/EvaluationModelServiceTest.java` (15个测试)

---

### 任务5: Security层测试创建 🔄
**状态：** 部分完成  
**当前覆盖率：**

| 类 | 当前覆盖率 | 测试状态 | 优先级 |
|---|-----------|---------|--------|
| JwtService | 1.4% → 75% | ✅ 10个测试 | 高 |
| JwtAuthenticationFilter | 11.1% → 85% | ✅ 8个测试 | 高 |
| PasswordPolicyValidator | 18.6% → 85% | ✅ 11个测试 | 中 |
| AdminAccessAspect | 10.8% → 90% | ✅ 6个测试 | 中 |
| CustomUserDetailsService | 9.2% → 70% | ✅ 5个测试 | 中 |

**已创建：**
- `JwtServiceTest.java` - 10个测试 ✅
- `JwtAuthenticationFilterTest.java` - 8个测试 ✅
- `PasswordPolicyValidatorTest.java` - 11个测试 ✅
- `AdminAccessAspectTest.java` - 6个测试 ✅
- `CustomUserDetailsServiceTest.java` - 5个测试 ✅

---

### 任务6: 测试覆盖率提升至85% 🔄
**状态：** 进行中  
**当前覆盖率：** 64% (2260/3442 行) - 2026-02-14更新  
**目标覆盖率：** 85% (2900/3442 行)  
**差距：** 640行需要覆盖

**覆盖率分解：**

| 包 | 当前覆盖率 | 目标覆盖率 | 差距 | 状态 |
|---|-----------|-----------|------|------|
| Controller | 50% | 80% | +30% | 🔄 需补充 |
| Service | 80% | 80% | ✅ 完成 | ✅ 完成 |
| Security | 68% | 70% | +2% | 🔄 接近完成 |
| Entity | 66% | 80% | +14% | ✅ 进行中 |
| DTO | 81% | 80% | ✅ 超额 | ✅ 完成 |
| Config | 43% | 50% | +7% | ⏳ 可选 |

---

## 📅 详细执行计划

### Sprint 1: Service层测试完善 (预计3-4天)

#### Day 1: CompetitionService测试
**目标：** 创建CompetitionServiceTest，覆盖主要业务逻辑

**任务清单：**
1. **研究现有代码**
   - 文件：`backend/src/main/java/com/example/userauth/service/CompetitionService.java`
   - 理解所有public方法
   - 识别依赖的Repository

2. **编写测试（预计20个测试方法）**
   - 创建文件：`backend/src/test/java/com/example/userauth/service/CompetitionServiceTest.java`
   - 测试场景：
     - `getAllCompetitions` - 获取所有赛事
     - `getCompetitionsByCreator` - 按创建者查询
     - `getCompetitionById` - 按ID查询（成功/失败）
     - `createCompetition` - 创建赛事（成功/验证失败）
     - `updateCompetition` - 更新赛事（权限检查）
     - `deleteCompetition` - 删除赛事
     - `addJudgesToCompetition` - 添加评委
     - `removeJudgeFromCompetition` - 移除评委
     - `addEntriesToCompetition` - 添加参赛作品
     - `submitEntryToCompetition` - 提交作品

3. **运行测试**
   ```bash
   cd backend
   mvn test -Dtest=CompetitionServiceTest
   ```

4. **验证覆盖率**
   ```bash
   mvn jacoco:report
   # 检查 CompetitionService 覆盖率是否提升到60%+
   ```

#### Day 2-3: EvaluationModelService测试
**目标：** 创建EvaluationModelServiceTest

**任务清单：**
1. **编写测试（预计15个测试方法）**
   - 文件：`backend/src/test/java/com/example/userauth/service/EvaluationModelServiceTest.java`
   - CRUD操作测试
   - 参数管理测试
   - 验证规则测试

2. **补充其他Service测试**
   - FileStorageService测试
   - FileValidationService测试

#### Day 4: Service层测试回顾
- 运行所有Service测试
- 修复失败的测试
- 更新覆盖率报告

**预期成果：**
- Service层覆盖率：30% → 65%
- 新增测试：50+个
- 整体覆盖率：47.3% → 60%

---

### Sprint 2: Security层测试完善 (预计2-3天)

#### Day 1: JwtService测试
**目标：** 创建JwtServiceTest，覆盖JWT核心功能

**任务清单：**
1. **编写测试（10个测试方法）**
   - 文件：`backend/src/test/java/com/example/userauth/security/JwtServiceTest.java`
   - 测试场景：
     - `generateToken` - 令牌生成
     - `validateToken` - 令牌验证（有效/无效/过期）
     - `extractUsername` - 提取用户名
     - `isTokenExpired` - 过期检查

2. **使用@SpringBootTest**
   - 需要加载Spring上下文
   - 注入实际的JwtService

#### Day 2: 其他Security组件测试
**任务清单：**
1. **PasswordPolicyValidatorTest** - 密码策略验证
2. **CustomUserDetailsServiceTest** - 用户详情加载
3. **AdminAccessAspectTest** - 管理员权限检查

#### Day 3: Security测试回顾
- 运行所有Security测试
- 修复问题

**预期成果：**
- Security层覆盖率：15% → 60%
- 新增测试：35+个
- 整体覆盖率：60% → 68%

---

### Sprint 3: 边界情况和集成测试 (预计2-3天)

#### Day 1: 边界情况测试
**目标：** 补充异常场景和边界测试

**任务清单：**
1. **Controller层边界测试**
   - 空值处理
   - 超长字符串
   - 特殊字符
   - 并发请求

2. **Service层边界测试**
   - 数据库连接失败
   - 事务回滚
   - 并发修改

#### Day 2: 集成测试
**目标：** 创建端到端测试

**任务清单：**
1. **API集成测试**
   - 使用TestRestTemplate
   - 测试完整请求流程

2. **数据库集成测试**
   - 使用@Testcontainers
   - MySQL容器测试

#### Day 3: 最终调优
- 运行完整测试套件
- 修复失败的测试
- 优化慢测试

**预期成果：**
- 整体覆盖率：68% → 75%
- 新增测试：30+个

---

### Sprint 4: 覆盖率冲刺 (预计2天)

#### Day 1: 覆盖率分析
**目标：** 识别未覆盖代码，针对性补充测试

**任务清单：**
1. **生成详细覆盖率报告**
   ```bash
   mvn clean test jacoco:report
   ```

2. **分析未覆盖代码**
   - 查看 `target/site/jacoco/index.html`
   - 识别红色区域（未覆盖）
   - 按优先级排序

3. **补充测试**
   - Config类测试（可选）
   - Exception处理测试
   - 工具类测试

#### Day 2: 最终验证
**目标：** 达到85%覆盖率

**任务清单：**
1. **运行完整测试**
   ```bash
   mvn clean test
   # 确保所有测试通过
   ```

2. **验证覆盖率**
   ```bash
   awk -F',' 'BEGIN {total=0; covered=0} NR>1 && $1=="userauth-backend" {total+=$8+$9; covered+=$9} END {printf "Coverage: %.1f%%\n", (covered/total*100)}' target/site/jacoco/jacoco.csv
   ```

3. **调整门禁配置**
   - 如果达到85%，启用严格门禁
   - 如果未达到，调整策略

**预期成果：**
- 整体覆盖率：75% → 85%
- 所有测试通过
- 覆盖率门禁启用

---

## 📊 测试覆盖率详细分解

### 当前覆盖率统计 (2026-02-13)

```
总体覆盖率: 47.3% (1613/3411 行)

按包统计:
- Controller: 45.67% (459/1005 行)
- Service: 30.2% (312/1033 行)
- Security: 15.1% (89/590 行)
- Entity: 60.5% (521/861 行)
- DTO: 55.3% (232/420 行)
```

### 目标覆盖率 (2026-02-20)

```
总体目标: 85% (2900/3411 行)

按包目标:
- Controller: 80% (804/1005 行) - 需增加 345 行
- Service: 80% (826/1033 行) - 需增加 514 行
- Security: 70% (413/590 行) - 需增加 324 行
- Entity: 80% (689/861 行) - 需增加 168 行
- DTO: 80% (336/420 行) - 需增加 104 行

总计需增加: 1455 行覆盖
```

---

## 🔧 技术实现细节

### 测试技术栈

| 技术 | 版本 | 用途 |
|-----|------|------|
| JUnit 5 | 5.10 | 单元测试框架 |
| Mockito | 5.8 | Mock框架 |
| JaCoCo | 0.8.11 | 覆盖率报告 |
| jqwik | 1.8.2 | 属性测试 |
| Testcontainers | 1.19.3 | 集成测试 |

### 关键注解使用

**Controller测试：**
```java
@WebMvcTest(Controller.class)
@ContextConfiguration(classes = {Controller.class, TestConfig.class})
@DisplayName("Controller Tests")
class ControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private Service service;
}
```

**Service测试：**
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("Service Tests")
class ServiceTest {
    @Mock
    private Repository repository;
    
    @InjectMocks
    private Service service;
}
```

### 覆盖率门禁配置

**pom.xml:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.85</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

---

## 📁 项目文件结构

### 测试文件位置

```
backend/src/test/java/com/example/userauth/
├── controller/           # Controller层测试
│   ├── AuthControllerTest.java
│   ├── CompetitionControllerTest.java
│   ├── FruitControllerTest.java
│   ├── RatingControllerTest.java
│   ├── FruitAdminControllerTest.java
│   ├── FruitDataControllerTest.java
│   ├── FruitDataAdminControllerTest.java
│   ├── EvaluationModelControllerTest.java
│   ├── FileControllerTest.java
│   └── HealthControllerTest.java
├── service/              # Service层测试
│   ├── RatingServiceTest.java        ✅ 已完成
│   ├── CompetitionServiceTest.java   🔄 计划中
│   └── EvaluationModelServiceTest.java 🔄 计划中
├── security/             # Security层测试
│   ├── JwtServiceTest.java           🔄 计划中
│   ├── JwtAuthenticationFilterTest.java 🔄 计划中
│   ├── CustomUserDetailsServiceTest.java 🔄 计划中
│   └── PasswordPolicyValidatorTest.java  🔄 计划中
├── config/
│   └── TestConfig.java   # 测试配置
├── CONTROLLER_TEST_TEMPLATE.txt
└── CONTROLLER_TEST_GUIDE.md
```

---

## 🎯 关键里程碑

| 里程碑 | 目标日期 | 关键成果 | 状态 |
|--------|---------|---------|------|
| M1: 403修复 | 2026-02-12 | 后端权限修复完成 | ✅ |
| M2: 测试框架 | 2026-02-13 | Controller测试78个通过 | ✅ |
| M3: Service测试 | 2026-02-17 | Service覆盖率达到65% | 🔄 |
| M4: Security测试 | 2026-02-20 | Security覆盖率达到60% | 🔄 |
| M5: 85%覆盖率 | 2026-02-23 | 整体覆盖率达到85% | 🔄 |

---

## 📈 风险评估与应对

| 风险 | 概率 | 影响 | 应对策略 |
|-----|------|------|---------|
| 测试编写时间超预期 | 中 | 高 | 优先测试核心业务逻辑 |
| 复杂业务逻辑难以测试 | 中 | 中 | 重构代码提高可测试性 |
| 覆盖率提升遇到瓶颈 | 低 | 中 | 调整JaCoCo排除规则 |
| 测试运行时间过长 | 低 | 低 | 并行测试 + 选择性执行 |

---

## 📝 每日检查清单

### 开发人员每日任务

- [ ] 运行新增测试 `mvn test -Dtest=NewTest`
- [ ] 运行完整测试套件 `mvn test`
- [ ] 检查覆盖率报告 `target/site/jacoco/index.html`
- [ ] 提交代码并推送
- [ ] 更新本规划文档进度

### 代码审查检查项

- [ ] 测试命名清晰：`methodName_Scenario_ExpectedResult`
- [ ] 使用@DisplayName说明测试目的
- [ ] 每个测试一个断言概念
- [ ] Mock对象正确配置
- [ ] 测试数据准备完整

---

## 🔗 相关资源

### 文档
- [测试指南](backend/src/test/CONTROLLER_TEST_GUIDE.md)
- [测试模板](backend/src/test/CONTROLLER_TEST_TEMPLATE.txt)
- [项目结构](PROJECT_STRUCTURE.md)
- [快速开始](QUICK_START.md)

### 命令速查

```bash
# 运行所有测试
cd backend && mvn test

# 运行特定测试
cd backend && mvn test -Dtest=RatingServiceTest

# 生成覆盖率报告
cd backend && mvn clean test jacoco:report

# 查看覆盖率
cd backend && open target/site/jacoco/index.html

# 快速测试（跳过集成测试）
cd backend && mvn test -DskipITs
```

---

## ✅ 完成标准

### Definition of Done

- [x] 所有Controller测试通过 (78个测试)
- [ ] 所有Service测试通过 (目标: 50+个测试)
- [ ] 所有Security测试通过 (目标: 35+个测试)
- [ ] 整体测试覆盖率达到85%
- [ ] 所有测试在CI中通过
- [ ] 代码审查完成
- [ ] 文档更新完成

---

**最后更新：** 2026-02-13  
**下次更新：** 每日更新进度  
**负责人：** Development Team  
**计划版本：** v1.0
