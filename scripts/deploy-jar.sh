#!/bin/bash

# =============================================================================
# 后端服务JAR包自动下载部署脚本
# 用于从GitHub Releases或其他源下载预编译JAR包并部署
# =============================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
PROJECT_DIR="/opt/userauth"
JAR_DIR="$PROJECT_DIR/backend/target"
JAR_FILE="userauth-backend-1.0.0.jar"
GITHUB_RELEASE_URL=""  # 如果有GitHub Releases,填写URL
BACKUP_DIR="$PROJECT_DIR/backup"

# 打印带颜色的信息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查依赖
check_dependencies() {
    print_info "检查依赖..."
    
    if ! command -v docker &> /dev/null; then
        print_error "Docker未安装,请先安装Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
        print_error "Docker Compose未安装,请先安装Docker Compose"
        exit 1
    fi
    
    print_success "依赖检查通过"
}

# 创建目录结构
setup_directories() {
    print_info "创建目录结构..."
    mkdir -p "$JAR_DIR"
    mkdir -p "$BACKUP_DIR"
    mkdir -p "$PROJECT_DIR/logs"
    print_success "目录创建完成"
}

# 从GitHub Releases下载JAR包(如果有)
download_from_github() {
    if [ -z "$GITHUB_RELEASE_URL" ]; then
        print_warning "未配置GitHub Releases URL,跳过下载"
        return 1
    fi
    
    print_info "从GitHub Releases下载JAR包..."
    
    # 备份旧版本
    if [ -f "$JAR_DIR/$JAR_FILE" ]; then
        mv "$JAR_DIR/$JAR_FILE" "$BACKUP_DIR/${JAR_FILE}.$(date +%Y%m%d_%H%M%S).bak"
        print_info "已备份旧版本JAR包"
    fi
    
    # 下载新版本
    curl -L -o "$JAR_DIR/$JAR_FILE" "$GITHUB_RELEASE_URL"
    
    if [ $? -eq 0 ]; then
        print_success "JAR包下载成功"
        return 0
    else
        print_error "JAR包下载失败"
        return 1
    fi
}

# 手动上传方式
check_local_jar() {
    print_info "检查本地JAR包..."
    
    if [ ! -f "$JAR_DIR/$JAR_FILE" ]; then
        print_warning "未找到JAR包: $JAR_DIR/$JAR_FILE"
        echo ""
        echo "请选择操作:"
        echo "1. 手动上传JAR包到 $JAR_DIR/"
        echo "2. 从当前目录复制"
        echo "3. 退出"
        echo ""
        read -p "请输入选项 (1-3): " choice
        
        case $choice in
            1)
                print_info "请手动上传JAR包到: $JAR_DIR/$JAR_FILE"
                print_info "上传完成后按回车继续..."
                read
                ;;
            2)
                if [ -f "./$JAR_FILE" ]; then
                    cp "./$JAR_FILE" "$JAR_DIR/"
                    print_success "JAR包已复制"
                else
                    print_error "当前目录未找到 $JAR_FILE"
                    exit 1
                fi
                ;;
            3)
                exit 0
                ;;
            *)
                print_error "无效选项"
                exit 1
                ;;
        esac
    fi
    
    if [ ! -f "$JAR_DIR/$JAR_FILE" ]; then
        print_error "仍然未找到JAR包,请确保文件存在后重试"
        exit 1
    fi
    
    print_success "JAR包检查通过: $JAR_DIR/$JAR_FILE"
    
    # 显示JAR包信息
    JAR_SIZE=$(du -h "$JAR_DIR/$JAR_FILE" | cut -f1)
    JAR_DATE=$(stat -c %y "$JAR_DIR/$JAR_FILE" | cut -d' ' -f1)
    print_info "JAR包大小: $JAR_SIZE"
    print_info "JAR包日期: $JAR_DATE"
}

# 检查.env文件
check_env_file() {
    print_info "检查环境变量配置..."
    
    if [ ! -f "$PROJECT_DIR/.env" ]; then
        print_warning "未找到.env文件,创建默认配置..."
        cat > "$PROJECT_DIR/.env" << 'EOF'
# ============================================
# 数据库配置
# ============================================
DB_ROOT_PASSWORD=YourSecureRootPassword123!
DB_PASSWORD=YourSecureDBPassword456!

# ============================================
# JWT安全配置(必须修改!)
# 生成命令: openssl rand -base64 48
# ============================================
JWT_SECRET=ChangeThisToYourSecureRandomKeyMin32Chars!

# ============================================
# CORS配置
# ============================================
CORS_ALLOWED_ORIGINS=http://localhost:8080,http://10.0.2.2:8080

# ============================================
# 日志级别
# ============================================
LOGGING_LEVEL_ROOT=INFO
EOF
        print_warning "请编辑 $PROJECT_DIR/.env 文件,修改默认配置!"
        echo ""
        read -p "是否现在编辑? (y/n): " edit_now
        if [ "$edit_now" = "y" ]; then
            ${EDITOR:-vi} "$PROJECT_DIR/.env"
        fi
    fi
    
    # 加载环境变量
    source "$PROJECT_DIR/.env"
    
    # 检查关键配置
    if [ "$JWT_SECRET" = "ChangeThisToYourSecureRandomKeyMin32Chars!" ]; then
        print_error "警告: JWT_SECRET使用的是默认值,请务必修改!"
        read -p "是否现在修改? (y/n): " change_jwt
        if [ "$change_jwt" = "y" ]; then
            ${EDITOR:-vi} "$PROJECT_DIR/.env"
            source "$PROJECT_DIR/.env"
        fi
    fi
    
    print_success "环境变量配置检查完成"
}

