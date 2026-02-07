#!/bin/bash
# =============================================================================
# 前后端API接口互联测试脚本
# 用途：在端到端集成测试之前验证前后端API连通性
# 使用方式：./api-integration-test.sh
# =============================================================================

set -e

# 配置
BASE_URL="http://localhost:8080/api"
TEST_USER="test_$(date +%s)"
TEST_PASS="Test1234!"

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 计数器
passed=0
failed=0

# =============================================================================
# 测试函数
# =============================================================================
test_api() {
    local name=$1
    local method=$2
    local endpoint=$3
    local expected_code=$4
    local data=$5
    local token=$6
    local description=$7
    
    local curl_cmd="curl -s -o /dev/null -w '%{http_code}' -X $method"
    local headers=""
    
    if [ -n "$data" ]; then
        headers="$headers -H 'Content-Type: application/json'"
        curl_cmd="$curl_cmd $headers -d '$data'"
    fi
    
    if [ -n "$token" ]; then
        curl_cmd="$curl_cmd -H 'Authorization: Bearer $token'"
    fi
    
    curl_cmd="$curl_cmd $BASE_URL$endpoint"
    
    response_code=$(eval $curl_cmd)
    
    if [ "$response_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓${NC} $name ($response_code)"
        if [ -n "$description" ]; then
            echo "  $description"
        fi
        ((passed++))
        return 0
    else
        echo -e "${RED}✗${NC} $name"
        echo "  预期: $expected_code, 实际: $response_code"
        if [ -n "$description" ]; then
            echo "  $description"
        fi
        ((failed++))
        return 1
    fi
}

# =============================================================================
# 环境检查
# =============================================================================
check_environment() {
    echo "=== 环境检查 ==="
    
    # 检查curl
    if ! command -v curl &> /dev/null; then
        echo -e "${RED}错误: 未安装curl${NC}"
        exit 1
    fi
    
    # 检查后端服务
    echo -n "检查后端服务... "
    if curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/health" | grep -q "200"; then
        echo -e "${GREEN}运行中${NC}"
    else
        echo -e "${RED}未启动${NC}"
        echo "请先启动后端服务:"
        echo "  docker-compose -f docker-compose.dev.yml up -d"
        echo "或:"
        echo "  cd backend && mvn spring-boot:run"
        exit 1
    fi
    
    echo "测试用户: $TEST_USER"
    echo ""
}

# =============================================================================
# 阶段1: 基础连通性测试
# =============================================================================
phase1_basic_connectivity() {
    echo "========================================"
    echo "[阶段1] 基础连通性测试"
    echo "========================================"
    
    test_api "健康检查" "GET" "/health" "200" "" "" "验证后端服务健康状态"
    test_api "详细健康检查" "GET" "/actuator/health" "200" "" "" "验证数据库和Redis连接"
    test_api "水果查询-芒果营养" "GET" "/fruit/query?type=nutrition&fruit=mango" "200" "" "" "查询芒果营养成分"
    test_api "水果查询-香蕉风味" "GET" "/fruit/query?type=flavor&fruit=banana" "200" "" "" "查询香蕉风味特征"
    test_api "水果查询-不存在" "GET" "/fruit/query?type=nutrition&fruit=apple" "404" "" "" "验证不存在的水果返回404"
    test_api "水果查询-缺少参数" "GET" "/fruit/query?type=nutrition" "400" "" "" "验证参数验证"
    
    echo ""
}

# =============================================================================
# 阶段2: 认证API测试
# =============================================================================
phase2_authentication() {
    echo "========================================"
    echo "[阶段2] 认证API测试"
    echo "========================================"
    
    test_api "用户注册" "POST" "/auth/register" "201" \
        "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" \
        "" "创建测试用户"
    
    test_api "重复注册(应失败)" "POST" "/auth/register" "409" \
        "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" \
        "" "验证用户名唯一性"
    
    test_api "用户登录" "POST" "/auth/login" "200" \
        "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" \
        "" "获取JWT令牌"
    
    test_api "错误密码登录" "POST" "/auth/login" "401" \
        "{\"username\":\"$TEST_USER\",\"password\":\"wrongpassword\"}" \
        "" "验证密码检查"
    
    test_api "不存在的用户登录" "POST" "/auth/login" "401" \
        "{\"username\":\"nonexistent_user_12345\",\"password\":\"password\"}" \
        "" "验证用户名存在性"
    
    echo ""
}

# =============================================================================
# 阶段3: 受保护端点测试
# =============================================================================
phase3_protected_endpoints() {
    echo "========================================"
    echo "[阶段3] 受保护端点测试"
    echo "========================================"
    
    # 获取Token
    echo -n "获取JWT令牌... "
    TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" | \
        grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}失败${NC}"
        echo "无法获取JWT令牌，跳过受保护端点测试"
        return 1
    fi
    
    echo -e "${GREEN}成功${NC}"
    echo "Token: ${TOKEN:0:50}..."
    echo ""
    
    # 测试受保护端点
    test_api "无Token访问(应失败)" "GET" "/evaluation-models" "401" \
        "" "" "" "验证JWT认证拦截"
    
    test_api "带Token获取评价模型" "GET" "/evaluation-models" "200" \
        "" "$TOKEN" "获取评价模型列表"
    
    test_api "带Token获取赛事列表" "GET" "/competitions" "200" \
        "" "$TOKEN" "获取赛事列表"
    
    test_api "带Token获取评分数据" "GET" "/ratings/1" "200" \
        "" "$TOKEN" "获取评分数据（赛事1可能不存在但接口应响应）"
    
    echo ""
}

# =============================================================================
# 阶段4: 数据验证测试
# =============================================================================
phase4_data_validation() {
    echo "========================================"
    echo "[阶段4] 数据验证测试"
    echo "========================================"
    
    # 重新获取Token（可能被前面的测试影响）
    TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
        -H "Content-Type: application/json" \
        -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" | \
        grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    # 测试评价模型数据结构
    echo -n "验证评价模型数据结构... "
    MODEL_RESPONSE=$(curl -s "$BASE_URL/evaluation-models" \
        -H "Authorization: Bearer $TOKEN")
    
    if echo "$MODEL_RESPONSE" | grep -q '"id"' && \
       echo "$MODEL_RESPONSE" | grep -q '"name"' && \
       echo "$MODEL_RESPONSE" | grep -q '"parameters"'; then
        echo -e "${GREEN}✓${NC}"
        ((passed++))
    else
        echo -e "${RED}✗${NC} 响应缺少必要字段"
        ((failed++))
    fi
    
    # 测试水果查询数据结构
    echo -n "验证水果查询数据结构... "
    FRUIT_RESPONSE=$(curl -s "$BASE_URL/fruit/query?type=nutrition&fruit=mango")
    
    if echo "$FRUIT_RESPONSE" | grep -q '"fruitName"' && \
       echo "$FRUIT_RESPONSE" | grep -q '"queryType"' && \
       echo "$FRUIT_RESPONSE" | grep -q '"data"'; then
        echo -e "${GREEN}✓${NC}"
        ((passed++))
    else
        echo -e "${RED}✗${NC} 响应缺少必要字段"
        ((failed++))
    fi
    
    echo ""
}

# =============================================================================
# 阶段5: 错误处理测试
# =============================================================================
phase5_error_handling() {
    echo "========================================"
    echo "[阶段5] 错误处理测试"
    echo "========================================"
    
    # 无效JSON格式
    test_api "无效JSON格式" "POST" "/auth/register" "400" \
        "{invalid json}" "" "" "验证JSON解析错误处理"
    
    # 无效查询类型
    test_api "无效水果查询类型" "GET" "/fruit/query?type=invalid&fruit=mango" "400" \
        "" "" "" "验证查询类型验证"
    
    # 访问不存在的端点
    test_api "访问不存在的端点" "GET" "/nonexistent/endpoint" "404" \
        "" "" "" "验证404错误处理"
    
    echo ""
}

# =============================================================================
# 测试报告
# =============================================================================
print_report() {
    echo "========================================"
    echo "           测试报告"
    echo "========================================"
    echo -e "通过: ${GREEN}$passed${NC}"
    echo -e "失败: ${RED}$failed${NC}"
    echo "总计: $((passed + failed))"
    echo ""
    
    if [ $failed -eq 0 ]; then
        echo -e "${GREEN}✓ 所有测试通过！${NC}"
        echo ""
        echo "前后端API接口互联正常，可以继续执行任务32（端到端集成测试）。"
        echo ""
        echo "建议执行："
        echo "1. 在Android模拟器中运行应用，测试实际UI交互"
        echo "2. 执行完整的用户注册→登录→查询流程"
        echo "3. 测试管理员创建评价模型和赛事功能"
        return 0
    else
        echo -e "${RED}✗ 存在失败的测试！${NC}"
        echo ""
        echo "请检查："
        echo "1. 后端服务是否正常运行"
        echo "2. 数据库和Redis连接是否正常"
        echo "3. 查看后端日志定位问题"
        echo ""
        echo "调试命令："
        echo "  docker-compose -f docker-compose.dev.yml logs -f backend"
        return 1
    fi
}

# =============================================================================
# 主程序
# =============================================================================
main() {
    echo ""
    echo "╔════════════════════════════════════════════════════════════╗"
    echo "║          前后端API接口互联测试                              ║"
    echo "║     API Integration Test for User Auth System              ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    
    # 环境检查
    check_environment
    
    # 执行测试阶段
    phase1_basic_connectivity
    phase2_authentication
    phase3_protected_endpoints
    phase4_data_validation
    phase5_error_handling
    
    # 输出报告
    print_report
    
    exit $failed
}

# 运行主程序
main "$@"
