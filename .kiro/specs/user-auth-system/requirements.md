# 需求文档

## 简介

本文档规定了用户认证系统的需求，该系统由Android移动应用程序和Spring Boot后端服务组成。该系统使用户能够注册新账户并进行身份验证以访问应用程序。

## 项目结构

```
user-auth-system/
├── android-app/          # Android应用（Kotlin + Jetpack Compose）
├── backend/              # Spring Boot后端服务
├── docs/                 # 文档和设计资料
└── scripts/              # 部署和工具脚本
```

**说明：**
- `android-app/`：包含完整的Android项目，测试位于`app/src/test`（单元测试）和`app/src/androidTest`（UI测试）
- `backend/`：包含Spring Boot项目，测试位于`src/test`（单元测试、集成测试、属性测试）
- `docs/`：存放API文档、部署指南、架构图等
- `scripts/`：存放数据库初始化脚本、部署脚本等

## 术语表

- **用户（User）**: 与Android应用程序交互的个人
- **认证系统（Auth_System）**: 包括Android应用和后端服务的完整认证系统
- **后端服务（Backend_Service）**: 处理认证逻辑的Spring Boot REST API服务
- **Android应用（Android_App）**: 基于Kotlin的移动应用程序
- **数据库（Database）**: 存储用户凭证的MySQL数据库
- **注册（Registration）**: 创建新用户账户的过程
- **登录（Login）**: 对现有用户进行身份验证的过程
- **认证令牌（Auth_Token）**: 成功认证后颁发的安全令牌
- **密码哈希（Password_Hash）**: 用户密码的加密表示
- **用户名（Username）**: 用户的唯一标识符，用于登录
- **主界面（Main_Screen）**: 用户登录后显示的主导航界面
- **赛事界面（Event_Screen）**: 显示赛事评价系统的功能界面
- **水果营养查询界面（Fruit_Nutrition_Screen）**: 显示水果营养信息查询的功能界面
- **查询类型（Query_Type）**: 用户选择的查询维度，包括"营养成分"或"风味"
- **营养成分（Nutrition）**: 水果中包含的营养物质信息，如热量、维生素、矿物质等
- **风味（Flavor）**: 水果的味觉和口感特征描述
- **查询结果（Query_Result）**: 根据用户选择返回的水果信息数据
- **管理员（Administrator）**: 拥有系统最高权限的用户，可以管理评价模型
- **评价模型（Evaluation_Model）**: 定义评价指标体系的模板，包含多个评价参数及其权重
- **评价参数（Evaluation_Parameter）**: 评价模型中的单个评价维度，如外观、风味、滋味等
- **参数权重（Parameter_Weight）**: 评价参数在总分中所占的分值
- **赛事（Competition）**: 用户基于评价模型创建的评价活动，包含赛事名称、简介、截止时间等信息
- **赛事创建者（Competition_Creator）**: 创建赛事的用户，拥有该赛事的管理权限
- **赛事评委（Competition_Judge）**: 被赛事创建者添加到赛事中的用户，可以为该赛事的参赛作品打分
- **参赛作品（Competition_Entry）**: 用户提交到赛事的作品，包含作品名称、简介和参赛图片
- **作品审核（Entry_Review）**: 赛事创建者审核参赛作品的过程，决定作品是否通过审核
- **作品评分（Entry_Rating）**: 赛事评委基于评价模型对已审核通过的参赛作品进行打分的过程
- **评分数据（Rating_Data）**: 评委提交的作品评分信息，包含各参数的得分
- **赛事截止时间（Competition_Deadline）**: 赛事允许提交作品和评分的最后时间点
- **CI_CD_Pipeline**: 持续集成和持续部署流水线，自动化构建、测试和部署过程
- **Container_Environment**: 基于Docker的容器化运行环境
- **Development_Environment**: 本地开发环境，包含所有必需的服务和配置
- **Production_Environment**: 生产环境，面向最终用户的正式运行环境
- **Test_Environment**: 测试环境，用于验证功能和性能的环境
- **Property_Testing**: 属性测试，验证系统在各种输入下的通用属性
- **Health_Check**: 健康检查，监控系统运行状态的机制
- **Security_Configuration**: 安全配置，包括TLS、密钥管理、证书等安全措施

## 需求

### 需求 1: 用户注册

**用户故事：** 作为新用户，我想用用户名和密码注册账户，以便我可以访问应用程序。

#### 验收标准

1. WHEN 用户提供有效的用户名和密码时，THE Backend_Service SHALL 在Database中创建新的用户账户
2. WHEN 用户尝试使用已存在的用户名注册时，THE Backend_Service SHALL 返回错误，指示该用户名已被注册
3. WHEN 用户提供的用户名少于3个字符或超过20个字符时，THE Backend_Service SHALL 拒绝注册并返回验证错误
4. WHEN 用户提供少于8个字符的密码时，THE Backend_Service SHALL 拒绝注册并返回验证错误
5. WHEN 存储密码时，THE Backend_Service SHALL 仅存储Password_Hash，而不是明文密码
6. WHEN 注册成功时，THE Backend_Service SHALL 返回包含用户基本信息的成功响应
7. THE 用户名 SHALL 仅包含字母、数字和下划线

