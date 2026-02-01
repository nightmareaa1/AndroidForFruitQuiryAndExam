# 项目文档

本目录包含用户认证系统的所有文档。

## 目录结构

```
docs/
├── api/                  # API文档
├── deployment/           # 部署指南
└── architecture/         # 架构设计文档
```

## 文档列表

### API文档 (`api/`)

- **openapi.yaml**: OpenAPI 3.0规范的完整API文档
- 包含所有端点的详细说明、请求/响应格式、错误码等

### 部署指南 (`deployment/`)

- **backend-deploy.md**: 后端服务部署指南
  - 环境配置
  - 数据库设置
  - 服务器部署
  - Docker部署
  - 监控和日志

- **android-build.md**: Android应用构建和发布指南
  - 签名配置
  - 构建APK/AAB
  - Google Play发布流程
  - 版本管理

### 开发指南 (`development/`)

- **database-integration-test-issues.md**: 数据库集成测试问题遗留文档
  - TestContainers Docker环境问题
  - Flyway迁移SQL兼容性问题
  - 临时解决方案和待解决问题
  - 解决计划和时间表

### 架构设计 (`architecture/`)

- **system-design.md**: 系统架构设计文档
  - 系统架构图
  - 技术选型说明
  - 数据流图
  - 安全架构
  - 性能优化策略

## 核心规格文档

项目的核心规格文档位于 `../.kiro/specs/user-auth-system/`:

- **requirements.md**: 详细的需求文档
  - 用户故事
  - 验收标准
  - 功能需求
  - 非功能需求

- **design.md**: 技术设计文档
  - 架构设计
  - 组件接口
  - 数据模型
  - API设计
  - 正确性属性

- **tasks.md**: 实现任务列表
  - 分阶段的实现计划
  - 任务依赖关系
  - 测试要求
  - 检查点

## 文档编写规范

### API文档

- 使用OpenAPI 3.0规范
- 包含完整的请求/响应示例
- 说明所有错误码和错误消息
- 提供认证和授权说明

### 部署文档

- 提供详细的步骤说明
- 包含配置文件示例
- 说明常见问题和解决方案
- 提供检查清单

### 架构文档

- 使用图表说明架构
- 解释技术选型理由
- 说明关键设计决策
- 包含性能和安全考虑

## 文档维护

- 所有文档使用Markdown格式
- 代码示例使用语法高亮
- 图表使用Mermaid或PlantUML
- 定期更新以反映最新变更

## 贡献指南

更新文档时请遵循以下原则：

1. **准确性**: 确保文档与实际代码一致
2. **完整性**: 提供足够的细节和示例
3. **清晰性**: 使用简洁明了的语言
4. **结构化**: 使用标题、列表和表格组织内容
5. **可维护性**: 避免重复，使用引用链接

## 相关资源

- [项目主README](../README.md)
- [Android应用README](../android-app/README.md)
- [后端服务README](../backend/README.md)
- [脚本说明](../scripts/README.md)
