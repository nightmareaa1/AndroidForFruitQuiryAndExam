# Controller测试创建指南

## 使用模板创建新测试

### 1. 准备工作

复制模板文件：
```bash
cp CONTROLLER_TEST_TEMPLATE.txt {ControllerName}Test.java
```

### 2. 替换占位符

在模板中替换以下占位符：

| 占位符 | 替换为 | 示例 |
|--------|--------|------|
| `{ControllerName}` | 控制器类名 | `CompetitionController` |
| `{ServiceName}` | 服务类名 | `CompetitionService` |
| `{serviceName}` | 服务实例名（小写） | `competitionService` |
| `{RequestDto}` | 请求DTO类名 | `CompetitionRequest` |
| `{ResponseDto}` | 响应DTO类名 | `CompetitionResponse` |
| `/api/endpoint` | 实际API路径 | `/api/competitions` |

### 3. 调整测试场景

根据实际Controller功能，调整以下测试：

#### GET 测试
- ✅ `getAllItems_Success` - 获取列表成功
- ✅ `getAllItems_EmptyList` - 空列表情况
- ✅ `getItemById_Success` - 根据ID获取
- ✅ `getItemById_NotFound` - 资源不存在(404)
- ✅ `getItemById_InvalidId` - 无效ID格式

#### POST 测试
- ✅ `createItem_Success` - 创建成功(201)
- ✅ `createItem_InvalidRequestBody` - 无效请求体
- ✅ `createItem_MissingRequiredFields` - 缺少必填字段
- ✅ `createItem_Duplicate` - 重复资源(409)

#### PUT 测试
- ✅ `updateItem_Success` - 更新成功(200)
- ✅ `updateItem_NotFound` - 资源不存在(404)
- ✅ `updateItem_InvalidRequest` - 无效请求

#### DELETE 测试
- ✅ `deleteItem_Success` - 删除成功(204)
- ✅ `deleteItem_NotFound` - 资源不存在(404)

#### 错误处理
- ✅ `handleUnexpectedError` - 服务器错误(500)

### 4. 特殊测试场景

根据Controller添加特殊测试：

#### 文件上传
```java
@Test
@DisplayName("Should upload file successfully")
void uploadFile_Success() throws Exception {
    MockMultipartFile file = new MockMultipartFile(
        "file", "test.jpg", "image/jpeg", "content".getBytes());
    
    mockMvc.perform(multipart("/api/endpoint/{id}/files", TEST_ID)
            .file(file))
            .andExpect(status().isCreated());
}
```

#### 权限控制
```java
@Test
@DisplayName("Should return 403 for unauthorized access")
void unauthorizedAccess_Returns403() throws Exception {
    // 测试非管理员访问管理员接口
    mockMvc.perform(post("/api/admin/endpoint"))
            .andExpect(status().isForbidden());
}
```

#### 查询参数
```java
@Test
@DisplayName("Should filter items by status")
void getItems_WithFilter() throws Exception {
    mockMvc.perform(get("/api/endpoint")
            .param("status", "ACTIVE")
            .param("page", "0")
            .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
}
```

### 5. 最佳实践

#### A. 测试命名规范
```
方法名_场景_预期结果

示例：
- getCompetitionById_ExistingCompetition_ReturnsCompetition
- createCompetition_InvalidDate_Returns400
- deleteCompetition_NonAdmin_Returns403
```

#### B. 使用 @DisplayName
每个测试都要有清晰的描述：
```java
@Test
@DisplayName("Should return 404 when competition not found")
void getCompetition_NotFound() throws Exception { }
```

#### C. 测试数据准备
```java
@BeforeEach
void setUp() {
    // 使用真实的测试数据
    validRequest = createValidRequest();
    responseDto = createResponseDto();
}

private CompetitionRequest createValidRequest() {
    CompetitionRequest request = new CompetitionRequest();
    request.setName("测试赛事");
    request.setDescription("这是一个测试赛事");
    request.setStartDate(LocalDateTime.now().plusDays(1));
    request.setEndDate(LocalDateTime.now().plusDays(7));
    return request;
}
```

#### D. Mock 验证
```java
// 验证方法被调用
verify(competitionService).getCompetitionById(TEST_ID);

// 验证调用次数
verify(competitionService, times(1)).createCompetition(any());

// 验证从不调用
verify(competitionService, never()).deleteCompetition(any());
```