### 需求 2: 用户登录

**用户故事：** 作为注册用户，我想用我的用户名和密码登录，以便我可以访问我的账户。

#### 验收标准

1. WHEN 用户提供有效凭证时，THE Backend_Service SHALL 验证用户并返回Auth_Token
2. WHEN 用户提供错误的密码时，THE Backend_Service SHALL 拒绝登录并返回认证错误
3. WHEN 用户提供不存在的用户名时，THE Backend_Service SHALL 拒绝登录并返回认证错误
4. WHEN 认证成功时，THE Auth_Token SHALL 对后续API请求有效
5. WHEN 用户成功登录时，THE Android_App SHALL 在设备上安全存储Auth_Token

### 需求 3: Android注册界面

**用户故事：** 作为新用户，我想在Android设备上有一个简单的注册界面，以便我可以轻松创建账户。

#### 验收标准

1. THE Android_App SHALL 显示带有用户名和密码输入字段的注册界面
2. WHEN 用户在密码字段中输入文本时，THE Android_App SHALL 遮蔽密码字符
3. WHEN 用户提交注册表单时，THE Android_App SHALL 将凭证发送到Backend_Service
4. WHEN 注册成功时，THE Android_App SHALL 将用户导航到登录界面
5. WHEN 注册失败时，THE Android_App SHALL 显示Backend_Service返回的错误消息
6. WHILE 注册请求正在进行时，THE Android_App SHALL 显示加载指示器
7. THE 注册界面 SHALL 显示用户名和密码的格式要求提示

### 需求 4: Android登录界面

**用户故事：** 作为注册用户，我想在Android设备上有一个简单的登录界面，以便我可以访问我的账户。

#### 验收标准

1. THE Android_App SHALL 显示带有用户名和密码输入字段的登录界面
2. WHEN 用户在密码字段中输入文本时，THE Android_App SHALL 遮蔽密码字符
3. WHEN 用户提交登录表单时，THE Android_App SHALL 将凭证发送到Backend_Service
4. WHEN 登录成功时，THE Android_App SHALL 将用户导航到主界面
5. WHEN 登录失败时，THE Android_App SHALL 显示Backend_Service返回的错误消息
6. WHILE 登录请求正在进行时，THE Android_App SHALL 显示加载指示器

### 需求 5: 主导航界面

**用户故事：** 作为已登录用户，我想在主界面上看到功能选择按钮，以便我可以访问不同的功能模块。

#### 验收标准

1. WHEN 用户成功登录后，THE Android_App SHALL 显示Main_Screen
2. THE Main_Screen SHALL 包含一个导航到Event_Screen的按钮
3. THE Main_Screen SHALL 包含一个导航到Fruit_Nutrition_Screen的按钮
4. WHEN 用户点击赛事按钮时，THE Android_App SHALL 导航到Event_Screen
5. WHEN 用户点击水果营养查询按钮时，THE Android_App SHALL 导航到Fruit_Nutrition_Screen
6. THE Main_Screen SHALL 显示当前登录用户的用户名

### 需求 6: 管理员界面

**用户故事：** 作为管理员，我想访问管理员界面来管理评价模型，以便为用户创建赛事提供标准化的评价体系。

#### 验收标准

1. THE Android_App SHALL 在Main_Screen为管理员用户显示"管理员"按钮
2. WHEN 非管理员用户登录时，THE Android_App SHALL 隐藏"管理员"按钮
3. WHEN 管理员点击"管理员"按钮时，THE Android_App SHALL 导航到管理员界面
4. THE 管理员界面 SHALL 显示"评价模型管理"入口
5. THE 管理员界面 SHALL 包含返回Main_Screen的导航功能

### 需求 6.1: 评价模型管理

**用户故事：** 作为管理员，我想创建和管理评价模型，以便用户可以基于这些模型创建赛事。

#### 验收标准

1. THE Android_App SHALL 提供评价模型管理界面
2. WHEN 非管理员用户尝试访问时，THE Backend_Service SHALL 返回403 Forbidden错误
3. THE 评价模型管理界面 SHALL 显示所有已创建的评价模型列表
4. THE 模型列表项 SHALL 显示模型名称、参数数量和总分值
5. THE 评价模型管理界面 SHALL 提供"创建新模型"按钮
6. WHEN 管理员点击"创建新模型"时，THE Android_App SHALL 显示模型创建界面
7. THE 模型创建界面 SHALL 包含模型名称输入字段
8. THE 模型创建界面 SHALL 提供添加评价参数的功能
9. WHEN 添加评价参数时，THE 界面 SHALL 要求输入参数名称和权重分值
10. THE 模型创建界面 SHALL 实时显示所有参数的总分值
11. WHEN 管理员提交模型时，THE Android_App SHALL 验证总分值为100分
12. WHEN 验证成功时，THE Android_App SHALL 将模型数据发送到Backend_Service
13. WHEN 模型创建成功时，THE Backend_Service SHALL 在Database中持久化模型数据
14. THE 评价模型管理界面 SHALL 提供编辑和删除已有模型的功能
15. WHEN 模型已被赛事使用时，THE Backend_Service SHALL 禁止删除该模型
16. THE Backend_Service SHALL 在系统初始化时创建预设的"芒果"评价模型
17. THE "芒果"评价模型 SHALL 包含：外观10分、风味24分、滋味16分、质构18分、形状22分、营养10分

