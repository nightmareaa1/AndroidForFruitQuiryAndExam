# 2GB服务器部署优化指南

本文档说明如何在2核2G内存的云服务器上部署后端服务，将内存占用从1GB+降低到约400-500MB。

## 优化概览

| 优化项 | 优化前 | 优化后 | 节省内存 |
|--------|--------|--------|----------|
| JVM堆内存 | 无限制(约1GB) | 512MB | ~500MB |
| 数据库连接池 | 20-50连接 | 5连接 | ~100MB |
| Redis连接池 | 8-20连接 | 4连接 | ~50MB |
| Actuator端点 | 10+端点 | 2端点 | ~50MB |
| Tomcat线程 | 200线程 | 50线程 | ~100MB |
| 日志级别 | INFO | WARN/INFO | ~30MB |
| **总计** | **~1.2GB** | **~400-500MB** | **~700MB** |

## 快速部署（推荐）

### 方式一：使用轻量级配置（最简单）

```bash
# 1. 构建应用
cd backend
mvn clean package -DskipTests

# 2. 使用light配置启动（2GB服务器专用）
java -jar -Dspring.profiles.active=light target/userauth-backend-1.0.0.jar
```

### 方式二：使用Docker部署（推荐）

```bash
# 1. 构建Docker镜像
cd backend
docker build -t userauth-backend:light .

# 2. 运行容器（自动使用优化的JVM参数）
docker run -d \
  --name userauth-backend \
  --memory=1g \
  --memory-swap=1.5g \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=light \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/userauth_dev \
  -e SPRING_DATASOURCE_USERNAME=userauth \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_REDIS_HOST=redis \
  -e JWT_SECRET=your-secret-key-at-least-32-characters \
  userauth-backend:light
```

### 方式三：使用docker-compose部署

```yaml
version: '3.8'

services:
  backend:
    build: ./backend
    container_name: userauth-backend
    restart: unless-stopped
    # 内存限制
    deploy:
      resources:
        limits:
          memory: 800M
        reservations:
          memory: 400M
    environment:
      # 使用轻量级配置
      SPRING_PROFILES_ACTIVE: light
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/userauth_dev
      SPRING_DATASOURCE_USERNAME: userauth
      SPRING_DATASOURCE_PASSWORD: ${DB_PASSWORD}
      SPRING_REDIS_HOST: redis
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - app-network

  mysql:
    image: mysql:8.0
    container_name: userauth-mysql
    restart: unless-stopped
    # MySQL内存限制
    deploy:
      resources:
        limits:
          memory: 512M
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_ROOT_PASSWORD}
      MYSQL_DATABASE: userauth_dev
      MYSQL_USER: userauth
      MYSQL_PASSWORD: ${DB_PASSWORD}
    command: >
      --default-authentication-plugin=mysql_native_password
      --character-set-server=utf8mb4
      --collation-server=utf8mb4_unicode_ci
      # MySQL内存优化
      --innodb-buffer-pool-size=128M
      --key-buffer-size=16M
      --query-cache-size=0
      --tmp-table-size=16M
      --max-connections=50
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    networks:
      - app-network

  redis:
    image: redis:7-alpine
    container_name: userauth-redis
    restart: unless-stopped
    # Redis内存限制
    deploy:
      resources:
        limits:
          memory: 128M
    command: >
      redis-server
      --maxmemory 64mb
      --maxmemory-policy allkeys-lru
      --appendonly no
    volumes:
      - redis_data:/data
    ports:
      - "6379:6379"
    networks:
      - app-network

volumes:
  mysql_data:
  redis_data:

networks:
  app-network:
    driver: bridge
```

启动命令：
```bash
docker-compose up -d
```

## 详细优化说明

### 1. JVM内存优化（Dockerfile）

```dockerfile
ENV JAVA_OPTS="-Xmx512m -Xms256m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:MaxMetaspaceSize=128m \
  -XX:CompressedClassSpaceSize=32m \
  -XX:ReservedCodeCacheSize=64m \
  -XX:+DisableExplicitGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/logs"
```

| 参数 | 说明 | 推荐值 |
|------|------|--------|
| `-Xmx512m` | 最大堆内存 | 512MB（2GB服务器） |
| `-Xms256m` | 初始堆内存 | 256MB |
| `-XX:+UseG1GC` | G1垃圾收集器 | 适合小内存 |
| `-XX:MaxMetaspaceSize=128m` | 元空间上限 | 128MB |
| `-XX:+UseStringDeduplication` | 字符串去重 | 节省10-20MB |

