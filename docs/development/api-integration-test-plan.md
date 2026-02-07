# 前后端接口互联测试方案

## 概述

本文档描述了在任务32（端到端集成测试）之前，验证Android前端与Spring Boot后端API接口互联性的完整测试方案。

**目标**：确保前后端API契约一致、网络连通正常、数据传输正确，为后续的端到端集成测试奠定基础。

**适用阶段**：任务32之前的预备阶段

---

## 1. API现状分析

### 1.1 后端API端点

| 模块 | 端点 | 方法 | 认证 | 说明 |
|------|------|------|------|------|
| **认证** | `/api/auth/register` | POST | 否 | 用户注册 |
| **认证** | `/api/auth/login` | POST | 否 | 用户登录 |
| **水果查询** | `/api/fruit/query` | GET | 否 | 查询水果营养/风味 |
| **评价模型** | `/api/evaluation-models` | GET | 是 | 获取所有模型 |
| **评价模型** | `/api/evaluation-models` | POST | 是(Admin) | 创建模型 |
| **评价模型** | `/api/evaluation-models/{id}` | GET | 是 | 获取模型详情 |
| **评价模型** | `/api/evaluation-models/{id}` | PUT | 是(Admin) | 更新模型 |
| **评价模型** | `/api/evaluation-models/{id}` | DELETE | 是(Admin) | 删除模型 |
| **赛事** | `/api/competitions` | GET | 是 | 获取赛事列表 |
| **赛事** | `/api/competitions` | POST | 是(Admin) | 创建赛事 |
| **赛事** | `/api/competitions/{id}` | GET | 是 | 获取赛事详情 |
| **赛事** | `/api/competitions/{id}` | PUT | 是(Admin) | 更新赛事 |
| **赛事** | `/api/competitions/{id}` | DELETE | 是(Admin) | 删除赛事 |
| **赛事** | `/api/competitions/{id}/entries` | POST | 是(Admin) | 提交参赛作品 |
| **评分** | `/api/ratings` | POST | 是 | 提交评分 |
| **评分** | `/api/ratings/{competitionId}` | GET | 是 | 获取评分数据 |
| **导出** | `/api/competitions/{id}/export` | GET | 是(Admin) | CSV导出 |
| **健康** | `/api/health` | GET | 否 | 健康检查 |
| **监控** | `/actuator/health` | GET | 否 | 详细健康检查 |

### 1.2 Android端API配置

**Base URL**: `http://10.0.2.2:8080/api/`
- 使用 `10.0.2.2` 访问Android模拟器宿主机localhost
- 生产环境应配置为实际服务器地址

**API服务接口**:
- `AuthApiService` - 认证相关
- `FruitApiService` - 水果查询
- `EvaluationApiService` - 赛事评价

**网络配置**:
```kotlin
// NetworkModule.kt
private const val BASE_URL = "http://10.0.2.2:8080/api/"
private const val CONNECT_TIMEOUT = 30L
private const val READ_TIMEOUT = 30L
private const val WRITE_TIMEOUT = 30L
```

### 1.3 DTO兼容性分析

| DTO | 后端字段 | Android字段 | 状态 |
|-----|---------|------------|------|
| **RegisterRequest** | username, password | username, password | ✅ 一致 |
| **LoginRequest** | username, password | username, password | ✅ 一致 |
| **AuthResponse** | token, username, roles | token, username, roles | ✅ 一致 |
| **UserResponse** | id, username, roles | id, username, roles | ✅ 一致 |
| **ModelRequest** | name, parameters | name, parameters | ✅ 一致 |
| **ModelResponse** | id, name, parameters | id, name, parameters | ✅ 一致 |
| **CompetitionRequest** | name, modelId, deadline, judgeIds | name, modelId, deadline, judgeIds | ✅ 一致 |
| **CompetitionResponse** | id, name, modelId, creatorId, deadline, status | id, name, modelId, creatorId, deadline, status | ✅ 一致 |
| **RatingRequest** | competitionId, entryId, scores, note | competitionId, entryId, scores, note | ✅ 一致 |
| **RatingResponse** | id, competitionId, entryId, judgeId, scores, note | id, competitionId, entryId, judgeId, scores, note | ✅ 一致 |
| **FruitQueryResponse** | data, fruitName, queryType | 待确认 | ⚠️ 需验证 |

**结论**：所有主要DTO字段已对齐，可以直接进行互联测试。

---

## 2. 测试阶段

### 阶段1：环境准备与启动验证