### 需求 6.2: 赛事系统

**用户故事：** 作为用户，我想基于评价模型创建和管理赛事，以便组织参赛作品的评价活动。

#### 验收标准

1. THE Android_App SHALL 提供Event_Screen界面
2. THE Event_Screen SHALL 显示界面标题"赛事"
3. THE Event_Screen SHALL 包含返回Main_Screen的导航功能
4. WHEN 用户在Event_Screen上点击返回按钮时，THE Android_App SHALL 导航回Main_Screen
5. THE Event_Screen SHALL 显示"我创建的赛事"、"我参加的赛事"和"我评审的赛事"三个标签页
6. THE Event_Screen SHALL 提供"创建新赛事"按钮
7. WHEN 用户点击"创建新赛事"时，THE Android_App SHALL 导航到赛事创建界面

### 需求 6.3: 创建和管理赛事

**用户故事：** 作为用户，我想基于评价模型创建赛事并管理赛事的评委和参赛作品，以便组织评价活动。

#### 验收标准

1. THE Android_App SHALL 提供赛事创建界面
2. THE 赛事创建界面 SHALL 包含赛事名称输入字段
3. THE 赛事创建界面 SHALL 包含赛事简介输入字段
4. THE 赛事创建界面 SHALL 包含评价模型选择下拉框
5. THE 评价模型选择下拉框 SHALL 显示所有可用的评价模型
6. THE 赛事创建界面 SHALL 包含赛事截止时间选择器
7. WHEN 用户提交赛事创建表单时，THE Android_App SHALL 验证所有必填字段已填写
8. WHEN 验证成功时，THE Android_App SHALL 将赛事数据（包含选择的评价模型ID）发送到Backend_Service
9. WHEN 赛事创建成功时，THE Backend_Service SHALL 在Database中持久化赛事数据
10. WHEN 赛事创建成功时，THE Backend_Service SHALL 将创建者设置为该赛事的赛事创建者
11. THE Android_App SHALL 提供赛事管理界面，显示用户创建的所有赛事
12. THE 赛事管理界面 SHALL 显示赛事名称、简介、评价模型名称、截止时间和状态
13. THE 赛事管理界面 SHALL 提供"编辑"和"删除"按钮
14. WHEN 用户点击"编辑"时，THE Android_App SHALL 允许修改赛事名称、简介和截止时间
15. WHEN 用户点击"删除"时，THE Android_App SHALL 提示确认并删除赛事
16. WHEN 赛事被删除时，THE Backend_Service SHALL 同时删除该赛事的所有参赛作品和评分数据

### 需求 6.4: 赛事评委管理

**用户故事：** 作为赛事创建者，我想添加和删除赛事评委，以便指定谁可以为参赛作品打分。

#### 验收标准

1. THE Android_App SHALL 在赛事管理界面提供"管理评委"入口
2. WHEN 用户点击"管理评委"时，THE Android_App SHALL 显示评委管理界面
3. THE 评委管理界面 SHALL 显示当前赛事的所有评委列表
4. THE 评委列表项 SHALL 显示评委的用户名
5. THE 评委管理界面 SHALL 提供"添加评委"按钮
6. WHEN 用户点击"添加评委"时，THE Android_App SHALL 显示用户名输入框
7. WHEN 用户输入用户名并提交时，THE Android_App SHALL 向Backend_Service发送添加评委请求
8. WHEN 用户名存在于系统数据库中时，THE Backend_Service SHALL 将该用户添加为赛事评委
9. WHEN 用户名不存在时，THE Backend_Service SHALL 返回错误提示"用户不存在"
10. WHEN 用户已是该赛事评委时，THE Backend_Service SHALL 返回错误提示"该用户已是评委"
11. THE 评委列表项 SHALL 提供"删除"按钮
12. WHEN 用户点击"删除"时，THE Android_App SHALL 提示确认并删除评委
13. WHEN 评委被删除时，THE Backend_Service SHALL 从赛事评委列表中移除该用户

### 需求 6.5: 参赛作品提交

**用户故事：** 作为用户，我想参加赛事并提交我的参赛作品，以便参与评价活动。

#### 验收标准

