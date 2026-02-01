@echo off
REM CI失败自动诊断脚本
REM 用于快速收集和分析构建失败信息

setlocal enabledelayedexpansion

echo ========================================
echo CI 失败自动诊断工具
echo ========================================
echo.

set TIMESTAMP=%date:~0,4%-%date:~5,2%-%date:~8,2%_%time:~0,2%-%time:~3,2%-%time:~6,2%
set TIMESTAMP=%TIMESTAMP: =0%
set REPORT_DIR=ci-diagnosis-%TIMESTAMP%

echo 创建诊断报告目录: %REPORT_DIR%
mkdir "%REPORT_DIR%" 2>nul

echo.
echo [1/7] 收集系统信息...
echo === 系统信息 === > "%REPORT_DIR%\system-info.txt"
echo 时间: %date% %time% >> "%REPORT_DIR%\system-info.txt"
java -version >> "%REPORT_DIR%\system-info.txt" 2>&1
mvn -version >> "%REPORT_DIR%\system-info.txt" 2>&1
docker --version >> "%REPORT_DIR%\system-info.txt" 2>&1

echo [2/7] 检查环境变量...
echo === 环境变量 === > "%REPORT_DIR%\environment.txt"
echo JAVA_HOME=%JAVA_HOME% >> "%REPORT_DIR%\environment.txt"
echo MAVEN_HOME=%MAVEN_HOME% >> "%REPORT_DIR%\environment.txt"
echo PATH=%PATH% >> "%REPORT_DIR%\environment.txt"

echo [3/7] 分析Maven依赖...
cd backend
mvn dependency:tree > "..\%REPORT_DIR%\dependency-tree.txt" 2>&1
mvn dependency:analyze > "..\%REPORT_DIR%\dependency-analysis.txt" 2>&1

echo [4/7] 收集最近的测试报告...
if exist "target\surefire-reports" (
    xcopy "target\surefire-reports\*" "..\%REPORT_DIR%\surefire-reports\" /E /I /Q 2>nul
)
if exist "target\failsafe-reports" (
    xcopy "target\failsafe-reports\*" "..\%REPORT_DIR%\failsafe-reports\" /E /I /Q 2>nul
)

echo [5/7] 检查Docker状态...
cd ..
echo === Docker 状态 === > "%REPORT_DIR%\docker-status.txt"
docker ps >> "%REPORT_DIR%\docker-status.txt" 2>&1
docker images | findstr userauth >> "%REPORT_DIR%\docker-status.txt" 2>&1

echo [6/7] 运行快速测试诊断...
cd backend
echo === 快速测试诊断 === > "..\%REPORT_DIR%\quick-test-diagnosis.txt"
mvn test -q -Dmaven.test.failure.ignore=true >> "..\%REPORT_DIR%\quick-test-diagnosis.txt" 2>&1

echo [7/7] 生成诊断摘要...
cd ..
echo === CI 诊断摘要 === > "%REPORT_DIR%\diagnosis-summary.txt"
echo 诊断时间: %date% %time% >> "%REPORT_DIR%\diagnosis-summary.txt"
echo. >> "%REPORT_DIR%\diagnosis-summary.txt"

REM 分析测试结果
findstr /C:"Tests run:" "%REPORT_DIR%\quick-test-diagnosis.txt" >> "%REPORT_DIR%\diagnosis-summary.txt"
findstr /C:"BUILD FAILURE" "%REPORT_DIR%\quick-test-diagnosis.txt" >> "%REPORT_DIR%\diagnosis-summary.txt"
findstr /C:"ERROR" "%REPORT_DIR%\quick-test-diagnosis.txt" >> "%REPORT_DIR%\diagnosis-summary.txt"

echo. >> "%REPORT_DIR%\diagnosis-summary.txt"
echo === 建议的下一步行动 === >> "%REPORT_DIR%\diagnosis-summary.txt"

REM 基于常见错误模式提供建议
findstr /C:"compilation failed" "%REPORT_DIR%\quick-test-diagnosis.txt" >nul
if %errorlevel% == 0 (
    echo - 编译错误：检查依赖和导入语句 >> "%REPORT_DIR%\diagnosis-summary.txt"
    echo - 运行: mvn clean compile >> "%REPORT_DIR%\diagnosis-summary.txt"
)

findstr /C:"Connection refused" "%REPORT_DIR%\quick-test-diagnosis.txt" >nul
if %errorlevel% == 0 (
    echo - 连接错误：检查Docker和TestContainers >> "%REPORT_DIR%\diagnosis-summary.txt"
    echo - 运行: docker ps 检查容器状态 >> "%REPORT_DIR%\diagnosis-summary.txt"
)

findstr /C:"Tests run:" "%REPORT_DIR%\quick-test-diagnosis.txt" >nul
if %errorlevel% == 0 (
    echo - 测试失败：查看详细测试报告 >> "%REPORT_DIR%\diagnosis-summary.txt"
    echo - 检查: %REPORT_DIR%\surefire-reports\ >> "%REPORT_DIR%\diagnosis-summary.txt"
)

echo.
echo ========================================
echo 诊断完成！
echo ========================================
echo.
echo 诊断报告已保存到: %REPORT_DIR%
echo.
echo 主要文件:
echo - diagnosis-summary.txt    : 诊断摘要和建议
echo - quick-test-diagnosis.txt : 快速测试结果
echo - system-info.txt         : 系统环境信息
echo - dependency-tree.txt     : Maven依赖树
echo - surefire-reports\       : 详细测试报告
echo.
echo 请查看 diagnosis-summary.txt 获取建议的下一步行动
echo.

pause