# CI/CD 流水线配置指南

## 概述

本项目使用GitHub Actions实现完整的CI/CD流水线，支持自动化构建、测试、安全扫描和部署。

## 工作流程

### 1. 主CI/CD流水线 (`.github/workflows/ci.yml`)

**触发条件:**
- 推送到 `main` 或 `develop` 分支
- 创建Pull Request到 `main` 或 `develop` 分支

**流水线阶段:**

#### 后端测试 (`backend-test`)
- 使用MySQL 8.0和Redis 7.0服务
- 运行单元测试和集成测试
- 生成代码覆盖率报告
- 构建JAR包

#### Android测试 (`android-test`)
- 运行Android单元测试
- 生成代码覆盖率报告
- 构建Debug APK

#### Android UI测试 (`android-ui-test`)
- 在macOS环境运行（支持硬件加速）
- 使用Android模拟器执行UI测试
- 缓存AVD以提高性能

#### 安全扫描 (`security-scan`)
- Trivy漏洞扫描
- CodeQL静态代码分析
- 结果上传到GitHub Security标签页

#### 代码质量检查 (`code-quality`)
- SonarCloud代码质量分析
- 代码覆盖率检查
- 技术债务评估

#### 构建镜像 (`build-images`)
- 仅在main分支推送时执行
- 构建并推送Docker镜像到GitHub Container Registry
- 使用语义化版本标签

#### 部署测试环境 (`deploy-test`)
- 自动部署到测试环境
- 运行冒烟测试

#### 部署生产环境 (`deploy-prod`)
- 需要手动审批
- 部署到生产环境
- 运行生产健康检查

### 2. Pull Request检查 (`.github/workflows/pr-check.yml`)

**快速验证:**
- 编译检查
- 快速单元测试
- 代码风格检查

### 3. 发布流程 (`.github/workflows/release.yml`)

**触发条件:** 推送版本标签 (如 `v1.0.0`)

**发布内容:**
- 创建GitHub Release
- 上传后端JAR包
- 上传Android APK
- 推送生产Docker镜像

## 环境配置

### GitHub Secrets

需要在GitHub仓库设置中配置以下Secrets:

```bash
# SonarCloud集成
SONAR_TOKEN=your_sonar_token

# 生产部署（如果需要）
PROD_DEPLOY_KEY=your_production_deploy_key
PROD_SERVER_HOST=your_production_server
PROD_SERVER_USER=your_production_user

# Android签名（如果需要）
ANDROID_KEYSTORE=base64_encoded_keystore
ANDROID_KEYSTORE_PASSWORD=keystore_password
ANDROID_KEY_ALIAS=key_alias
ANDROID_KEY_PASSWORD=key_password
```

### 环境保护规则

在GitHub仓库设置中配置环境保护规则:

**test环境:**
- 无需审批
- 自动部署

**production环境:**
- 需要审批者批准
- 仅允许main分支部署
- 部署前等待时间: 5分钟

## 代码质量门禁

### 覆盖率要求
- 后端代码覆盖率: ≥80%
- Android代码覆盖率: ≥70%
- 新增代码覆盖率: ≥90%

### 质量门禁
- 无阻塞性安全漏洞
- 无重复代码超过3%
- 无代码异味超过A级
- 所有测试必须通过

## 依赖管理

### Dependabot配置
- 每周检查依赖更新
- 自动创建PR更新依赖
- 分别管理Maven、Gradle、Docker、GitHub Actions依赖

### 安全扫描
- Trivy容器镜像扫描
- CodeQL静态代码分析
- 依赖漏洞扫描
- 自动安全更新

## 性能优化

### 缓存策略
- Maven依赖缓存
- Gradle依赖缓存
- SonarCloud包缓存
- Android AVD缓存

### 并行执行
- 后端和Android测试并行执行
- 多个测试套件并行运行
- 独立的安全扫描和代码质量检查

## 故障排除

### 常见问题

**1. MySQL连接超时**
```yaml
# 增加健康检查重试次数
options: >-
  --health-cmd="mysqladmin ping -h localhost"
  --health-interval=10s
  --health-timeout=5s
  --health-retries=10
```

**2. Android模拟器启动失败**
```yaml
# 使用macOS runner并启用硬件加速
runs-on: macos-latest
emulator-options: -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim
```

**3. 代码覆盖率上传失败**
```yaml
# 确保测试报告路径正确
- name: Upload coverage reports
  uses: codecov/codecov-action@v3
  with:
    file: ./backend/target/site/jacoco/jacoco.xml
    fail_ci_if_error: false
```

### 调试技巧

**查看详细日志:**
```bash
# 在workflow中添加调试步骤
- name: Debug info
  run: |
    echo "Runner OS: ${{ runner.os }}"
    echo "GitHub ref: ${{ github.ref }}"
    echo "Event name: ${{ github.event_name }}"
    ls -la
```

**本地测试GitHub Actions:**
```bash
# 使用act工具本地运行
npm install -g @github/act
act -j backend-test
```

## 监控和告警

### 流水线监控
- 构建时间趋势
- 测试成功率
- 部署频率
- 失败率统计

### 告警配置
- 构建失败通知
- 安全漏洞告警
- 代码质量下降提醒
- 部署状态通知

## 最佳实践

1. **小而频繁的提交**: 避免大型PR，便于快速反馈
2. **测试优先**: 确保所有新功能都有对应测试
3. **安全第一**: 定期更新依赖，修复安全漏洞
4. **监控部署**: 部署后及时检查应用健康状态
5. **文档更新**: 保持CI/CD文档与实际配置同步