#### 步骤1.1：启动后端服务

**方式1：使用Docker Compose（推荐）**
```bash
# 在项目根目录执行
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose -f docker-compose.dev.yml ps

# 查看后端日志
docker-compose -f docker-compose.dev.yml logs -f backend
```

**方式2：本地Maven运行**
```bash
cd backend
mvn clean install -DskipTests
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**验证检查点**：
- [ ] MySQL容器启动成功（端口3306）
- [ ] Redis容器启动成功（端口6379）
- [ ] 后端服务启动成功（端口8080）
- [ ] 控制台无错误日志
- [ ] Flyway迁移执行成功

#### 步骤1.2：验证后端健康状态

```bash
# 基础健康检查
curl http://localhost:8080/api/health

# 预期响应：
# {"status":"UP"}

# 详细健康检查
curl http://localhost:8080/actuator/health

# 预期响应包含：
# - status: "UP"
# - db: {"status":"UP"}
# - redis: {"status":"UP"}
```

**验证检查点**：
- [ ] 返回HTTP 200
- [ ] status字段为"UP"
- [ ] 数据库组件状态为UP
- [ ] Redis组件状态为UP

---

### 阶段2：API契约验证

#### 步骤2.1：无认证端点测试

**测试1：健康检查端点**
```bash
curl -v http://localhost:8080/api/health

# 验证点：
# - HTTP 200 OK
# - Content-Type: application/json
# - 响应体包含 {"status":"UP"}
```

**测试2：水果查询端点**
```bash
# 测试芒果营养查询
curl -v "http://localhost:8080/api/fruit/query?type=nutrition&fruit=mango"

# 预期响应结构：
# {
#   "fruitName": "芒果",
#   "queryType": "nutrition",
#   "data": [
#     {"label": "维生素C", "value": "36.4", "unit": "mg/100g"},
#     ...
#   ]
# }

# 测试香蕉风味查询
curl -v "http://localhost:8080/api/fruit/query?type=flavor&fruit=banana"

# 测试不存在的水果
curl -v "http://localhost:8080/api/fruit/query?type=nutrition&fruit=apple"
# 预期：HTTP 404 Not Found
```

**验证检查点**：
- [ ] 有效查询返回HTTP 200和JSON数据
- [ ] 缺少参数返回HTTP 400
- [ ] 不存在的水果返回HTTP 404
- [ ] 响应JSON格式正确

#### 步骤2.2：认证端点测试（完整流程）

**测试3：用户注册**
```bash
# 注册新用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234!"
  }'

# 预期响应：
# HTTP 201 Created
# {
#   "id": 1,
#   "username": "testuser",
#   "roles": ["USER"]
# }

# 测试重复注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test1234!"}'

# 预期：HTTP 409 Conflict
```

**测试4：用户登录**
```bash
# 使用刚注册的用户登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234!"
  }'

# 预期响应：
# HTTP 200 OK
# {
#   "token": "eyJhbGciOiJIUzI1NiJ9...",
#   "username": "testuser",
#   "roles": ["USER"]
# }

# 提取Token（保存用于后续测试）
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test1234!"}' | \
  grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "获取到的Token: $TOKEN"
```

**测试5：使用Token访问受保护端点**
```bash
# 获取评价模型列表
curl -v http://localhost:8080/api/evaluation-models \
  -H "Authorization: Bearer $TOKEN"

# 预期响应：
# HTTP 200 OK
# 返回评价模型列表（可能包含预设的芒果模型）

# 不带Token访问
curl -v http://localhost:8080/api/evaluation-models

# 预期：HTTP 401 Unauthorized
```

**验证检查点**：
- [ ] 注册成功返回201和用户数据
- [ ] 重复用户名返回409
- [ ] 登录成功返回200和JWT令牌
- [ ] 错误密码返回401
- [ ] 受保护端点需要Token
- [ ] 有效Token可访问受保护资源

---

### 阶段3：Android端API调用验证

#### 步骤3.1：网络连通性测试

**测试6：Android模拟器网络连通**
```bash
# 进入Android模拟器shell
adb shell

# 测试到后端的连通性
ping -c 3 10.0.2.2

# 测试端口连通
curl http://10.0.2.2:8080/api/health
```

#### 步骤3.2：Android单元测试

在Android项目中运行以下测试验证API客户端：

```bash
cd android-app

# 运行所有单元测试
./gradlew testDebugUnitTest

