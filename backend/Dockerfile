# Production Dockerfile for Spring Boot Backend
FROM eclipse-temurin:17-jdk-jammy as builder

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Production stage
FROM eclipse-temurin:17-jre-jammy

# Create app user
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Create necessary directories
RUN mkdir -p /app/logs /app/uploads && chown -R appuser:appuser /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown appuser:appuser app.jar

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# JVM内存优化参数（2GB服务器推荐）
# -Xmx512m: 最大堆内存512MB
# -Xms256m: 初始堆内存256MB
# -XX:+UseG1GC: 使用G1垃圾收集器，适合小内存
# -XX:MaxGCPauseMillis=200: 最大GC停顿时间200ms
# -XX:+UseStringDeduplication: 字符串去重，节省内存
# -XX:MaxMetaspaceSize=128m: 元空间最大128MB
# -XX:CompressedClassSpaceSize=32m: 压缩类空间32MB
# -XX:ReservedCodeCacheSize=64m: 代码缓存64MB
# -XX:+DisableExplicitGC: 禁止显式GC调用
# -XX:+HeapDumpOnOutOfMemoryError: OOM时生成堆转储
# -XX:HeapDumpPath=/app/logs: 堆转储路径
ENV JAVA_OPTS="-Xmx512m -Xms256m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UseStringDeduplication \
  -XX:MaxMetaspaceSize=128m \
  -XX:CompressedClassSpaceSize=32m \
  -XX:ReservedCodeCacheSize=64m \
  -XX:+DisableExplicitGC \
  -XX:+HeapDumpOnOutOfMemoryError \
  -XX:HeapDumpPath=/app/logs \
  -Djava.security.egd=file:/dev/./urandom \
  -Dspring.backgroundpreinitializer.ignore=true \
  -Dspring.jmx.enabled=false"

# Run application with memory optimization
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]