1. THE Android_App SHALL 在Event_Screen的"我参加的赛事"标签页显示所有已审核通过的赛事列表
2. THE 赛事列表项 SHALL 显示赛事名称、简介、截止时间和状态
3. THE 赛事列表项 SHALL 提供"提交作品"按钮
4. WHEN 用户点击"提交作品"时，THE Android_App SHALL 显示作品提交界面
5. THE 作品提交界面 SHALL 包含作品名称输入字段
6. THE 作品提交界面 SHALL 包含作品简介输入字段
7. THE 作品提交界面 SHALL 提供图片上传功能
8. WHEN 用户上传图片时，THE Android_App SHALL 支持从相册选择或拍照
9. WHEN 用户提交作品时，THE Android_App SHALL 验证所有必填字段已填写
10. WHEN 验证成功时，THE Android_App SHALL 将作品数据和图片发送到Backend_Service
11. WHEN 作品提交成功时，THE Backend_Service SHALL 在Database中持久化作品数据
12. WHEN 作品提交成功时，THE Backend_Service SHALL 将作品状态设置为"待审核"
13. WHEN 赛事截止时间已过时，THE Backend_Service SHALL 拒绝新的作品提交
14. THE Android_App SHALL 显示用户已提交的作品列表
15. THE 作品列表项 SHALL 显示作品名称、简介、图片和审核状态

### 需求 6.6: 参赛作品审核

**用户故事：** 作为赛事创建者，我想审核参赛作品，以便决定哪些作品可以参与评分。

#### 验收标准

1. THE Android_App SHALL 在赛事管理界面提供"审核作品"入口
2. WHEN 用户点击"审核作品"时，THE Android_App SHALL 显示作品审核界面
3. THE 作品审核界面 SHALL 显示所有待审核的参赛作品列表
4. THE 作品列表项 SHALL 显示作品名称、简介、参赛图片和提交者用户名
5. THE 作品列表项 SHALL 提供"通过"和"拒绝"按钮
6. WHEN 用户点击"通过"时，THE Android_App SHALL 向Backend_Service发送审核通过请求
7. WHEN 审核通过时，THE Backend_Service SHALL 将作品状态更新为"已审核"
8. WHEN 用户点击"拒绝"时，THE Android_App SHALL 显示拒绝原因输入框
9. WHEN 用户提交拒绝原因时，THE Android_App SHALL 向Backend_Service发送审核拒绝请求
10. WHEN 审核拒绝时，THE Backend_Service SHALL 将作品状态更新为"已拒绝"并存储拒绝原因
11. THE 作品审核界面 SHALL 提供筛选功能，可按"待审核"、"已审核"、"已拒绝"状态筛选作品

### 需求 6.7: 作品评分

**用户故事：** 作为赛事评委，我想基于赛事的评价模型为已审核通过的参赛作品打分，以便完成评价任务。

#### 验收标准

1. THE Android_App SHALL 在Event_Screen的"我评审的赛事"标签页显示用户作为评委的所有赛事列表
2. THE 赛事列表项 SHALL 显示赛事名称、简介、评价模型名称、截止时间和评分进度
3. THE 赛事列表项 SHALL 提供"开始评分"按钮
4. WHEN 用户点击"开始评分"时，THE Android_App SHALL 显示作品评分界面
5. THE 作品评分界面 SHALL 显示所有已审核通过的参赛作品列表
6. THE 作品列表项 SHALL 显示作品名称、简介和参赛图片
7. WHEN 用户选择一个作品进行评分时，THE Android_App SHALL 显示该赛事评价模型的所有评价参数
8. THE 评分界面 SHALL 为每个评价参数显示参数名称和权重分值
9. THE 评分界面 SHALL 为每个评价参数提供评分输入控件（0到参数权重分值之间）
10. THE 评分界面 SHALL 提供评语输入框
11. THE 评分界面 SHALL 实时显示当前总分
12. WHEN 用户输入评分和评语后点击"提交"时，THE Android_App SHALL 验证所有参数都已评分
13. WHEN 验证成功时，THE Android_App SHALL 将评分数据（包含各参数得分）发送到Backend_Service
14. WHEN 评分提交成功时，THE Backend_Service SHALL 在Database中持久化评分数据
15. WHEN 赛事截止时间已过时，THE Backend_Service SHALL 拒绝新的评分提交
16. THE 作品评分界面 SHALL 显示用户已评分的作品数量和总作品数量
17. THE 作品评分界面 SHALL 支持修改已提交的评分和评语

### 需求 6.8: 评分数据展示

**用户故事：** 作为用户，我想查看赛事的评分数据，以便了解作品的评价结果。

#### 验收标准

