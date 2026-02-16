# 项目文档中心

本文档中心包含用户认证与水果评测系统的所有技术文档、开发指南和部署手册。

## 文档导航

### 快速开始
- [快速开始指南](../QUICK_START.md) - 5分钟启动完整开发环境
- [项目主README](../README.md) - 项目概览和快速链接

### 项目规格文档

核心规格文档位于 `.kiro/specs/user-auth-system/`：

- **requirements.md** - 详细需求文档
  - 用户故事和验收标准
  - 功能需求和非功能需求
  - 用例和场景

- **design.md** - 技术设计文档
  - 系统架构设计
  - 数据模型设计
  - API接口设计
  - 正确性属性（31个）

- **tasks.md** - 实现任务清单
  - 分阶段实现计划
  - 任务依赖关系
  - 测试要求

## 文档目录结构

```
docs/
├── api/                           # API文档
│   └── openapi.yaml              # OpenAPI 3.0规范
│
├── deployment/                    # 部署指南
│   ├── backend-deployment-guide.md   # 后端部署指南
│   ├── jar-deployment-guide.md       # JAR包部署
│   ├── android-connect-backend.md    # Android连接后端
│   ├── PRODUCTION_DEPLOYMENT.md      # 生产部署
│   ├── ci-cd-setup.md                # CI/CD配置
│   ├── server-requirements.md        # 服务器要求
│   ├── 2gb-server-optimization.md    # 2GB服务器优化
│   ├── docker-mirror-china.md        # 国内Docker镜像
│   ├── create-github-release.md      # GitHub发布
│   └── download-jar-from-github.md   # 下载JAR包
│
├── development/                   # 开发文档
│   ├── README.md                      # 开发文档说明
│   ├── android-testing-analysis.md    # Android测试分析
│   ├── android-backend-crud-integration-plan.md  # Android后端集成
│   ├── android-ui-navigation-analysis.md         # UI导航分析
│   ├── api-integration-test-plan.md   # API集成测试计划
│   ├── api-integration-test-report.md # API集成测试报告
│   ├── e2e-integration-test-report.md # E2E测试报告
│   ├── database-integration-test-issues.md       # 数据库测试问题
│   ├── spring-boot-test-issues.md     # Spring Boot测试问题
│   ├── flyway-migration-analysis.md   # Flyway迁移分析
│   ├── ci-setup-summary.md            # CI设置总结
│   ├── ci-cd-testing-scope.md         # 测试范围
│   ├── ci-error-quick-reference.md    # 错误速查
│   ├── ci-error-troubleshooting-guide.md         # 错误排查
│   ├── troubleshooting-log.md         # 问题排查日志
│   ├── problem-solving-guide.md       # 问题解决指南
│   ├── MONITORING_LOGGING.md          # 监控和日志
│   └── checkpoint-29-*.md             # 检查点报告
│
├── security/                      # 安全文档
│   ├── security-configuration.md      # 安全配置
│   └── environment-key-management.md  # 密钥管理
│
├── plans/                         # 计划文档
│   ├── 2026-02-13-database-optimization-plan.md
│   ├── 2026-02-14-android-test-coverage-60-percent.md
│   └── android-coverage-60-percent-plan.md
│
└── handoff/                       # 交接文档
    └── android-test-coverage-handoff.md
```

## 文档分类

### 1. 部署文档 (`deployment/`)

生产环境部署相关文档：

| 文档 | 描述 | 适用场景 |
|------|------|----------|
| [backend-deployment-guide.md](deployment/backend-deployment-guide.md) | 后端服务完整部署指南 | 生产部署 |
| [jar-deployment-guide.md](deployment/jar-deployment-guide.md) | JAR包部署指南 | 传统服务器 |
| [PRODUCTION_DEPLOYMENT.md](deployment/PRODUCTION_DEPLOYMENT.md) | 生产环境部署清单 | 上线前检查 |
| [ci-cd-setup.md](deployment/ci-cd-setup.md) | CI/CD配置指南 | 自动化部署 |
| [server-requirements.md](deployment/server-requirements.md) | 服务器硬件要求 | 环境准备 |
| [2gb-server-optimization.md](deployment/2gb-server-optimization.md) | 低配置服务器优化 | 资源受限环境 |
| [docker-mirror-china.md](deployment/docker-mirror-china.md) | 国内Docker镜像配置 | 国内环境 |
| [android-connect-backend.md](deployment/android-connect-backend.md) | Android连接后端指南 | 网络配置 |

### 2. 开发文档 (`development/`)

开发过程相关文档：

#### 测试相关
- [android-testing-analysis.md](development/android-testing-analysis.md) - Android测试策略分析
- [api-integration-test-plan.md](development/api-integration-test-plan.md) - API集成测试计划
- [api-integration-test-report.md](development/api-integration-test-report.md) - API集成测试报告
- [e2e-integration-test-report.md](development/e2e-integration-test-report.md) - 端到端测试报告
- [ci-cd-testing-scope.md](development/ci-cd-testing-scope.md) - CI/CD测试范围

