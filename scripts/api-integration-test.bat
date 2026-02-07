@echo off
setlocal EnableDelayedExpansion
rem =============================================================================
rem 前后端API接口互联测试脚本 (Windows版本)
rem 用途：在端到端集成测试之前验证前后端API连通性
rem 使用方式：api-integration-test.bat
rem 依赖：需要安装curl (Windows 10 1803+ 已内置)
rem =============================================================================

rem 配置
set BASE_URL=http://localhost:8080/api
set TEST_USER=test_%RANDOM%%RANDOM%
set TEST_PASS=Test1234!

rem 计数器
set passed=0
set failed=0

rem =============================================================================
rem 测试函数
rem =============================================================================
:TestApi
set name=%~1
set method=%~2
set endpoint=%~3
set expected=%~4
set data=%~5
set token=%~6
set description=%~7

set "curl_cmd=curl -s -o nul -w "
set "response="

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
    if not "!description!"=="" echo   !description!
    set /a passed+=1
) else (
    echo [FAIL] !name!
    echo   预期: !expected!, 实际: !response!
    if not "!description!"=="" echo   !description!
    set /a failed+=1
)
goto :eof

rem =============================================================================
rem 环境检查
rem =============================================================================
:CheckEnvironment
echo === 环境检查 ===

rem 检查curl
curl --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未安装curl或不在PATH中
    echo Windows 10 1803+ 已内置curl
    exit /b 1
)

rem 检查后端服务
echo | set /p="检查后端服务... "
curl -s -o nul -w "%%{http_code}" %BASE_URL%/health | findstr "200" >nul
if errorlevel 1 (
    echo [未启动]
    echo 请先启动后端服务:
    echo   docker-compose -f docker-compose.dev.yml up -d
    echo 或:
    echo   cd backend ^&^& mvn spring-boot:run
    exit /b 1
) else (
    echo [运行中]
)

echo 测试用户: %TEST_USER%
echo.
goto :eof

rem =============================================================================
rem 阶段1: 基础连通性测试
rem =============================================================================
:Phase1
echo ========================================
echo [阶段1] 基础连通性测试
echo ========================================

call :TestApi "健康检查" "GET" "/health" "200" "" "" "验证后端服务健康状态"
call :TestApi "详细健康检查" "GET" "/actuator/health" "200" "" "" "验证数据库和Redis连接"
call :TestApi "水果查询-芒果营养" "GET" "/fruit/query?type=nutrition&fruit=mango" "200" "" "" "查询芒果营养成分"
call :TestApi "水果查询-香蕉风味" "GET" "/fruit/query?type=flavor&fruit=banana" "200" "" "" "查询香蕉风味特征"
call :TestApi "水果查询-不存在" "GET" "/fruit/query?type=nutrition&fruit=apple" "404" "" "" "验证不存在的水果返回404"
call :TestApi "水果查询-缺少参数" "GET" "/fruit/query?type=nutrition" "400" "" "" "验证参数验证"

echo.
goto :eof

rem =============================================================================
rem 阶段2: 认证API测试
rem =============================================================================
:Phase2
echo ========================================
echo [阶段2] 认证API测试
echo ========================================

call :TestApi "用户注册" "POST" "/auth/register" "201" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}" "" "创建测试用户"

call :TestApi "重复注册(应失败)" "POST" "/auth/register" "409" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}" "" "验证用户名唯一性"

call :TestApi "用户登录" "POST" "/auth/login" "200" "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}" "" "获取JWT令牌"

call :TestApi "错误密码登录" "POST" "/auth/login" "401" "{\"username\":\"%TEST_USER%\",\"password\":\"wrongpassword\"}" "" "验证密码检查"

call :TestApi "不存在的用户登录" "POST" "/auth/login" "401" "{\"username\":\"nonexistent_user_12345\",\"password\":\"password\"}" "" "验证用户名存在性"

echo.
goto :eof