1. THE Android_App SHALL 在赛事详情界面提供"查看评分"入口
2. WHEN 用户点击"查看评分"时，THE Android_App SHALL 显示评分数据展示界面
3. THE 评分数据展示界面 SHALL 显示所有已审核通过的参赛作品列表
4. THE 作品列表项 SHALL 显示作品名称、简介、参赛图片和总平均分
5. THE 作品列表项 SHALL 提供"查看详情"按钮
6. WHEN 用户点击"查看详情"时，THE Android_App SHALL 显示作品评分详情界面
7. THE 作品评分详情界面 SHALL 显示所有评委的评分和评语
8. THE 评分详情 SHALL 显示评委用户名、各评价参数得分、总分和评语
9. THE 评分详情 SHALL 显示每个评价参数的平均分
10. THE 评分数据展示界面 SHALL 支持按总平均分排序
11. WHEN 赛事截止时间未到时，THE 评分数据 SHALL 仅对赛事创建者和评委可见
12. WHEN 赛事截止时间已过时，THE 评分数据 SHALL 对所有用户可见

### 需求 7: 水果营养查询界面

**用户故事：** 作为用户，我想通过选择查询类型和水果来查询营养信息，以便我可以了解不同水果的营养成分或风味特点。

#### 验收标准

1. THE Android_App SHALL 提供Fruit_Nutrition_Screen界面
2. THE Fruit_Nutrition_Screen SHALL 显示界面标题"水果营养查询"
3. THE Fruit_Nutrition_Screen SHALL 包含返回Main_Screen的导航功能
4. WHEN 用户在Fruit_Nutrition_Screen上点击返回按钮时，THE Android_App SHALL 导航回Main_Screen
5. THE Fruit_Nutrition_Screen SHALL 显示第一个下拉选择框，包含"营养成分"和"风味"两个选项
6. THE Fruit_Nutrition_Screen SHALL 显示第二个下拉选择框，包含"芒果"和"香蕉"两个水果选项
7. THE Fruit_Nutrition_Screen SHALL 显示一个"查询"按钮
8. WHEN 用户未选择查询类型或水果时点击查询按钮，THE Android_App SHALL 显示友好的提示信息要求用户完成选择
9. WHEN 用户选择了查询类型和水果后点击查询按钮，THE Android_App SHALL 向Backend_Service发送查询请求
10. WHEN 查询成功时，THE Android_App SHALL 以清晰的表格形式展示查询结果
11. THE 查询结果表格 SHALL 包含两列：成分名称和数值
12. WHEN 查询结果为营养成分时，THE 表格 SHALL 显示营养成分名称和对应的数值
13. WHEN 查询结果为风味时，THE 表格 SHALL 显示风味成分名称和对应的数值
13. WHILE 查询请求正在进行时，THE Android_App SHALL 显示加载指示器
14. WHEN 查询失败时，THE Android_App SHALL 显示友好的错误提示信息
15. THE 查询结果表格 SHALL 使用清晰的视觉设计，包括表头、行分隔线和适当的间距
16. THE 查询结果表格 SHALL 支持显示大数值（如89231.12）和小数值（如2305.42）

### 需求 8: 后端API端点

**用户故事：** 作为系统架构师，我想要定义良好的REST API端点，以便Android应用可以与后端服务通信。

#### 验收标准

1. THE Backend_Service SHALL 在/api/auth/register提供POST端点用于用户注册
2. THE Backend_Service SHALL 在/api/auth/login提供POST端点用于用户认证
3. THE Backend_Service SHALL 在/api/fruit/query提供GET端点用于水果营养查询
4. THE Backend_Service SHALL 在/api/evaluation-models提供GET和POST端点用于评价模型的查询和创建
5. THE Backend_Service SHALL 在/api/evaluation-models/{id}提供GET、PUT和DELETE端点用于评价模型的获取、更新和删除
6. THE Backend_Service SHALL 在/api/competitions提供GET和POST端点用于赛事的查询和创建
7. THE Backend_Service SHALL 在/api/competitions/{id}提供GET、PUT和DELETE端点用于赛事的获取、更新和删除
8. THE Backend_Service SHALL 在/api/competitions/{id}/judges提供GET和POST端点用于赛事评委的查询和添加
9. THE Backend_Service SHALL 在/api/competitions/{id}/judges/{userId}提供DELETE端点用于删除赛事评委
10. THE Backend_Service SHALL 在/api/competitions/{id}/entries提供GET和POST端点用于参赛作品的查询和提交
11. THE Backend_Service SHALL 在/api/competitions/{id}/entries/{entryId}提供GET端点用于获取作品详情
12. THE Backend_Service SHALL 在/api/competitions/{id}/entries/{entryId}/review提供POST端点用于审核参赛作品
13. THE Backend_Service SHALL 在/api/competitions/{id}/entries/{entryId}/ratings提供GET和POST端点用于作品评分的查询和提交
14. THE Backend_Service SHALL 在/api/competitions/{id}/ratings提供GET端点用于获取赛事的所有评分数据
15. WHEN 收到API请求时，THE Backend_Service SHALL 验证请求体格式
16. WHEN API请求包含无效JSON时，THE Backend_Service SHALL 返回400 Bad Request错误
17. WHEN 注册成功时，THE Backend_Service SHALL 返回201 Created状态码
18. WHEN 登录成功时，THE Backend_Service SHALL 返回200 OK状态码和Auth_Token
19. WHEN 认证失败时，THE Backend_Service SHALL 返回401 Unauthorized状态码
20. WHEN 水果查询请求缺少必需参数时，THE Backend_Service SHALL 返回400 Bad Request错误
21. WHEN 水果查询成功时，THE Backend_Service SHALL 返回200 OK状态码和查询结果数据
22. WHEN 用户无权限访问资源时，THE Backend_Service SHALL 返回403 Forbidden状态码
23. WHEN 请求的资源不存在时，THE Backend_Service SHALL 返回404 Not Found状态码

