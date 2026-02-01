# 贡献指南

感谢你对用户认证系统项目的关注！本文档提供了如何为项目做出贡献的指南。

## 目录

- [开发环境设置](#开发环境设置)
- [开发流程](#开发流程)
- [代码规范](#代码规范)
- [提交规范](#提交规范)
- [测试要求](#测试要求)
- [Pull Request流程](#pull-request流程)

## 开发环境设置

### 1. Fork和克隆项目

```bash
# Fork项目到你的GitHub账号
# 然后克隆你的fork
git clone https://github.com/your-username/user-auth-system.git
cd user-auth-system

# 添加上游仓库
git remote add upstream https://github.com/original-owner/user-auth-system.git
```

### 2. 安装依赖

参考 [QUICK_START.md](QUICK_START.md) 设置开发环境。

### 3. 创建开发分支

```bash
# 从main分支创建新分支
git checkout -b feature/your-feature-name

# 或修复bug
git checkout -b fix/bug-description
```

## 开发流程

### 1. 查看任务列表

查看 [tasks.md](.kiro/specs/user-auth-system/tasks.md) 了解待实现的功能。

### 2. 理解需求和设计

- 阅读 [requirements.md](.kiro/specs/user-auth-system/requirements.md)
- 阅读 [design.md](.kiro/specs/user-auth-system/design.md)
- 确保理解功能需求和技术设计

### 3. 实现功能

- 遵循现有的代码结构
- 遵循代码规范
- 编写清晰的注释
- 保持代码简洁

### 4. 编写测试

**必须编写测试！** 没有测试的代码不会被合并。

#### 后端测试
- 单元测试：测试Service层和Repository层
- 集成测试：测试API端点
- 属性测试：验证正确性属性

#### Android测试
- 单元测试：测试ViewModel和Repository
- UI测试：测试用户界面交互

### 5. 运行测试

```bash
# 后端测试
cd backend
mvn test

# Android测试
cd android-app
./gradlew test
```

确保所有测试通过！

### 6. 提交代码

遵循提交规范（见下文）。

## 代码规范

### Java/Kotlin代码规范

#### 后端（Java）

```java
// 类名：大驼峰
public class UserService {
    
    // 常量：全大写，下划线分隔
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    
    // 变量：小驼峰
    private UserRepository userRepository;
    
    // 方法：小驼峰，动词开头
    public User findUserByUsername(String username) {
        // 实现
    }
    
    // 注释：清晰说明意图
    /**
     * 验证用户凭证并生成JWT令牌
     * 
     * @param username 用户名
     * @param password 密码
     * @return 认证响应，包含令牌
     * @throws AuthenticationException 认证失败时抛出
     */
    public AuthResponse authenticate(String username, String password) {
        // 实现
    }
}
```

#### Android（Kotlin）

```kotlin
// 类名：大驼峰
class AuthViewModel : ViewModel() {
    
    // 常量：全大写，下划线分隔
    companion object {
        private const val MIN_PASSWORD_LENGTH = 8
    }
    
    // 变量：小驼峰
    private val authRepository: AuthRepository
    
    // 函数：小驼峰，动词开头
    fun login(username: String, password: String) {
        // 实现
    }
    
    // 注释：清晰说明意图
    /**
     * 注册新用户
     * 
     * @param username 用户名（3-20字符）
     * @param password 密码（至少8字符）
     */
    fun register(username: String, password: String) {
        // 实现
    }
}
```

### 代码格式化

#### Java
使用Google Java Style Guide或项目配置的Checkstyle规则。

```bash
# 使用Maven插件格式化
mvn spotless:apply
```

#### Kotlin
使用ktlint进行格式化。

```bash
# 格式化代码
./gradlew ktlintFormat

# 检查格式
./gradlew ktlintCheck
```

### 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | `UserService`, `AuthController` |
| 接口 | 大驼峰 | `UserRepository`, `AuthService` |
| 方法/函数 | 小驼峰 | `findUser()`, `validatePassword()` |
| 变量 | 小驼峰 | `username`, `authToken` |
| 常量 | 全大写+下划线 | `MAX_ATTEMPTS`, `DEFAULT_TIMEOUT` |
| 包名 | 全小写 | `com.example.userauth.service` |

## 提交规范

### 提交消息格式

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type类型

- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式（不影响功能）
- `refactor`: 重构（不是新功能也不是修复bug）
- `test`: 添加或修改测试
- `chore`: 构建过程或辅助工具的变动

### Scope范围

- `backend`: 后端相关
- `android`: Android相关
- `docs`: 文档相关
- `scripts`: 脚本相关
- `auth`: 认证模块
- `evaluation`: 评价系统
- `fruit`: 水果查询

### 示例

```bash
# 新功能
git commit -m "feat(backend): 实现用户注册API"

# 修复bug
git commit -m "fix(android): 修复登录界面密码显示问题"

# 文档更新
git commit -m "docs: 更新API文档"

# 测试
git commit -m "test(backend): 添加用户注册的属性测试"
```

### 详细提交消息

```
feat(backend): 实现JWT令牌认证

- 添加JwtTokenProvider类
- 实现令牌生成和验证方法
- 配置Spring Security过滤器
- 添加单元测试

Closes #123
```

## 测试要求

### 测试覆盖率要求

- **后端**: 代码覆盖率 ≥ 80%
- **Android**: ViewModel和Repository覆盖率 ≥ 70%

### 必须测试的内容

#### 后端
- ✅ 所有Service层方法
- ✅ 所有API端点
- ✅ 所有正确性属性
- ✅ 错误处理逻辑
- ✅ 权限验证

#### Android
- ✅ 所有ViewModel方法
- ✅ 所有Repository方法
- ✅ 关键UI交互（可选）

### 测试命名规范

```java
// 后端测试
@Test
public void shouldReturnUserWhenUsernameExists() {
    // given
    String username = "testuser";
    
    // when
    User user = userService.findByUsername(username);
    
    // then
    assertNotNull(user);
    assertEquals(username, user.getUsername());
}
```

```kotlin
// Android测试
@Test
fun `should emit success state when login succeeds`() {
    // given
    val username = "testuser"
    val password = "password123"
    
    // when
    viewModel.login(username, password)
    
    // then
    val state = viewModel.loginState.value
    assertTrue(state is LoginState.Success)
}
```

## Pull Request流程

### 1. 更新你的分支

```bash
# 获取上游更新
git fetch upstream

# 合并到你的分支
git checkout main
git merge upstream/main

# 更新你的功能分支
git checkout feature/your-feature-name
git rebase main
```

### 2. 推送到你的Fork

```bash
git push origin feature/your-feature-name
```

### 3. 创建Pull Request

1. 访问GitHub上你的Fork
2. 点击 "New Pull Request"
3. 选择你的分支
4. 填写PR描述

### PR描述模板

```markdown
## 变更说明
简要描述这个PR做了什么。

## 相关Issue
Closes #123

## 变更类型
- [ ] 新功能
- [ ] Bug修复
- [ ] 文档更新
- [ ] 代码重构
- [ ] 测试

## 测试
- [ ] 所有测试通过
- [ ] 添加了新测试
- [ ] 测试覆盖率达标

## 检查清单
- [ ] 代码遵循项目规范
- [ ] 提交消息符合规范
- [ ] 文档已更新
- [ ] 没有引入新的警告
```

### 4. Code Review

- 响应审查意见
- 根据反馈修改代码
- 保持讨论专业和友好

### 5. 合并

PR被批准后，维护者会合并你的代码。

## 常见问题

### Q: 我应该从哪里开始？

A: 查看 [tasks.md](.kiro/specs/user-auth-system/tasks.md)，选择标记为"未开始"的任务。

### Q: 我的测试失败了怎么办？

A: 
1. 检查错误消息
2. 确保测试环境正确配置
3. 查看相关文档
4. 在Issue中寻求帮助

### Q: 如何运行单个测试？

A:
```bash
# 后端
mvn test -Dtest=UserServiceTest

# Android
./gradlew test --tests "*LoginViewModelTest"
```

### Q: 我可以同时处理多个任务吗？

A: 建议一次只处理一个任务，确保每个PR专注于单一功能。

### Q: 代码审查需要多长时间？

A: 通常在1-3个工作日内。复杂的PR可能需要更长时间。

## 行为准则

- 尊重所有贡献者
- 保持专业和友好
- 接受建设性批评
- 关注代码质量
- 帮助他人

## 获取帮助

- 📖 查看 [文档](docs/)
- 💬 在Issue中提问
- 📧 联系维护者

## 感谢

感谢你为项目做出贡献！每一个贡献都让项目变得更好。

---

Happy Coding! 🚀
