#!/bin/bash

# =============================================================================
# GitHub JAR包下载脚本
# 支持多种方式从GitHub下载后端JAR包
# =============================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置
JAR_DIR="backend/target"
JAR_FILE="userauth-backend-1.0.0.jar"
JAR_PATH="$JAR_DIR/$JAR_FILE"

# GitHub配置（需要根据实际情况修改）
GITHUB_OWNER="your-username"           # GitHub用户名或组织名
GITHUB_REPO="your-repo-name"           # 仓库名
GITHUB_TOKEN=""                        # GitHub Personal Access Token（可选，用于私有仓库）
RELEASE_TAG="latest"                   # 版本标签，如 "v1.0.0" 或 "latest"

# 打印信息
print_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }

# 创建目录
setup_dir() {
    mkdir -p "$JAR_DIR"
}

# 方式1: 从GitHub Releases下载（推荐）
download_from_releases() {
    print_info "方式1: 从GitHub Releases下载..."
    
    # 构建下载URL
    if [ "$RELEASE_TAG" = "latest" ]; then
        DOWNLOAD_URL="https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/latest/download/$JAR_FILE"
    else
        DOWNLOAD_URL="https://github.com/$GITHUB_OWNER/$GITHUB_REPO/releases/download/$RELEASE_TAG/$JAR_FILE"
    fi
    
    print_info "下载URL: $DOWNLOAD_URL"
    
    # 使用curl下载
    if command -v curl &> /dev/null; then
        if [ -n "$GITHUB_TOKEN" ]; then
            curl -L -H "Authorization: token $GITHUB_TOKEN" \
                 -o "$JAR_PATH" \
                 "$DOWNLOAD_URL"
        else
            curl -L -o "$JAR_PATH" "$DOWNLOAD_URL"
        fi
    # 或使用wget下载
    elif command -v wget &> /dev/null; then
        if [ -n "$GITHUB_TOKEN" ]; then
            wget --header="Authorization: token $GITHUB_TOKEN" \
                 -O "$JAR_PATH" \
                 "$DOWNLOAD_URL"
        else
            wget -O "$JAR_PATH" "$DOWNLOAD_URL"
        fi
    else
        print_error "需要安装curl或wget"
        return 1
    fi
    
    if [ $? -eq 0 ] && [ -f "$JAR_PATH" ]; then
        print_success "下载成功: $JAR_PATH"
        ls -lh "$JAR_PATH"
        return 0
    else
        print_error "下载失败"
        return 1
    fi
}

# 方式2: 使用GitHub API获取最新Release
download_via_api() {
    print_info "方式2: 使用GitHub API下载..."
    
    if ! command -v curl &> /dev/null; then
        print_error "需要安装curl"
        return 1
    fi
    
    # API URL
    if [ "$RELEASE_TAG" = "latest" ]; then
        API_URL="https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/latest"
    else
        API_URL="https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/releases/tags/$RELEASE_TAG"
    fi
    
    print_info "API URL: $API_URL"
    
    # 获取下载URL
    if [ -n "$GITHUB_TOKEN" ]; then
        DOWNLOAD_URL=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
                       "$API_URL" | \
                       grep -o '"browser_download_url": "[^"]*'$JAR_FILE'"' | \
                       cut -d'"' -f4)
    else
        DOWNLOAD_URL=$(curl -s "$API_URL" | \
                       grep -o '"browser_download_url": "[^"]*'$JAR_FILE'"' | \
                       cut -d'"' -f4)
    fi
    
    if [ -z "$DOWNLOAD_URL" ]; then
        print_error "未找到JAR文件下载链接"
        print_info "可用的Release文件:"
        if [ -n "$GITHUB_TOKEN" ]; then
            curl -s -H "Authorization: token $GITHUB_TOKEN" "$API_URL" | grep '"browser_download_url"'
        else
            curl -s "$API_URL" | grep '"browser_download_url"'
        fi
        return 1
    fi
    
    print_info "找到下载链接: $DOWNLOAD_URL"
    
    # 下载文件
    curl -L -o "$JAR_PATH" "$DOWNLOAD_URL"
    
    if [ $? -eq 0 ] && [ -f "$JAR_PATH" ]; then
        print_success "下载成功"
        ls -lh "$JAR_PATH"
        return 0
    else
        print_error "下载失败"
        return 1
    fi
}