# 仅运行API相关测试
./gradlew testDebugUnitTest --tests="*ApiServiceTest"
./gradlew testDebugUnitTest --tests="*RepositoryTest"
```

**关键测试类**：
- `AuthRepositoryTest` - 验证认证Repository
- `AuthInterceptorTest` - 验证JWT拦截器
- `ApiServiceTest` - 验证API服务配置

**验证检查点**：
- [ ] 所有单元测试通过
- [ ] MockWebServer测试正常工作
- [ ] Repository层逻辑正确
- [ ] 拦截器正确添加Authorization头

---

### 阶段4：端到端集成预测试

#### 步骤4.1：完整用户流程测试

**场景1：注册 → 登录 → 查询水果**
```bash
#!/bin/bash
# 保存为 test-auth-flow.sh

BASE_URL="http://localhost:8080/api"
USERNAME="apitest_$(date +%s)"
PASSWORD="Test1234!"

echo "=== 测试用户: $USERNAME ==="

# 1. 注册
echo "[1/4] 注册用户..."
REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")
echo "注册响应: $REGISTER_RESPONSE"

# 2. 登录
echo "[2/4] 用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$USERNAME\",\"password\":\"$PASSWORD\"}")
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "获取Token: ${TOKEN:0:50}..."

# 3. 查询评价模型
echo "[3/4] 查询评价模型..."
curl -s "$BASE_URL/evaluation-models" \
  -H "Authorization: Bearer $TOKEN" | head -c 200
echo ""

# 4. 查询水果数据
echo "[4/4] 查询水果数据..."
curl -s "$BASE_URL/fruit/query?type=nutrition&fruit=mango" | head -c 200
echo ""

echo "=== 测试完成 ==="
```

**场景2：管理员流程测试**
```bash
#!/bin/bash
# 需要先创建管理员用户

BASE_URL="http://localhost:8080/api"
ADMIN_USER="admin"
ADMIN_PASS="admin123"

# 1. 管理员登录
echo "[1/3] 管理员登录..."
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}")
ADMIN_TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)

# 2. 创建评价模型
echo "[2/3] 创建评价模型..."
MODEL_RESPONSE=$(curl -s -X POST "$BASE_URL/evaluation-models" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -d '{
    "name": "测试模型",
    "parameters": [
      {"name": "甜度", "weight": 40},
      {"name": "酸度", "weight": 30},
      {"name": "香味", "weight": 30}
    ]
  }')
echo "模型创建响应: $MODEL_RESPONSE"

# 3. 创建赛事
echo "[3/3] 创建赛事..."
# 获取模型ID
MODEL_ID=$(echo $MODEL_RESPONSE | grep -o '"id":[0-9]*' | head -1 | cut -d':' -f2)
echo "使用模型ID: $MODEL_ID"
```

---

## 3. 自动化测试脚本

### 3.1 快速测试脚本

创建 `scripts/api-integration-test.sh`：

```bash
#!/bin/bash
set -e

# 配置
BASE_URL="http://localhost:8080/api"
TEST_USER="test_$(date +%s)"
TEST_PASS="Test1234!"
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

passed=0
failed=0

# 测试函数
test_api() {
    local name=$1
    local method=$2
    local endpoint=$3
    local expected_code=$4
    local data=$5
    local token=$6
    
    local curl_cmd="curl -s -o /dev/null -w '%{http_code}' -X $method"
    
    if [ -n "$data" ]; then
        curl_cmd="$curl_cmd -H 'Content-Type: application/json' -d '$data'"
    fi
    
    if [ -n "$token" ]; then
        curl_cmd="$curl_cmd -H 'Authorization: Bearer $token'"
    fi
    
    curl_cmd="$curl_cmd $BASE_URL$endpoint"
    
    response_code=$(eval $curl_cmd)
    
    if [ "$response_code" = "$expected_code" ]; then
        echo -e "${GREEN}✓${NC} $name ($response_code)"
        ((passed++))
    else
        echo -e "${RED}✗${NC} $name (expected $expected_code, got $response_code)"
        ((failed++))
    fi
}

echo "=== 前后端API互联测试 ==="
echo "测试用户: $TEST_USER"
echo ""

# 阶段1：基础连通性测试
echo "[阶段1] 基础连通性测试"
test_api "健康检查" "GET" "/health" "200"
test_api "水果查询-芒果营养" "GET" "/fruit/query?type=nutrition&fruit=mango" "200"
test_api "水果查询-不存在水果" "GET" "/fruit/query?type=nutrition&fruit=apple" "404"
echo ""