### 2. 数据库连接池优化（application-light.yml）

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 5      # 默认20/50 → 5
      minimum-idle: 2           # 默认5/10 → 2
      idle-timeout: 120000      # 2分钟
```

**为什么这样配置：**
- 每个数据库连接占用约2-5MB内存
- 20个连接 = 100MB，5个连接 = 25MB
- 2GB服务器处理并发量有限，5个连接足够

### 3. Redis连接池优化

```yaml
spring:
  data:
    redis:
      lettuce:
        pool:
          max-active: 4         # 默认8/20 → 4
          max-idle: 2           # 默认8/10 → 2
```

### 4. Tomcat线程池优化

```yaml
server:
  tomcat:
    threads:
      max: 50                   # 默认200 → 50
      min-spare: 5              # 默认10 → 5
    max-connections: 100        # 默认8192 → 100
```

### 5. Actuator精简

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info    # 只保留health和info
  metrics:
    enabled: false              # 禁用metrics
```

### 6. 日志级别优化

```yaml
logging:
  level:
    root: WARN                  # 默认INFO → WARN
    org.springframework.security: ERROR
    org.hibernate.SQL: ERROR
```

## 内存占用监控

### 查看JVM内存使用

```bash
# 进入容器
docker exec -it userauth-backend sh

# 查看JVM内存
jps -lvm
jstat -gc $(pgrep java) 1000 5

# 查看堆内存使用
jmap -heap $(pgrep java)
```

### 使用Actuator监控

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 应用信息
curl http://localhost:8080/actuator/info
```

### 查看容器内存

```bash
# 查看容器内存使用
docker stats userauth-backend

# 查看详细内存统计
docker inspect userauth-backend | grep -i memory
```

## 性能验证

### 1. 启动内存测试

```bash
# 启动后立即查看内存
ps aux | grep java
# 或
top -p $(pgrep java)
```

**预期结果：**
- 启动时：约350-400MB
- 稳定运行：约400-500MB
- 峰值（上传文件时）：约500-600MB

### 2. 压力测试

```bash
# 使用ab或wrk进行压力测试
ab -n 1000 -c 10 http://localhost:8080/api/fruit/query?type=nutrition&fruit=mango

# 观察内存是否稳定
docker stats userauth-backend
```

## 故障排查

### 内存不足错误

如果看到 `OutOfMemoryError`：

1. **增加JVM堆内存**（如果服务器有更多内存）：
   ```dockerfile
   -Xmx768m -Xms384m
   ```

2. **进一步减少连接池**：
   ```yaml
   maximum-pool-size: 3
   max-active: 2
   ```

3. **禁用非必要功能**：
   ```yaml
   flyway:
     enabled: false    # 手动执行迁移后禁用
   ```

### 数据库连接不足

如果出现连接超时：

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 8      # 从5增加到8
      connection-timeout: 20000 # 增加超时时间到20秒
```

### 请求处理缓慢

如果并发处理能力不足：

```yaml
server:
  tomcat:
    threads:
      max: 100                  # 从50增加到100
    accept-count: 100           # 增加等待队列
```

## 与Android联调配置

### 后端配置

确保CORS配置允许Android访问：

```yaml
app:
  cors:
    allowed-origins: http://10.0.2.2:8080,http://localhost:8080
```

### Android配置

在 `NetworkModule.kt` 中配置后端地址：

```kotlin
// 使用服务器公网IP或域名
private const val BASE_URL = "http://your-server-ip:8080/api/"
```

## 总结

通过以下优化，后端服务内存占用从1GB+降低到400-500MB：

1. ✅ JVM堆内存限制为512MB
2. ✅ 数据库连接池减少到5个连接
3. ✅ Redis连接池减少到4个连接
4. ✅ Tomcat线程池减少到50个线程
5. ✅ 禁用不必要的Actuator端点和Metrics
6. ✅ 提升日志级别减少日志内存

现在2GB服务器可以同时运行：
- MySQL (~300MB)
- Redis (~64MB)
- 后端服务 (~500MB)
- 剩余约1GB用于系统和缓存

## 参考链接

- [Spring Boot Memory Optimization](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.cloud.memory)
- [JVM G1GC Tuning](https://docs.oracle.com/javase/8/docs/technotes/guides/vm/G1.html)
- [HikariCP Configuration](https://github.com/brettwooldridge/HikariCP#configuration-knobs-baby)