# 方式3: 使用GitHub CLI (gh)
download_via_gh_cli() {
    print_info "方式3: 使用GitHub CLI下载..."
    
    if ! command -v gh &> /dev/null; then
        print_error "GitHub CLI (gh) 未安装"
        print_info "安装方法: https://cli.github.com/"
        return 1
    fi
    
    # 检查是否已登录
    if ! gh auth status &> /dev/null; then
        print_warning "请先登录GitHub CLI: gh auth login"
        return 1
    fi
    
    # 下载Release文件
    if [ "$RELEASE_TAG" = "latest" ]; then
        gh release download --repo "$GITHUB_OWNER/$GITHUB_REPO" \
                           --pattern "$JAR_FILE" \
                           --dir "$JAR_DIR"
    else
        gh release download "$RELEASE_TAG" \
                           --repo "$GITHUB_OWNER/$GITHUB_REPO" \
                           --pattern "$JAR_FILE" \
                           --dir "$JAR_DIR"
    fi
    
    if [ $? -eq 0 ] && [ -f "$JAR_PATH" ]; then
        print_success "下载成功"
        ls -lh "$JAR_PATH"
        return 0
    else
        print_error "下载失败"
        return 1
    fi
}

# 方式4: 从GitHub Actions Artifact下载（适用于CI构建）
download_from_actions() {
    print_info "方式4: 从GitHub Actions Artifact下载..."
    
    if ! command -v curl &> /dev/null; then
        print_error "需要安装curl"
        return 1
    fi
    
    if [ -z "$GITHUB_TOKEN" ]; then
        print_error "需要GitHub Token来下载Actions Artifact"
        print_info "请设置GITHUB_TOKEN环境变量"
        return 1
    fi
    
    # 获取最新workflow run
    RUN_ID=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
             "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/actions/runs?branch=main&status=success&per_page=1" | \
             grep -o '"id": [0-9]*' | head -1 | cut -d' ' -f2)
    
    if [ -z "$RUN_ID" ]; then
        print_error "未找到成功的Workflow运行记录"
        return 1
    fi
    
    print_info "找到Workflow Run ID: $RUN_ID"
    
    # 获取Artifact下载URL
    ARTIFACT_URL=$(curl -s -H "Authorization: token $GITHUB_TOKEN" \
                   "https://api.github.com/repos/$GITHUB_OWNER/$GITHUB_REPO/actions/runs/$RUN_ID/artifacts" | \
                   grep -o '"archive_download_url": "[^"]*"' | head -1 | cut -d'"' -f4)
    
    if [ -z "$ARTIFACT_URL" ]; then
        print_error "未找到Artifact"
        return 1
    fi
    
    # 下载Artifact (zip格式)
    TEMP_ZIP="$JAR_DIR/temp_artifact.zip"
    curl -L -H "Authorization: token $GITHUB_TOKEN" \
         -o "$TEMP_ZIP" \
         "$ARTIFACT_URL"
    
    # 解压
    if command -v unzip &> /dev/null; then
        unzip -o "$TEMP_ZIP" -d "$JAR_DIR"
        rm "$TEMP_ZIP"
        print_success "下载并解压成功"
        return 0
    else
        print_error "需要安装unzip来解压artifact"
        return 1
    fi
}

# 验证下载的JAR包
verify_jar() {
    print_info "验证JAR包..."
    
    if [ ! -f "$JAR_PATH" ]; then
        print_error "JAR包不存在: $JAR_PATH"
        return 1
    fi
    
    # 检查文件大小
    FILE_SIZE=$(stat -f%z "$JAR_PATH" 2>/dev/null || stat -c%s "$JAR_PATH" 2>/dev/null)
    if [ "$FILE_SIZE" -lt 1000000 ]; then
        print_error "JAR包太小，可能下载不完整 ($FILE_SIZE bytes)"
        return 1
    fi
    
    # 验证JAR格式
    if command -v unzip &> /dev/null; then
        if unzip -t "$JAR_PATH" &> /dev/null; then
            print_success "JAR包验证通过"
            ls -lh "$JAR_PATH"
            return 0
        else
            print_error "JAR包损坏"
            return 1
        fi
    else
        # 简单检查magic number
        if [ "$(xxd -l 4 "$JAR_PATH" | grep -o '504b0304')" = "504b0304" ]; then
            print_success "JAR包验证通过"
            ls -lh "$JAR_PATH"
            return 0
        else
            print_warning "无法验证JAR包格式，但文件存在"
            ls -lh "$JAR_PATH"
            return 0
        fi
    fi
}