#### E. JSON Path 断言
```java
// 基本字段
.andExpect(jsonPath("$.id").value(1))
.andExpect(jsonPath("$.name").value("测试名称"))

// 数组
.andExpect(jsonPath("$.items").isArray())
.andExpect(jsonPath("$.items", hasSize(3)))
.andExpect(jsonPath("$.items[0].id").value(1))

// 嵌套对象
.andExpect(jsonPath("$.creator.username").value("admin"))

// 布尔值
.andExpect(jsonPath("$.active").value(true))

// null 检查
.andExpect(jsonPath("$.deletedAt").doesNotExist())
```

### 6. 运行测试

```bash
# 运行单个测试类
mvn test -Dtest=CompetitionControllerTest

# 运行多个测试类
mvn test -Dtest=CompetitionControllerTest,RatingControllerTest

# 运行所有 Controller 测试
mvn test -Dtest="*ControllerTest"

# 生成覆盖率报告
mvn clean test jacoco:report

# 查看报告
open target/site/jacoco/index.html
```

### 7. 调试技巧

#### 打印请求和响应
```java
@Test
void debugTest() throws Exception {
    mockMvc.perform(get("/api/endpoint/{id}", TEST_ID))
            .andDo(print())  // 打印完整请求和响应
            .andExpect(status().isOk());
}
```

#### 检查异常
```java
@Test
void testWithException() throws Exception {
    when(service.getItem(any()))
            .thenThrow(new IllegalStateException("Invalid state"));
    
    mockMvc.perform(get("/api/endpoint/{id}", TEST_ID))
            .andExpect(status().isInternalServerError())
            .andExpect(result -> {
                Exception exception = result.getResolvedException();
                assert exception != null;
                assert exception.getMessage().contains("Invalid state");
            });
}
```

### 8. 检查清单

创建新测试前，确认：

- [ ] 了解Controller的所有端点
- [ ] 知道每个端点的输入输出
- [ ] 了解业务逻辑和边界条件
- [ ] 知道哪些端点需要特殊权限
- [ ] 已查看现有的Service测试

测试完成后，检查：

- [ ] 所有测试都能独立运行
- [ ] 没有不必要的Mock
- [ ] 测试名称清晰描述场景
- [ ] 覆盖率提升到预期水平
- [ ] 没有硬编码的魔法数字

### 9. 常见问题

**Q: 测试失败，返回403**
A: 确保使用了 `TestConfig` 并禁用了安全:
```java
@ContextConfiguration(classes = {Controller.class, TestConfig.class})
```

**Q: JSON解析错误**
A: 检查DTO的字段名和JSON Path是否匹配:
```java
// DTO 中是 getUserName()
.andExpect(jsonPath("$.userName").value("test"))
```

**Q: Mock不生效**
A: 确保使用了 `@MockBean` 而不是 `@Mock`:
```java
@MockBean  // ✅ 正确
private UserService userService;

@Mock  // ❌ 错误，在Spring测试中不会注入
private UserService userService;
```

**Q: 测试太慢**
A: 使用 `@WebMvcTest` 而不是 `@SpringBootTest`:
```java
@WebMvcTest(Controller.class)  // ✅ 只加载Web层
@SpringBootTest  // ❌ 加载整个上下文，慢10倍
```

### 10. 快速参考卡片

```java
// 常用 HTTP 方法
get("/api/endpoint")           // GET
post("/api/endpoint")          // POST
put("/api/endpoint/{id}")      // PUT
delete("/api/endpoint/{id}")   // DELETE
patch("/api/endpoint/{id}")    // PATCH

// 路径变量
get("/api/endpoint/{id}", 1L)
get("/api/endpoint/{id}", TEST_ID)

// 请求参数
.param("key", "value")
.param("page", "0")
.param("size", "10")

// 请求头
.header("Authorization", "Bearer token")
.contentType(MediaType.APPLICATION_JSON)

// 常用断言
status().isOk()           // 200
status().isCreated()      // 201
status().isNoContent()    // 204
status().isBadRequest()   // 400
status().isUnauthorized() // 401
status().isForbidden()    // 403
status().isNotFound()     // 404
status().isConflict()     // 409
status().isInternalServerError() // 500
```
