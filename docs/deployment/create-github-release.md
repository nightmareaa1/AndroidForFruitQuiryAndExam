# GitHub Release 创建指南

本文档介绍如何在GitHub上创建Release并上传JAR包文件。

## 方法1: 使用GitHub网页界面（推荐）

### 步骤1: 访问Release页面

1. 打开你的GitHub仓库: https://github.com/nightmareaa1/AndroidForFruitQuiryAndExam
2. 点击右侧的 **"Releases"** 或 **"Create a new release"**

### 步骤2: 创建新Release

1. 点击绿色的 **"Create a new release"** 按钮

### 步骤3: 填写Release信息

| 字段 | 填写内容 | 示例 |
|------|---------|------|
| **Choose a tag** | 版本标签 | 输入 `v1.0.0`，选择 "Create new tag" |
| **Target** | 分支或commit | 选择 `main` 分支 |
| **Release title** | 版本标题 | `v1.0.0 - 首次发布` |
| **Describe this release** | 版本说明 | 见下文模板 |

### 步骤4: 编写Release说明（可选）

```markdown
## v1.0.0 - 首次发布

### 功能特性
- ✅ 用户认证系统
- ✅ 水果查询API
- ✅ 竞赛管理功能
- ✅ 图片上传支持

### 技术栈
- Spring Boot 3.2.1
- MySQL 8.0
- Redis 7

### 部署说明
1. 下载 JAR 文件
2. 运行 `docker-compose -f docker-compose.jar.yml up -d`
3. 访问 http://localhost:8080/actuator/health

### 系统要求
- Docker 20.10+
- Docker Compose 2.0+
- 2GB RAM
```

### 步骤5: 上传JAR文件

1. 点击 **"Attach binaries by dropping them here or selecting them"** 区域
2. 选择你的JAR文件: `backend/target/userauth-backend-1.0.0.jar`
3. 等待上传完成

### 步骤6: 发布Release

1. 选择 **"This is a pre-release"**（如果是测试版）
2. 或选择 **"Set as the latest release"**（如果是正式版）
3. 点击绿色的 **"Publish release"** 按钮

---

## 方法2: 使用GitHub CLI（命令行）

### 安装GitHub CLI

```bash
# Windows (使用winget)
winget install --id GitHub.cli

# macOS
brew install gh

# Linux
curl -fsSL https://cli.github.com/packages/githubcli-archive-keyring.gpg | sudo dd of=/usr/share/keyrings/githubcli-archive-keyring.gpg
sudo apt update
sudo apt install gh
```

### 登录GitHub CLI

```bash
gh auth login
# 选择 HTTPS 或 SSH
# 按提示完成认证
```

### 创建Release

```bash
# 进入你的项目目录
cd /path/to/AndroidForFruitQuiryAndExam

# 创建标签
git tag -a v1.0.0 -m "v1.0.0 - 首次发布"
git push origin v1.0.0

# 创建Release并上传JAR
cd backend
cd ..
gh release create v1.0.0 \
  --title "v1.0.0 - 首次发布" \
  --notes "首次发布，包含用户认证和水果查询功能" \
  backend/target/userauth-backend-1.0.0.jar
```

### 上传文件到现有Release

```bash
# 如果Release已存在，可以追加文件
gh release upload v1.0.0 backend/target/userauth-backend-1.0.0.jar
```

---

## 方法3: 使用GitHub Actions自动发布

### 创建工作流文件

创建 `.github/workflows/release.yml`:

