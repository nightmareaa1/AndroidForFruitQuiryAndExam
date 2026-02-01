# Java 21 和 Gradle 兼容性修复指南

## 问题描述
```
Your build is currently configured to use incompatible Java 21.0.8 and Gradle 8.2.
Cannot sync the project.
We recommend upgrading to Gradle version 9.0-milestone-1.
The minimum compatible Gradle version is 8.5.
The maximum compatible Gradle JVM version is 19.
```

## 解决方案

### 方法1: 自动修复脚本（推荐）

**Windows 命令行:**
```bash
android-app\scripts\fix-gradle-java21.bat
```

**PowerShell:**
```powershell
android-app\scripts\fix-gradle-java21.ps1
```

### 方法2: 手动修复步骤

#### 步骤1: 清理Gradle缓存
```bash
cd android-app
gradlew clean --stop
gradlew --stop
```

#### 步骤2: 清除本地Gradle缓存
**Windows:**
```cmd
rmdir /s /q "%USERPROFILE%\.gradle\caches"
rmdir /s /q "%USERPROFILE%\.gradle\daemon"
```

**PowerShell:**
```powershell
Remove-Item "$env:USERPROFILE\.gradle\caches" -Recurse -Force -ErrorAction SilentlyContinue
Remove-Item "$env:USERPROFILE\.gradle\daemon" -Recurse -Force -ErrorAction SilentlyContinue
```

#### 步骤3: 重新下载Gradle Wrapper
```bash
gradlew wrapper --gradle-version=8.5
```

#### 步骤4: 测试构建
```bash
gradlew assembleDebug
```

### 方法3: Android Studio修复

1. **关闭Android Studio**
2. **运行修复脚本**（方法1）
3. **重新打开Android Studio**
4. **点击 "Sync Project with Gradle Files"**

## 已修复的配置

### Gradle版本升级
- **从**: Gradle 8.2
- **到**: Gradle 8.5（支持Java 21）

### 插件版本更新
- **Android Gradle Plugin**: 8.2.0 → 8.2.2
- **Kotlin**: 1.9.20 → 1.9.22
- **KSP**: 1.9.20-1.0.14 → 1.9.22-1.0.16

### JVM参数优化
添加了Java 21兼容性参数：
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8 \
  --add-opens=java.base/java.util=ALL-UNNAMED \
  --add-opens=java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens=java.base/java.text=ALL-UNNAMED \
  --add-opens=java.desktop/java.awt.font=ALL-UNNAMED \
  [其他兼容性参数...]
```

## 验证修复

### 检查Java版本
```bash
java -version
```
应该显示Java 21.x.x

### 检查Gradle版本
```bash
gradlew --version
```
应该显示Gradle 8.5

### 测试构建
```bash
gradlew assembleDebug
```
应该成功构建无错误

## 常见问题

### Q: 仍然看到Java版本错误？
**A**: 确保JAVA_HOME环境变量指向Java 21安装目录

### Q: Android Studio仍然无法同步？
**A**: 
1. 关闭Android Studio
2. 删除 `.idea` 文件夹
3. 运行修复脚本
4. 重新打开项目

### Q: 构建速度变慢？
**A**: 启用了配置缓存来提高性能：
```properties
org.gradle.configuration-cache=true
```

### Q: 需要回退到旧版本？
**A**: 修改 `gradle-wrapper.properties`:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.2-bin.zip
```

## 兼容性矩阵

| Java版本 | 最小Gradle版本 | 推荐Gradle版本 |
|---------|---------------|---------------|
| Java 17 | 7.3           | 8.5           |
| Java 21 | 8.5           | 8.5+          |

## 后续步骤

修复完成后，你可以：
1. 运行单元测试: `gradlew testDebugUnitTest`
2. 运行UI测试: `gradlew connectedAndroidTest`
3. 构建发布版本: `gradlew assembleRelease`

## 需要帮助？

如果问题仍然存在：
1. 检查Android Studio版本（推荐最新稳定版）
2. 确认Java 21正确安装
3. 查看详细错误日志
4. 考虑使用Java 17作为替代方案