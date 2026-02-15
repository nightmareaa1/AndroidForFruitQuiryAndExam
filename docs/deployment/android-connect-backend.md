# Android应用连接后端服务指南

本文档说明如何配置Android模拟器和真机连接部署在云服务器上的后端服务。

## 网络架构

```
┌─────────────────────────────────────────────────────────────┐
│                        公网/局域网                              │
│  ┌──────────────┐                    ┌──────────────────┐   │
│  │ Android真机   │◄──────────────────►│   云服务器        │   │
│  │ (4G/WiFi)    │   公网IP:8080      │   后端服务:8080   │   │
│  └──────────────┘                    └──────────────────┘   │
│                                                               │
│  ┌─────────────────┐                                          │
│  │ Android模拟器    │◄──────────────────────────────────────►│
│  │ (本机/宿主机网络)│   10.0.2.2:8080 (宿主机回环地址)          │
│  └─────────────────┘                                          │
└─────────────────────────────────────────────────────────────┘
```

## 方案一：模拟器连接后端

### 方式1：后端运行在本地电脑（开发环境）

如果后端在你开发电脑上运行：

**Android模拟器特殊地址**：
- `10.0.2.2` - 指向开发电脑的localhost

**修改Android配置**：

```kotlin
// NetworkModule.kt 或 Constants.kt
object NetworkConfig {
    // 模拟器访问本机后端
    const val BASE_URL = "http://10.0.2.2:8080/api/"
}
```

### 方式2：后端在云服务器（生产/测试环境）

**修改Android配置**：

```kotlin
object NetworkConfig {
    // 模拟器访问云服务器后端
    // 使用服务器公网IP
    const val BASE_URL = "http://你的服务器IP:8080/api/"
    
    // 示例
    // const val BASE_URL = "http://192.168.1.100:8080/api/"
    // const val BASE_URL = "http://203.0.113.10:8080/api/"
}
```

---

## 方案二：真机连接后端

### 方式1：手机和服务器在同一WiFi（局域网）

**Android配置**：

```kotlin
object NetworkConfig {
    // 使用服务器局域网IP
    const val BASE_URL = "http://192.168.1.100:8080/api/"
}
```

**获取服务器局域网IP**：

```bash
# 在服务器上执行
ip addr show | grep "inet " | head -2
# 或
hostname -I
```

### 方式2：手机通过公网访问（4G/外部WiFi）

**Android配置**：

```kotlin
object NetworkConfig {
    // 使用服务器公网IP或域名
    const val BASE_URL = "http://服务器公网IP:8080/api/"
    
    // 如果有域名
    // const val BASE_URL = "http://api.yourdomain.com/api/"
}
```

---

## 后端服务器配置

### 1. 检查CORS配置

确保后端允许Android应用访问。编辑 `docker-compose.jar.yml`：

```yaml
services:
  backend:
    environment:
      # 添加你的Android应用地址（根据实际IP修改）
      CORS_ALLOWED_ORIGINS: >
        http://localhost:8080,
        http://10.0.2.2:8080,
        http://192.168.1.*:8080,
        http://你的服务器公网IP:8080
```

**推荐：允许所有来源（仅开发环境）**：

```yaml
CORS_ALLOWED_ORIGINS: "*"
```

### 2. 配置服务器防火墙

**开放8080端口**：

```bash
# Ubuntu/Debian (UFW)
sudo ufw allow 8080/tcp
sudo ufw status

# CentOS (Firewalld)
sudo firewall-cmd --permanent --add-port=8080/tcp
sudo firewall-cmd --reload

# 或使用iptables
sudo iptables -I INPUT -p tcp --dport 8080 -j ACCEPT
sudo iptables-save
```

### 3. 配置云服务器安全组

如果你使用阿里云、腾讯云等云服务器：

1. 登录云服务器控制台
2. 找到 **安全组** 设置
3. 添加 **入站规则**：
   - 协议：TCP
   - 端口：8080
   - 来源：0.0.0.0/0 (允许所有) 或指定IP段

### 4. 验证后端可访问性

**在服务器本地测试**：

```bash
curl http://localhost:8080/actuator/health
```

**在其他设备测试**（手机浏览器或电脑）：

```bash
# 使用浏览器访问
http://你的服务器IP:8080/actuator/health

# 或使用curl
curl http://你的服务器IP:8080/actuator/health
```

---

## Android项目配置

### 1. 配置网络权限

确保 `AndroidManifest.xml` 有网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

<!-- Android 9+ 需要允许明文HTTP（如果是http而非https） -->
<application
    android:usesCleartextTraffic="true"
    ... >
</application>
```

### 2. 创建网络配置文件

创建 `res/xml/network_security_config.xml`：

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

在 `AndroidManifest.xml` 中引用：

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ... >
</application>
```

### 3. 修改Retrofit配置

找到 `NetworkModule.kt` 或 `ApiService.kt`：

```kotlin
// 方案1：使用BuildConfig动态配置
object NetworkConfig {
    val BASE_URL = when (BuildConfig.BUILD_TYPE) {
        "debug" -> "http://10.0.2.2:8080/api/"  // 模拟器
        // "debug" -> "http://192.168.1.100:8080/api/"  // 真机调试
        "release" -> "http://你的服务器公网IP:8080/api/"
        else -> "http://10.0.2.2:8080/api/"
    }
}

// Retrofit配置
@Provides
@Singleton
fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .baseUrl(NetworkConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
```