# 停止旧服务
stop_existing_services() {
    print_info "停止现有服务..."
    cd "$PROJECT_DIR"
    
    # 检查是否有运行中的容器
    if docker ps | grep -q "userauth-"; then
        print_info "发现运行中的服务,正在停止..."
        docker compose -f docker-compose.jar.yml down 2>/dev/null || true
        docker compose -f docker-compose.light.yml down 2>/dev/null || true
        sleep 5
    fi
    
    print_success "旧服务已停止"
}

# 部署服务
deploy_services() {
    print_info "开始部署服务..."
    cd "$PROJECT_DIR"
    
    # 使用docker-compose.jar.yml部署
    if [ -f "docker-compose.jar.yml" ]; then
        print_info "使用JAR包部署配置..."
        docker compose -f docker-compose.jar.yml up -d --build
    else
        print_info "使用标准部署配置..."
        docker compose -f docker-compose.light.yml up -d
    fi
    
    print_success "服务部署完成"
}

# 等待服务启动
wait_for_services() {
    print_info "等待服务启动..."
    
    # 等待MySQL
    print_info "等待MySQL启动..."
    for i in {1..30}; do
        if docker exec userauth-mysql mysqladmin ping -h localhost --silent 2>/dev/null; then
            print_success "MySQL已就绪"
            break
        fi
        sleep 2
        echo -n "."
    done
    
    # 等待Redis
    print_info "等待Redis启动..."
    for i in {1..30}; do
        if docker exec userauth-redis redis-cli ping 2>/dev/null | grep -q "PONG"; then
            print_success "Redis已就绪"
            break
        fi
        sleep 1
        echo -n "."
    done
    
    # 等待Backend
    print_info "等待Backend启动(可能需要60秒)..."
    for i in {1..60}; do
        if curl -s http://localhost:8080/actuator/health 2>/dev/null | grep -q '"status":"UP"'; then
            print_success "Backend已就绪"
            break
        fi
        sleep 2
        echo -n "."
    done
    echo ""
}

# 验证部署
verify_deployment() {
    print_info "验证部署状态..."
    
    # 检查容器状态
    echo ""
    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
    
    # 健康检查
    HEALTH=$(curl -s http://localhost:8080/actuator/health 2>/dev/null)
    if echo "$HEALTH" | grep -q '"status":"UP"'; then
        print_success "✅ 服务健康检查通过"
        echo "健康状态详情:"
        echo "$HEALTH" | python3 -m json.tool 2>/dev/null || echo "$HEALTH"
    else
        print_error "❌ 服务健康检查失败"
        print_info "查看日志: docker logs userauth-backend --tail 50"
        return 1
    fi
    
    # API测试
    print_info "测试API接口..."
    API_RESPONSE=$(curl -s "http://localhost:8080/api/fruit/query?type=nutrition&fruit=mango" 2>/dev/null)
    if [ ! -z "$API_RESPONSE" ]; then
        print_success "✅ API接口响应正常"
    else
        print_warning "⚠️ API接口可能未完全就绪(可稍后手动测试)"
    fi
    
    return 0
}

# 显示部署信息
show_deployment_info() {
    echo ""
    echo "=========================================="
    echo "🎉 部署完成!"
    echo "=========================================="
    echo ""
    echo "📊 服务状态:"
    docker ps --format "table {{.Names}}\t{{.Status}}" | grep userauth
    echo ""
    echo "🔗 访问地址:"
    echo "   - API地址: http://your-server-ip:8080/api"
    echo "   - 健康检查: http://your-server-ip:8080/actuator/health"
    echo ""
    echo "📋 常用命令:"
    echo "   - 查看日志: docker logs userauth-backend -f"
    echo "   - 停止服务: docker compose -f docker-compose.jar.yml down"
    echo "   - 重启服务: docker compose -f docker-compose.jar.yml restart"
    echo "   - 进入容器: docker exec -it userauth-backend sh"
    echo ""
    echo "💾 内存使用:"
    docker stats --no-stream --format "table {{.Name}}\t{{.MemUsage}}" | grep userauth || true
    echo ""
}

# 主函数
main() {
    echo "=========================================="
    echo "🚀 后端服务JAR包部署脚本"
    echo "=========================================="
    echo ""
    
    # 检查是否在项目目录
    if [ ! -f "docker-compose.jar.yml" ] && [ ! -f "docker-compose.light.yml" ]; then
        print_info "创建项目目录: $PROJECT_DIR"
        sudo mkdir -p "$PROJECT_DIR"
        sudo chown $USER:$USER "$PROJECT_DIR"
    fi
    
    check_dependencies
    setup_directories
    
    # 尝试从GitHub下载,如果没有配置则检查本地
    if ! download_from_github; then
        check_local_jar
    fi
    
    check_env_file
    stop_existing_services
    deploy_services
    wait_for_services
    
    if verify_deployment; then
        show_deployment_info
        print_success "部署成功完成!"
    else
        print_error "部署验证失败,请检查日志"
        exit 1
    fi
}

# 处理脚本参数
case "${1:-}" in
    --download|-d)
        # 仅下载模式
        setup_directories
        download_from_github || check_local_jar
        ;;
    --verify|-v)
        # 仅验证模式
        verify_deployment
        ;;
    --help|-h)
        echo "用法: $0 [选项]"
        echo ""
        echo "选项:"
        echo "  --download, -d    仅下载/检查JAR包"
        echo "  --verify, -v      仅验证部署状态"
        echo "  --help, -h        显示帮助信息"
        echo ""
        echo "示例:"
        echo "  $0                完整部署流程"
        echo "  $0 --download     仅准备JAR包"
        echo "  $0 --verify       检查服务状态"
        ;;
    *)
        # 默认:完整部署
        main
        ;;
esac