### 需求 9: 数据持久化

**用户故事：** 作为系统管理员，我想要用户数据安全地存储在数据库中，以便用户账户在服务器重启后仍然存在。

#### 验收标准

1. THE Backend_Service SHALL 在MySQL Database中存储用户记录
2. WHEN 用户注册时，THE Database SHALL 持久化用户的用户名、Password_Hash和管理员标识
3. THE Database SHALL 在用户名字段上强制唯一性约束
4. WHEN Backend_Service启动时，THE Database连接 SHALL 自动建立
5. IF Database连接失败，THEN THE Backend_Service SHALL 记录错误并重试连接
6. THE Database SHALL 包含用户管理员标识字段，用于区分普通用户和管理员

### 需求 9.1: 评价模型数据持久化

**用户故事：** 作为系统管理员，我想要评价模型数据安全地存储在数据库中，以便用户可以基于这些模型创建赛事。

#### 验收标准

1. THE Database SHALL 包含评价模型表，存储模型名称和创建时间
2. THE Database SHALL 包含评价参数表，存储参数名称、权重分值和所属模型ID
3. WHEN 评价模型被删除时，THE Database SHALL 检查是否有赛事使用该模型
4. WHEN 有赛事使用该模型时，THE Database SHALL 阻止删除操作
5. THE Database SHALL 支持高效查询所有评价模型及其参数
6. WHEN Backend_Service启动时，THE Database SHALL 包含预设的"芒果"评价模型数据

### 需求 9.2: 赛事系统数据持久化

**用户故事：** 作为系统管理员，我想要赛事系统的所有数据安全地存储在数据库中，以便支持完整的赛事流程。

#### 验收标准

1. THE Database SHALL 包含赛事表，存储赛事名称、简介、创建者ID、评价模型ID、截止时间和状态
2. THE Database SHALL 包含赛事评委表，存储赛事ID和评委用户ID的关联关系
3. THE Database SHALL 包含参赛作品表，存储作品名称、简介、图片路径、所属赛事ID、提交者ID和审核状态
4. THE Database SHALL 包含作品评分表，存储评委ID、作品ID、评价参数ID、参数得分和评语
5. WHEN 赛事被删除时，THE Database SHALL 级联删除该赛事的所有评委关联、参赛作品和评分数据
6. WHEN 赛事到达截止时间时，THE Database SHALL 更新赛事状态为"已结束"
7. THE Database SHALL 支持高效查询用户创建的所有赛事
8. THE Database SHALL 支持高效查询用户参加的所有赛事
9. THE Database SHALL 支持高效查询用户作为评委的所有赛事
10. THE Database SHALL 支持高效查询单个赛事的所有参赛作品
11. THE Database SHALL 支持高效查询单个作品的所有评分数据及各参数得分

### 需求 10: 水果营养数据管理

**用户故事：** 作为系统管理员，我想要水果营养数据存储在数据库中，以便用户可以查询准确的营养信息。

#### 验收标准

1. THE Backend_Service SHALL 在Database中存储水果营养数据
2. THE Database SHALL 包含水果基本信息表，存储水果名称和标识
3. THE Database SHALL 包含营养成分表，存储每种水果的营养成分名称和数值
4. THE Database SHALL 包含风味特征表，存储每种水果的风味成分名称和数值
5. WHEN Backend_Service启动时，THE Database SHALL 包含芒果和香蕉的预置数据
6. WHEN 收到水果查询请求时，THE Backend_Service SHALL 根据查询类型（营养成分或风味）和水果名称从Database检索相应数据
7. WHEN 查询的水果不存在时，THE Backend_Service SHALL 返回404 Not Found错误
8. THE 营养成分数据 SHALL 包含成分名称和数值
9. THE 风味特征数据 SHALL 包含成分名称和数值

### 需求 10.1: 文件上传和存储

**用户故事：** 作为用户，我想上传参赛作品图片到服务器，以便其他用户可以查看我的作品。

#### 验收标准