```yaml
name: Create Release

on:
  push:
    tags:
      - 'v*'  # 推送 v 开头的标签时触发

jobs:
  build-and-release:
    runs-on: ubuntu-latest
    
    steps:
    # 1. 检出代码
    - name: Checkout code
      uses: actions/checkout@v3
    
    # 2. 设置JDK
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven
    
    # 3. 构建JAR
    - name: Build with Maven
      run: |
        cd backend
        mvn clean package -DskipTests
    
    # 4. 创建Release
    - name: Create Release
      id: create_release
      uses: actions/create-release@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        tag_name: ${{ github.ref_name }}
        release_name: Release ${{ github.ref_name }}
        body: |
          ## 变更内容
          - 自动构建的JAR包
          
          ## 部署方法
          ```bash
          docker-compose -f docker-compose.jar.yml up -d
          ```
        draft: false
        prerelease: false
    
    # 5. 上传JAR文件
    - name: Upload Release Asset
      uses: actions/upload-release-asset@v1
      env:
        GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      with:
        upload_url: ${{ steps.create_release.outputs.upload_url }}
        asset_path: backend/target/userauth-backend-1.0.0.jar
        asset_name: userauth-backend-1.0.0.jar
        asset_content_type: application/java-archive
```

### 使用方法

```bash
# 1. 创建标签
git tag -a v1.0.0 -m "v1.0.0 release"

# 2. 推送标签到GitHub
git push origin v1.0.0

# 3. GitHub Actions会自动构建并创建Release
```

---

## 方法4: 使用curl命令行

```bash
# 设置变量
TOKEN="ghp_你的_github_token"
OWNER="nightmareaa1"
REPO="AndroidForFruitQuiryAndExam"
TAG="v1.0.0"

# 1. 创建Release
curl -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$OWNER/$REPO/releases \
  -d '{
    "tag_name": "'"$TAG"'",
    "name": "Release '"$TAG"'",
    "body": "首次发布",
    "draft": false,
    "prerelease": false
  }'

# 2. 获取上传URL（从返回的JSON中提取）
# 3. 上传JAR文件
curl -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Content-Type: application/java-archive" \
  --data-binary @backend/target/userauth-backend-1.0.0.jar \
  "https://uploads.github.com/repos/$OWNER/$REPO/releases/{release_id}/assets?name=userauth-backend-1.0.0.jar"
```

---

## 验证Release创建成功

### 方法1: 查看GitHub页面

访问: https://github.com/nightmareaa1/AndroidForFruitQuiryAndExam/releases

应该能看到你创建的Release和上传的JAR文件。

### 方法2: 使用curl验证

```bash
# 检查最新Release
curl -s https://api.github.com/repos/nightmareaa1/AndroidForFruitQuiryAndExam/releases/latest | grep -E '"tag_name"|"name": "userauth'

# 预期输出
# "tag_name": "v1.0.0"
# "name": "userauth-backend-1.0.0.jar"
```

### 方法3: 测试下载脚本

```bash
# 运行下载脚本
./scripts/download-jar.sh -o nightmareaa1 -r AndroidForFruitQuiryAndExam

# 如果成功，会显示
# ✅ JAR包下载成功
# 文件位置: backend/target/userauth-backend-1.0.0.jar
```

---

## 快速命令参考

```bash
# ===== GitHub CLI 方式 =====
# 创建标签并推送
git tag -a v1.0.0 -m "首次发布"
git push origin v1.0.0

# 创建Release并上传文件
gh release create v1.0.0 \
  --title "v1.0.0 - 首次发布" \
  --notes "包含用户认证和水果查询功能" \
  backend/target/userauth-backend-1.0.0.jar

# ===== 查看Release =====
# 浏览器打开
open https://github.com/nightmareaa1/AndroidForFruitQuiryAndExam/releases

# 或命令行查看
gh release view v1.0.0

# ===== 下载Release文件 =====
# 使用脚本
./scripts/download-jar.sh -o nightmareaa1 -r AndroidForFruitQuiryAndExam

# 或直接使用curl
curl -L -o backend/target/userauth-backend-1.0.0.jar \
  https://github.com/nightmareaa1/AndroidForFruitQuiryAndExam/releases/latest/download/userauth-backend-1.0.0.jar
```

---

## 注意事项

1. **JAR文件大小限制**: GitHub Release附件最大2GB
2. **私有仓库**: 需要使用GitHub Token才能下载
3. **标签命名**: 建议使用语义化版本号 (v1.0.0, v1.1.0, v2.0.0)
4. **Release说明**: 写清楚变更内容，方便用户了解更新

---

**现在你可以创建一个Release，然后使用脚本下载JAR包了！** 🚀
