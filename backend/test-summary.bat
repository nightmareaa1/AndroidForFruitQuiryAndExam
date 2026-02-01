@echo off
REM 运行Maven测试并只显示摘要信息

setlocal enabledelayedexpansion

echo 正在运行测试...
echo.

REM 创建临时文件存储完整输出
set TEMP_FILE=%TEMP%\maven_test_output.txt

REM 运行测试并捕获输出
mvn test -Dspring.profiles.active=test > "%TEMP_FILE%" 2>&1

REM 检查构建结果
findstr /C:"BUILD SUCCESS" "%TEMP_FILE%" >nul
if %errorlevel% == 0 (
    echo ✅ 构建成功
) else (
    echo ❌ 构建失败
)

echo.
echo === 测试摘要 ===

REM 显示测试统计
for /f "tokens=*" %%i in ('findstr /C:"Tests run:" "%TEMP_FILE%"') do (
    echo %%i
)

REM 显示失败的测试
echo.
findstr /C:"FAILURE" /C:"ERROR" "%TEMP_FILE%" >nul
if %errorlevel% == 0 (
    echo === 失败详情 ===
    findstr /C:"FAILURE" /C:"ERROR" /C:"Failed tests:" "%TEMP_FILE%"
)

REM 清理临时文件
del "%TEMP_FILE%" 2>nul

echo.
echo 完整日志位于: target\surefire-reports\