#### 问题排查
- [database-integration-test-issues.md](development/database-integration-test-issues.md) - 数据库测试问题
- [spring-boot-test-issues.md](development/spring-boot-test-issues.md) - Spring Boot测试问题
- [ci-error-quick-reference.md](development/ci-error-quick-reference.md) - CI错误速查
- [ci-error-troubleshooting-guide.md](development/ci-error-troubleshooting-guide.md) - CI错误排查指南
- [troubleshooting-log.md](development/troubleshooting-log.md) - 问题排查日志
- [problem-solving-guide.md](development/problem-solving-guide.md) - 问题解决指南

#### 架构与设计
- [android-backend-crud-integration-plan.md](development/android-backend-crud-integration-plan.md) - Android后端CRUD集成
- [android-ui-navigation-analysis.md](development/android-ui-navigation-analysis.md) - UI导航分析
- [flyway-migration-analysis.md](development/flyway-migration-analysis.md) - Flyway迁移分析

#### 检查点报告
- [checkpoint-29-task-29-1-report.md](development/checkpoint-29-task-29-1-report.md)
- [checkpoint-29-task-29-2-report.md](development/checkpoint-29-task-29-2-report.md)
- [checkpoint-29-task-29-2-optimized-report.md](development/checkpoint-29-task-29-2-optimized-report.md)
- [checkpoint-29-final-summary.md](development/checkpoint-29-final-summary.md)

### 3. 安全文档 (`security/`)

安全配置和最佳实践：

- [security-configuration.md](security/security-configuration.md) - 安全配置指南
- [environment-key-management.md](security/environment-key-management.md) - 环境密钥管理

### 4. 计划文档 (`plans/`)

项目规划和优化计划：

- [2026-02-13-database-optimization-plan.md](plans/2026-02-13-database-optimization-plan.md) - 数据库优化计划
- [2026-02-14-android-test-coverage-60-percent.md](plans/2026-02-14-android-test-coverage-60-percent.md) - Android测试覆盖率计划

## API文档

### OpenAPI规范

完整的API文档位于 [api/openapi.yaml](api/openapi.yaml)，包含：

- 所有RESTful API端点
- 请求/响应参数定义
- 认证和授权说明
- 错误码定义

### 查看方式

**方式1：Swagger UI**
```bash
# 启动后端服务后访问
http://localhost:8080/swagger-ui.html
```

**方式2：Postman**
- 导入 `api/openapi.yaml` 到 Postman
- 自动生成请求集合

**方式3：在线工具**
- 使用 [Swagger Editor](https://editor.swagger.io/)
- 粘贴 `openapi.yaml` 内容查看

## 常用链接

### 快速参考
- [项目主README](../README.md) - 项目总览
- [快速开始](../QUICK_START.md) - 5分钟启动
- [项目结构](../PROJECT_STRUCTURE.md) - 目录结构
- [项目总结](../PROJECT_SUMMARY.md) - 功能总结

### 子项目文档
- [Android README](../android-app/README.md) - Android应用文档
- [后端README](../backend/README.md) - 后端服务文档
- [脚本README](../scripts/README.md) - 脚本说明

### 开发资源
- [需求文档](../.kiro/specs/user-auth-system/requirements.md)
- [设计文档](../.kiro/specs/user-auth-system/design.md)
- [任务列表](../.kiro/specs/user-auth-system/tasks.md)

## 文档编写规范

### Markdown规范

- 使用标准Markdown语法
- 代码块使用语法高亮（指定语言）
- 使用相对路径链接其他文档
- 表格用于展示结构化数据

### 文档结构

每个文档应包含：
1. **标题** - 清晰的文档标题
2. **概述** - 简要说明文档目的
3. **内容** - 详细的内容说明
4. **示例** - 代码或操作示例
5. **参考** - 相关链接

### 更新维护

- 定期更新以反映最新变更
- 在文档头部标注最后更新时间
- 重大变更时更新版本历史

## 贡献指南

### 如何添加新文档

1. 在适当的目录创建 `.md` 文件
2. 遵循文档编写规范
3. 在本README中添加文档链接和描述
4. 提交Pull Request

### 文档审查清单

- [ ] 内容准确，与代码一致
- [ ] 步骤清晰，可复现
- [ ] 代码示例可运行
- [ ] 链接有效
- [ ] 格式正确

## 文档统计

- **总文档数**: 40+
- **部署文档**: 10+
- **开发文档**: 20+
- **安全文档**: 2
- **计划文档**: 3

## 获取帮助

如果在阅读文档时有疑问：

1. 查看 [问题排查日志](development/troubleshooting-log.md)
2. 参考 [问题解决指南](development/problem-solving-guide.md)
3. 提交 Issue 到项目仓库
4. 联系开发团队

---

**最后更新**: 2026年2月

**维护者**: 开发团队