# 阶段2：认证测试
echo "[阶段2] 认证API测试"
test_api "用户注册" "POST" "/auth/register" "201" "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}"
test_api "重复注册(应失败)" "POST" "/auth/register" "409" "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}"
test_api "用户登录" "POST" "/auth/login" "200" "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}"
test_api "错误密码登录" "POST" "/auth/login" "401" "{\"username\":\"$TEST_USER\",\"password\":\"wrongpassword\"}"
echo ""

# 获取Token
TOKEN=$(curl -s -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"$TEST_USER\",\"password\":\"$TEST_PASS\"}" | \
  grep -o '"token":"[^"]*' | cut -d'"' -f4)

# 阶段3：受保护端点测试
echo "[阶段3] 受保护端点测试"
test_api "无Token访问(应失败)" "GET" "/evaluation-models" "401"
test_api "带Token访问" "GET" "/evaluation-models" "200" "" "$TOKEN"
echo ""

# 测试报告
echo "=== 测试报告 ==="
echo -e "通过: ${GREEN}$passed${NC}"
echo -e "失败: ${RED}$failed${NC}"
echo "总计: $((passed + failed))"

if [ $failed -eq 0 ]; then
    echo -e "${GREEN}所有测试通过！✓${NC}"
    exit 0
else
    echo -e "${RED}存在失败的测试！✗${NC}"
    exit 1
fi
```

### 3.2 Windows批处理脚本

创建 `scripts/api-integration-test.bat`：

```batch
@echo off
setlocal EnableDelayedExpansion

set BASE_URL=http://localhost:8080/api
set TEST_USER=test_%RANDOM%
set TEST_PASS=Test1234!
set passed=0
set failed=0

echo === 前后端API互联测试 ===
echo 测试用户: %TEST_USER%
echo.

:: 测试函数
:TestApi
set name=%~1
set method=%~2
set endpoint=%~3
set expected=%~4
set data=%~5
set token=%~6

if "!data!"=="" (
    if "!token!"=="" (
        for /f %%a in ('curl -s -o nul -w "%%{http_code}" -X !method! %BASE_URL%%endpoint%') do set response=%%a
    ) else (
        for /f %%a in ('curl -s -o nul -w "%%{http_code}" -X !method! -H "Authorization: Bearer !token!" %BASE_URL%%endpoint%') do set response=%%a
    )
) else (
    if "!token!"=="" (
        for /f %%a in ('curl -s -o nul -w "%%{http_code}" -X !method! -H "Content-Type: application/json" -d "!data!" %BASE_URL%%endpoint%') do set response=%%a
    ) else (
        for /f %%a in ('curl -s -o nul -w "%%{http_code}" -X !method! -H "Content-Type: application/json" -H "Authorization: Bearer !token!" -d "!data!" %BASE_URL%%endpoint%') do set response=%%a
    )
)

if "!response!"=="!expected!" (
    echo [OK] !name! (!response!)
    set /a passed+=1
) else (
    echo [FAIL] !name! (expected !expected!, got !response!)
    set /a failed+=1
)
goto :eof

:: 阶段1：基础测试
echo [阶段1] 基础连通性测试
call :TestApi "健康检查" "GET" "/health" "200"
call :TestApi "水果查询-芒果" "GET" "/fruit/query?type=nutrition&fruit=mango" "200"
call :TestApi "水果查询-不存在" "GET" "/fruit/query?type=nutrition&fruit=apple" "404"
echo.

:: 阶段2：认证测试
echo [阶段2] 认证API测试
call :TestApi "用户注册" "POST" "/auth/register" "201" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}"
call :TestApi "重复注册" "POST" "/auth/register" "409" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}"
call :TestApi "用户登录" "POST" "/auth/login" "200" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}"
echo.

:: 获取Token
for /f "tokens=*" %%a in ('curl -s -X POST %BASE_URL%/auth/login -H "Content-Type: application/json" -d "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}" ^| findstr "token"') do (
    set login_response=%%a
)
:: 注意：Windows下提取JSON需要更复杂的处理

echo.
echo === 测试报告 ===
echo 通过: %passed%
echo 失败: %failed%
echo 总计: %passed% + %failed%