1. THE Backend_Service SHALL 支持图片文件上传
2. THE Backend_Service SHALL 验证上传文件的格式，仅接受常见图片格式（JPG、PNG、WEBP）
3. THE Backend_Service SHALL 验证上传文件的大小，单个文件不超过10MB
4. WHEN 文件上传成功时，THE Backend_Service SHALL 将文件存储到服务器文件系统
5. THE Backend_Service SHALL 为每个上传的文件生成唯一的文件名
6. THE Backend_Service SHALL 在Database中存储文件的访问路径
7. THE Backend_Service SHALL 提供文件访问端点，允许客户端下载已上传的图片
8. WHEN 访问不存在的文件时，THE Backend_Service SHALL 返回404 Not Found错误

### 需求 11: 安全要求

**用户故事：** 作为注重安全的用户，我想要我的密码被安全处理，以便我的账户保持受保护状态。

#### 验收标准

1. WHEN Backend_Service收到密码时，THE Backend_Service SHALL 在存储前使用安全哈希算法对其进行哈希处理
2. THE Backend_Service SHALL 使用BCrypt或等效算法进行密码哈希
3. WHEN 登录时比较密码时，THE Backend_Service SHALL 比较哈希值
4. WHEN 与远程服务器通信时，THE Android_App SHALL 通过HTTPS传输凭证
5. THE Auth_Token SHALL 包含过期时间以限制其有效期

### 需求 12: 部署配置

**用户故事：** 作为系统管理员，我想要后端服务易于部署到远程服务器，以便用户可以从他们的移动设备访问它。

#### 验收标准

1. THE Backend_Service SHALL 支持通过环境变量或配置文件配置数据库连接
2. THE Backend_Service SHALL 支持通过环境变量或配置文件配置服务器端口
3. THE Backend_Service SHALL 包含CORS配置以允许来自Android_App的请求
4. WHEN 部署时，THE Backend_Service SHALL 记录启动信息，包括端口和数据库连接状态
5. THE Backend_Service SHALL 打包为可执行JAR文件以便于部署

### 需求 13: 统一开发环境

**用户故事：** 作为开发人员，我希望有统一的本地开发环境，以便快速启动项目并与团队保持一致的开发体验。

#### 验收标准

1. WHEN 开发人员执行环境启动命令 THEN Container_Environment SHALL 在5分钟内完成所有服务的启动
2. WHEN 开发环境启动完成 THEN Development_Environment SHALL 包含MySQL数据库、后端服务和所有必需的依赖服务
3. WHEN 新团队成员首次设置环境 THEN Development_Environment SHALL 通过单一命令完成所有配置
4. WHEN 开发环境运行 THEN Container_Environment SHALL 与生产环境使用相同的数据库类型和版本
5. WHEN 开发人员修改代码 THEN Development_Environment SHALL 支持热重载和实时调试

### 需求 14: CI/CD流水线自动化

**用户故事：** 作为DevOps工程师，我希望建立统一的CI/CD流水线，以便自动化构建、测试和部署多模块项目。

#### 验收标准

1. WHEN 代码推送到主分支 THEN CI_CD_Pipeline SHALL 自动触发构建和测试流程
2. WHEN CI/CD流水线执行 THEN CI_CD_Pipeline SHALL 并行构建Android应用和后端服务
3. WHEN 所有测试通过 THEN CI_CD_Pipeline SHALL 自动部署到测试环境
4. WHEN 构建或测试失败 THEN CI_CD_Pipeline SHALL 发送通知并阻止部署
5. WHEN 部署完成 THEN CI_CD_Pipeline SHALL 执行健康检查验证部署成功

### 需求 15: 测试环境一致性

**用户故事：** 作为测试工程师，我希望测试环境与生产环境保持一致，以便确保测试结果的可靠性。

#### 验收标准

1. WHEN 运行后端测试 THEN Test_Environment SHALL 使用MySQL容器而非H2内存数据库
2. WHEN 执行集成测试 THEN Test_Environment SHALL 模拟真实的数据库约束和SQL方言
3. WHEN 运行属性测试 THEN Property_Testing SHALL 覆盖所有已定义的正确性属性
4. WHEN 执行Android UI测试 THEN Test_Environment SHALL 支持自动化UI测试执行
5. WHEN 测试完成 THEN Test_Environment SHALL 生成详细的测试报告和覆盖率数据

### 需求 16: 生产就绪的安全配置

**用户故事：** 作为安全工程师，我希望建立标准化的安全配置，以便确保生产环境的安全性。

#### 验收标准

1. WHEN 部署到生产环境 THEN Security_Configuration SHALL 启用HTTPS和TLS 1.3
2. WHEN 管理密钥和证书 THEN Security_Configuration SHALL 使用安全的密钥管理方案
3. WHEN 证书即将过期 THEN Security_Configuration SHALL 自动更新SSL证书
4. WHEN 处理敏感数据 THEN Security_Configuration SHALL 加密存储所有敏感配置
5. WHEN 系统运行 THEN Security_Configuration SHALL 记录所有安全相关事件

### 需求 17: 容器化部署标准