rem =============================================================================
rem 阶段3: 受保护端点测试
rem =============================================================================
:Phase3
echo ========================================
echo [阶段3] 受保护端点测试
echo ========================================

rem 获取Token
echo | set /p="获取JWT令牌... "

rem 登录并提取token（简化处理，Windows批处理解析JSON较复杂）
set "LOGIN_RESPONSE="
for /f "delims=" %%a in ('curl -s -X POST %BASE_URL%/auth/login -H "Content-Type: application/json" -d "{\"username\":\"%TEST_USER%\",\"password\":\"%TEST_PASS%\"}"') do set LOGIN_RESPONSE=%%a

rem 简单的token提取（假设格式固定）
set TOKEN=!LOGIN_RESPONSE:"token":"="!
for /f "delims=" %%a in ("!TOKEN!") do (
    set TOKEN=%%a
    set TOKEN=!TOKEN:"=!
    set TOKEN=!TOKEN:}=!
    set TOKEN=!TOKEN:{=!
)

if "!TOKEN!"=="" (
    echo [失败]
    echo 无法获取JWT令牌，跳过受保护端点测试
    goto :eof
)

echo [成功]
echo Token: !TOKEN:~0,50!...
echo.

call :TestApi "无Token访问(应失败)" "GET" "/evaluation-models" "401" "" "" "" "验证JWT认证拦截"

call :TestApi "带Token获取评价模型" "GET" "/evaluation-models" "200" "" "!TOKEN!" "获取评价模型列表"

call :TestApi "带Token获取赛事列表" "GET" "/competitions" "200" "" "!TOKEN!" "获取赛事列表"

call :TestApi "带Token获取评分数据" "GET" "/ratings/1" "200" "" "!TOKEN!" "获取评分数据"

echo.
goto :eof

rem =============================================================================
rem 阶段4: 错误处理测试
rem =============================================================================
:Phase4
echo ========================================
echo [阶段4] 错误处理测试
echo ========================================

call :TestApi "无效JSON格式" "POST" "/auth/register" "400" "{invalid json}" "" "" "验证JSON解析错误处理"

call :TestApi "无效水果查询类型" "GET" "/fruit/query?type=invalid&fruit=mango" "400" "" "" "" "验证查询类型验证"

call :TestApi "访问不存在的端点" "GET" "/nonexistent/endpoint" "404" "" "" "" "验证404错误处理"

echo.
goto :eof

rem =============================================================================
rem 测试报告
rem =============================================================================
:PrintReport
echo ========================================
echo            测试报告
echo ========================================
echo 通过: %passed%
echo 失败: %failed%
set /a total=passed+failed
echo 总计: %total%
echo.

if %failed%==0 (
    echo [OK] 所有测试通过！
    echo.
    echo 前后端API接口互联正常，可以继续执行任务32（端到端集成测试）。
    echo.
    echo 建议执行：
    echo 1. 在Android模拟器中运行应用，测试实际UI交互
    echo 2. 执行完整的用户注册→登录→查询流程
    echo 3. 测试管理员创建评价模型和赛事功能
    exit /b 0
) else (
    echo [FAIL] 存在失败的测试！
    echo.
    echo 请检查：
    echo 1. 后端服务是否正常运行
    echo 2. 数据库和Redis连接是否正常
    echo 3. 查看后端日志定位问题
    echo.
    echo 调试命令：
    echo   docker-compose -f docker-compose.dev.yml logs -f backend
    exit /b 1
)

rem =============================================================================
rem 主程序
rem =============================================================================
:Main
echo.
echo ============================================================
echo           前后端API接口互联测试
echo     API Integration Test for User Auth System
echo ============================================================
echo.

call :CheckEnvironment
if errorlevel 1 exit /b 1

call :Phase1
call :Phase2
call :Phase3
call :Phase4
call :PrintReport

exit /b %failed%

rem 运行主程序
call :Main