if %failed%==0 (
    echo 所有测试通过！
    exit /b 0
) else (
    echo 存在失败的测试！
    exit /b 1
)
```

---

## 4. 常见问题排查

### 4.1 连接问题

| 问题现象 | 可能原因 | 解决方案 |
|---------|---------|---------|
| `Connection refused` | 后端未启动 | 检查docker-compose状态 |
| `Connection timeout` | 防火墙阻止 | 检查端口8080是否开放 |
| `Unknown host` | DNS解析失败 | 使用IP地址替代localhost |
| Android无法连接 | 使用localhost而非10.0.2.2 | 修改NetworkModule中的Base URL |

### 4.2 API响应问题

| 问题现象 | 可能原因 | 解决方案 |
|---------|---------|---------|
| HTTP 404 | URL路径错误 | 核对端点路径，确保以/api开头 |
| HTTP 401 | JWT令牌无效 | 检查Token格式，确保使用Bearer前缀 |
| HTTP 403 | 权限不足 | 确认用户角色是否满足要求 |
| HTTP 415 | Content-Type错误 | 添加 `-H "Content-Type: application/json"` |
| HTTP 500 | 后端错误 | 查看后端日志定位问题 |

### 4.3 DTO不匹配问题

| 问题现象 | 可能原因 | 解决方案 |
|---------|---------|---------|
| JSON解析失败 | 字段名不匹配 | 对比前后端DTO字段名 |
| 数据类型错误 | 类型不匹配 | 检查Long/Integer等数值类型 |
| 缺少必填字段 | 验证失败 | 确保发送所有后端要求的字段 |

### 4.4 调试命令

```bash
# 查看后端详细日志
docker-compose -f docker-compose.dev.yml logs -f backend --tail=100

# 测试MySQL连接
docker-compose -f docker-compose.dev.yml exec mysql mysql -u userauth -p -e "SHOW DATABASES;"

# 检查端口占用
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Android模拟器网络测试
adb shell ping -c 3 10.0.2.2
adb shell curl http://10.0.2.2:8080/api/health

# 抓包分析
adb shell tcpdump -i any -w /sdcard/capture.pcap
adb pull /sdcard/capture.pcap .
# 使用Wireshark分析capture.pcap
```

---

## 5. 测试检查清单

在完成接口互联测试后，请确认以下检查项：

### 5.1 基础环境 ✅
- [ ] 后端服务可正常启动
- [ ] 数据库连接正常
- [ ] Redis连接正常
- [ ] 健康检查端点返回200
- [ ] Android项目可正常编译

### 5.2 API连通性 ✅
- [ ] 无认证端点可访问（水果查询）
- [ ] 认证端点可访问（注册/登录）
- [ ] 受保护端点需要JWT令牌
- [ ] JWT令牌验证工作正常
- [ ] CORS配置正确（跨域请求正常）

### 5.3 DTO兼容性 ✅
- [ ] 所有请求DTO字段名一致
- [ ] 所有响应DTO字段名一致
- [ ] 数据类型匹配
- [ ] 必填字段验证通过
- [ ] JSON序列化/反序列化正常

### 5.4 Android集成 ✅
- [ ] Base URL配置正确
- [ ] 网络权限已声明（AndroidManifest.xml）
- [ ] 拦截器正确添加Authorization头
- [ ] 超时设置合理
- [ ] 错误处理机制完善
- [ ] 单元测试全部通过

### 5.5 端到端流程 ✅
- [ ] 注册流程完整测试
- [ ] 登录流程完整测试
- [ ] Token存储和复用正常
- [ ] 水果查询功能正常
- [ ] 评价模型查看正常
- [ ] 所有API响应时间 < 5秒

---

## 6. 下一步

完成以上所有测试后，可以安全地进入**任务32：端到端集成测试**，测试内容将包括：

1. 完整的用户注册 → 登录 → 主界面流程
2. 管理员创建评价模型 → 创建赛事 → 分配评委流程
3. 评委评分 → 数据展示 → CSV导出流程
4. 多用户并发场景测试
5. 错误恢复和边界条件测试

---

## 附录

### A. 参考文档

- [后端API文档](../api/)
- [Android项目说明](../../android-app/README.md)
- [后端项目说明](../../backend/README.md)
- [Docker开发环境](../development/docker-setup.md)

### B. 相关脚本

- `scripts/dev-start.sh` / `dev-start.bat` - 启动开发环境
- `scripts/api-integration-test.sh` - API集成测试（本方案）
- `android-app/scripts/test-quiet.sh` - Android安静测试

### C. 联系方式

如有问题，请参考项目根目录的 CONTRIBUTING.md 文件。

---

**文档版本**: 1.0  
**最后更新**: 2026-02-05  
**维护者**: 开发团队
