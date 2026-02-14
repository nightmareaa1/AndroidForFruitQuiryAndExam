# Android测试覆盖率提升计划 (13% → 60%)

## 目标
将业务逻辑代码测试覆盖率从13%提升至60%

## 当前状态 (13%)
| 模块 | 覆盖率 | 待提升 |
|------|--------|--------|
| data.local | 70% | ✅ 已达标 |
| data.model | 65% | ✅ 已达标 |
| viewmodel | 10% | 🔴 +20% |
| data.repository | 7% | 🔴 +25% |
| data.api | 10% | 🔴 +15% |

## 执行计划

### Phase 1: Repository层测试 (+18% → 31%)
**预计耗时**: 2小时  
**目标覆盖率**: 25-30%

#### Task 1.1: FruitRepositoryTest (5 tests)
- [ ] 测试获取水果列表成功/失败
- [ ] 测试创建水果
- [ ] 测试更新水果
- [ ] 测试删除水果

#### Task 1.2: FruitDataRepositoryTest (5 tests)
- [ ] 测试获取水果数据
- [ ] 测试按类型筛选
- [ ] 测试CRUD操作

#### Task 1.3: CompetitionRepositoryTest (8 tests)
- [ ] 测试获取赛事列表
- [ ] 测试创建/更新/删除赛事
- [ ] 测试提交作品
- [ ] 测试获取作品列表

#### Task 1.4: RatingRepositoryTest (5 tests)
- [ ] 测试提交评分
- [ ] 测试获取评分列表
- [ ] 测试更新评分

---

### Phase 2: ViewModel层补充测试 (+15% → 46%)
**预计耗时**: 2小时  
**目标覆盖率**: 40-45%

#### Task 2.1: AuthViewModelTest (6 tests)
- [ ] 测试登录成功/失败
- [ ] 测试注册
- [ ] 测试登出
- [ ] 测试token刷新

#### Task 2.2: EntryAddViewModelTest (5 tests)
- [ ] 测试添加作品
- [ ] 测试图片上传
- [ ] 测试表单验证

#### Task 2.3: EntryEditViewModelTest (5 tests)
- [ ] 测试加载作品
- [ ] 测试更新作品
- [ ] 测试删除作品

#### Task 2.4: RatingViewModelTest (5 tests)
- [ ] 测试加载评分
- [ ] 测试提交评分
- [ ] 测试评分计算

---

### Phase 3: API层测试 (+10% → 56%)
**预计耗时**: 1.5小时  
**目标覆盖率**: 50-55%

#### Task 3.1: FruitApiServiceTest (4 tests)
- [ ] 测试水果API调用
- [ ] 测试错误处理

#### Task 3.2: CompetitionApiTest (4 tests)
- [ ] 测试赛事API调用
- [ ] 测试参赛作品API

#### Task 3.3: RatingApiTest (3 tests)
- [ ] 测试评分API调用

---

### Phase 4: 工具类与补充测试 (+6% → 62%)
**预计耗时**: 1小时  
**目标覆盖率**: 60%+

#### Task 4.1: AuthInterceptorTest补充 (3 tests)
- [ ] 测试token附加
- [ ] 测试token刷新

#### Task 4.2: DTO模型测试 (5 tests)
- [ ] 测试各种DTO转换

#### Task 4.3: 边界情况测试 (4 tests)
- [ ] 测试空数据
- [ ] 测试网络异常

---

## 总计
- **测试文件**: 15个新文件
- **测试方法**: 约72个新测试
- **预计总覆盖率**: 60-65%
- **总耗时**: 约6-7小时

## 验证步骤
每个Phase完成后：
1. 运行 `./gradlew testDebugUnitTest`
2. 运行 `./gradlew jacocoTestReportDebug`
3. 检查覆盖率报告
4. 确保所有测试通过

## 风险与应对
| 风险 | 应对 |
|------|------|
| 某些类依赖Android Context | 使用MockK mock Context |
| 文件上传测试复杂 | 简化测试，只验证调用 |
| 时间不足 | 优先高ROI的Repository和ViewModel |