**用户故事：** 作为运维工程师，我希望建立标准化的容器化部署方案，以便简化部署和运维工作。

#### 验收标准

1. WHEN 构建应用镜像 THEN Container_Environment SHALL 生成优化的多阶段Docker镜像
2. WHEN 部署服务 THEN Container_Environment SHALL 使用docker-compose编排所有服务
3. WHEN 服务启动 THEN Container_Environment SHALL 配置适当的资源限制和健康检查
4. WHEN 服务异常 THEN Container_Environment SHALL 自动重启失败的容器
5. WHEN 扩展服务 THEN Container_Environment SHALL 支持水平扩展和负载均衡

### 需求 18: 监控和运维方案

**用户故事：** 作为运维工程师，我希望建立完善的监控和运维方案，以便及时发现和解决系统问题。

#### 验收标准

1. WHEN 系统运行 THEN Health_Check SHALL 监控所有关键服务的健康状态
2. WHEN 收集系统指标 THEN Health_Check SHALL 记录CPU、内存、磁盘和网络使用情况
3. WHEN 发生异常 THEN Health_Check SHALL 发送告警通知相关人员
4. WHEN 查看日志 THEN Health_Check SHALL 提供集中化的日志管理和查询
5. WHEN 分析性能 THEN Health_Check SHALL 提供应用性能监控和分析报告

### 需求 19: 数据库管理优化

**用户故事：** 作为数据库管理员，我希望优化数据库管理流程，以便提高数据安全性和运维效率。

#### 验收标准

1. WHEN 执行数据库迁移 THEN Container_Environment SHALL 使用版本化的迁移脚本
2. WHEN 备份数据库 THEN Container_Environment SHALL 自动执行定期备份
3. WHEN 恢复数据 THEN Container_Environment SHALL 支持快速数据恢复
4. WHEN 监控数据库 THEN Health_Check SHALL 监控数据库性能和连接状态
5. WHEN 数据库异常 THEN Health_Check SHALL 自动执行故障转移

### 需求 20: 配置管理标准化

**用户故事：** 作为系统管理员，我希望标准化配置管理，以便简化不同环境的配置维护。

#### 验收标准

1. WHEN 管理配置 THEN Security_Configuration SHALL 使用环境变量管理敏感配置
2. WHEN 切换环境 THEN Security_Configuration SHALL 支持多环境配置文件
3. WHEN 更新配置 THEN Security_Configuration SHALL 验证配置的有效性
4. WHEN 部署应用 THEN Security_Configuration SHALL 自动加载对应环境的配置
5. WHEN 配置变更 THEN Security_Configuration SHALL 记录配置变更历史


## 测试要求

### 测试分类

本系统的测试分为以下几类：

**后端测试（必须执行）**
- 单元测试：测试Service层、Repository层的业务逻辑
- 集成测试：使用MySQL容器测试API端点和数据持久化，确保与生产环境一致性
- 属性测试：使用jqwik框架验证正确性属性
- 所有测试可通过Maven/Gradle命令独立执行，支持CI/CD自动化

**Android测试（必须执行）**
- 单元测试：测试ViewModel、Repository的逻辑
- 集成测试：使用MockWebServer模拟后端响应
- UI测试：使用Espresso框架测试UI交互，支持CI/CD自动化执行
- 所有测试可通过Gradle命令独立执行

**性能测试（建议执行）**
- 高并发测试、负载测试、压力测试
- 不影响核心功能验证
- 用于评估系统在生产环境的性能表现

### 测试环境要求

1. **环境一致性**: 测试环境必须与生产环境保持一致
   - 后端测试使用MySQL容器，而非H2内存数据库
   - 数据库版本、配置与生产环境相同
   - 容器化测试环境，确保跨平台一致性

2. **自动化程度**: 所有测试必须支持自动化执行
   - 后端测试通过Maven/Gradle命令执行
   - Android测试通过Gradle命令执行
   - UI测试支持无头模式执行
   - 集成到CI/CD流水线中

3. **测试数据管理**: 标准化测试数据管理
   - 使用Docker容器提供隔离的测试数据库
   - 每次测试运行前自动重置数据库状态
   - 提供标准化的测试数据集

### 测试覆盖要求

1. 后端代码覆盖率应达到80%以上
2. Android ViewModel和Repository代码覆盖率应达到70%以上
3. 所有正确性属性必须通过属性测试验证
4. 所有API端点必须有集成测试覆盖
5. 关键用户流程必须有端到端测试覆盖
6. 所有安全相关功能必须有专门的安全测试
7. 性能关键路径必须有性能测试覆盖

### 测试报告要求

1. **覆盖率报告**: 生成详细的代码覆盖率报告
2. **测试结果报告**: 提供清晰的测试执行结果
3. **性能测试报告**: 记录响应时间、吞吐量等关键指标
4. **安全测试报告**: 记录安全漏洞扫描结果
5. **集成到CI/CD**: 测试报告自动发布到CI/CD平台