# 备份现有JAR包
backup_existing() {
    if [ -f "$JAR_PATH" ]; then
        BACKUP_NAME="${JAR_PATH}.$(date +%Y%m%d_%H%M%S).bak"
        cp "$JAR_PATH" "$BACKUP_NAME"
        print_info "已备份旧版本: $BACKUP_NAME"
    fi
}

# 显示帮助信息
show_help() {
    cat << 'EOF'
用法: ./download-jar.sh [选项] [方法]

方法:
  releases    从GitHub Releases直接下载（默认）
  api         使用GitHub API获取下载链接
  gh          使用GitHub CLI下载
  actions     从GitHub Actions Artifact下载

选项:
  -o, --owner <owner>     GitHub用户名/组织名
  -r, --repo <repo>       仓库名
  -t, --tag <tag>         版本标签 (默认: latest)
  -k, --token <token>     GitHub Personal Access Token
  -b, --backup            备份现有JAR包
  -h, --help              显示帮助信息

环境变量:
  GITHUB_OWNER            GitHub用户名/组织名
  GITHUB_REPO             仓库名
  GITHUB_TOKEN            GitHub Personal Access Token
  RELEASE_TAG             版本标签

示例:
  # 使用默认配置下载最新版本
  ./download-jar.sh

  # 指定仓库和版本
  ./download-jar.sh -o myuser -r myrepo -t v1.0.0 releases

  # 使用GitHub CLI下载
  ./download-jar.sh gh

  # 从Actions下载（需要Token）
  GITHUB_TOKEN=xxx ./download-jar.sh actions

EOF
}

# 解析命令行参数
METHOD="releases"
BACKUP=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -o|--owner)
            GITHUB_OWNER="$2"
            shift 2
            ;;
        -r|--repo)
            GITHUB_REPO="$2"
            shift 2
            ;;
        -t|--tag)
            RELEASE_TAG="$2"
            shift 2
            ;;
        -k|--token)
            GITHUB_TOKEN="$2"
            shift 2
            ;;
        -b|--backup)
            BACKUP=true
            shift
            ;;
        -h|--help)
            show_help
            exit 0
            ;;
        releases|api|gh|actions)
            METHOD="$1"
            shift
            ;;
        *)
            print_error "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
done

# 主函数
main() {
    echo "=========================================="
    echo "🚀 GitHub JAR包下载工具"
    echo "=========================================="
    echo ""
    
    # 检查必要配置
    if [ -z "$GITHUB_OWNER" ] || [ "$GITHUB_OWNER" = "your-username" ]; then
        print_error "请设置GitHub用户名: -o <owner> 或 GITHUB_OWNER环境变量"
        exit 1
    fi
    
    if [ -z "$GITHUB_REPO" ] || [ "$GITHUB_REPO" = "your-repo-name" ]; then
        print_error "请设置仓库名: -r <repo> 或 GITHUB_REPO环境变量"
        exit 1
    fi
    
    print_info "目标仓库: $GITHUB_OWNER/$GITHUB_REPO"
    print_info "版本标签: $RELEASE_TAG"
    print_info "下载方法: $METHOD"
    echo ""
    
    # 创建目录
    setup_dir
    
    # 备份现有JAR
    if [ "$BACKUP" = true ]; then
        backup_existing
    fi
    
    # 根据方法下载
    case $METHOD in
        releases)
            download_from_releases
            ;;
        api)
            download_via_api
            ;;
        gh)
            download_via_gh_cli
            ;;
        actions)
            download_from_actions
            ;;
        *)
            print_error "未知下载方法: $METHOD"
            exit 1
            ;;
    esac
    
    # 验证
    if [ $? -eq 0 ]; then
        echo ""
        verify_jar
        echo ""
        print_success "🎉 JAR包下载完成!"
        echo ""
        echo "文件位置: $JAR_PATH"
        echo ""
        echo "下一步:"
        echo "  docker-compose -f docker-compose.jar.yml up -d --build"
    else
        print_error "❌ 下载失败"
        exit 1
    fi
}

main