### 4. 更好的方案：配置文件切换

创建 `Config.kt`：

```kotlin
object ServerConfig {
    // 开发环境 - 模拟器
    const val DEV_EMULATOR = "http://10.0.2.2:8080/api/"
    
    // 开发环境 - 真机（同一WiFi）
    const val DEV_DEVICE_LOCAL = "http://192.168.1.100:8080/api/"
    
    // 测试环境 - 公网
    const val TEST_PUBLIC = "http://203.0.113.10:8080/api/"
    
    // 生产环境
    const val PRODUCTION = "http://api.yourdomain.com/api/"
    
    // 当前使用的配置
    val BASE_URL = DEV_EMULATOR
    // val BASE_URL = DEV_DEVICE_LOCAL
    // val BASE_URL = TEST_PUBLIC
}
```

使用：

```kotlin
Retrofit.Builder()
    .baseUrl(ServerConfig.BASE_URL)
    ...
```

---

## 调试测试

### 1. 测试后端连通性

**在Android Studio的Logcat中查看**：

```kotlin
// 添加网络日志拦截器
val logging = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
```

### 2. 使用浏览器测试

**在手机上打开浏览器**：

```
http://你的服务器IP:8080/actuator/health
```

如果能看到 `{"status":"UP"}`，说明网络连通。

### 3. 网络调试工具

**Ping测试**：

```bash
# 在电脑或手机的Termux中
ping 你的服务器IP
```

**端口测试**：

```bash
# 测试8080端口是否开放
telnet 你的服务器IP 8080
# 或
curl -v http://你的服务器IP:8080/actuator/health
```

---

## 常见问题

### Q: 模拟器提示 "Connection refused"

**原因**: 
- 后端未启动
- 使用了错误的IP地址

**解决**:
```kotlin
// 模拟器必须使用 10.0.2.2，不能是 localhost 或 127.0.0.1
const val BASE_URL = "http://10.0.2.2:8080/api/"  // ✅ 正确
const val BASE_URL = "http://localhost:8080/api/"  // ❌ 错误
const val BASE_URL = "http://127.0.0.1:8080/api/"  // ❌ 错误
```

### Q: 真机提示 "Cleartext HTTP traffic not permitted"

**原因**: Android 9+ 默认禁止明文HTTP

**解决**:
1. 在 `AndroidManifest.xml` 添加：
```xml
<application android:usesCleartextTraffic="true" ...>
```

2. 或创建 `network_security_config.xml`

### Q: 连接超时 (Timeout)

**原因**: 服务器防火墙或安全组未开放端口

**解决**:
1. 检查服务器防火墙：
```bash
sudo ufw status  # 应该显示 8080 ALLOW
```

2. 检查云服务器安全组入站规则

3. 检查后端是否运行：
```bash
docker ps  # 查看容器状态
docker logs userauth-backend  # 查看日志
```

### Q: CORS错误

**原因**: 后端未配置允许的来源

**解决**:
修改 `docker-compose.jar.yml`：
```yaml
environment:
  CORS_ALLOWED_ORIGINS: "*"  # 允许所有（开发环境）
  # 或指定具体地址
  # CORS_ALLOWED_ORIGINS: "http://10.0.2.2:8080,http://192.168.1.100:8080"
```

重启服务：
```bash
docker-compose -f docker-compose.jar.yml restart backend
```

---

## 快速配置清单

### 服务器端配置

- [ ] 后端服务运行在 0.0.0.0:8080
- [ ] 防火墙开放 8080 端口
- [ ] 云服务器安全组开放 8080 端口
- [ ] CORS配置允许Android应用访问

### Android端配置

- [ ] AndroidManifest.xml 添加 INTERNET 权限
- [ ] AndroidManifest.xml 设置 usesCleartextTraffic="true"
- [ ] 修改 BASE_URL 为服务器IP:8080
- [ ] 模拟器使用 10.0.2.2:8080
- [ ] 真机使用服务器IP:8080

### 网络验证

- [ ] 服务器本地 curl localhost:8080/actuator/health 正常
- [ ] 电脑浏览器访问 http://服务器IP:8080/actuator/health 正常
- [ ] 手机浏览器访问 http://服务器IP:8080/actuator/health 正常
- [ ] Android应用能正常调用API

---

## 完整配置示例

### docker-compose.jar.yml

```yaml
services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile.jar
    environment:
      SPRING_PROFILES_ACTIVE: light
      # 允许所有来源访问（开发环境）
      CORS_ALLOWED_ORIGINS: "*"
      # 生产环境指定具体域名
      # CORS_ALLOWED_ORIGINS: "http://yourdomain.com,http://10.0.2.2:8080"
    ports:
      - "0.0.0.0:8080:8080"  # 确保监听所有接口
```

### Android Config.kt

```kotlin
object Config {
    // 根据环境切换
    val BASE_URL = when {
        BuildConfig.DEBUG && isEmulator() -> "http://10.0.2.2:8080/api/"
        BuildConfig.DEBUG -> "http://192.168.1.100:8080/api/"  // 修改为实际IP
        else -> "http://你的服务器公网IP:8080/api/"
    }
    
    private fun isEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }
}
```

---

**按照以上配置，你的Android模拟器和真机应该都能连接到后端服务了！** 🚀
