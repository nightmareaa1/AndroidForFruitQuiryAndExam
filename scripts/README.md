# 脚本目录

本目录包含用于数据库初始化、部署和维护的脚本。

## 脚本列表

### 数据库脚本

#### `init-db.sql`
数据库初始化脚本，创建所有必需的表结构。

**用途**:
- 创建用户表和角色表
- 创建评价系统相关表
- 创建水果营养数据表
- 设置外键约束和索引

**使用方法**:
```bash
# MySQL
mysql -u root -p < init-db.sql

# 或在MySQL客户端中
source init-db.sql
```

**包含的表**:
- `users` - 用户表
- `user_roles` - 用户角色表
- `evaluation_models` - 评价模型表
- `evaluation_indicators` - 评价指标表
- `evaluation_tasks` - 评价任务表
- `samples` - 样本表
- `task_participants` - 任务参与者表
- `evaluation_submissions` - 评价提交表
- `evaluation_ratings` - 评价评分表
- `fruits` - 水果表
- `nutrition_data` - 营养成分表
- `flavor_data` - 风味特征表

#### `test-data.sql`
测试数据脚本，插入预置的测试数据。

**用途**:
- 插入测试用户（系统管理员、任务管理员、普通评价员）
- 插入水果营养数据（芒果、香蕉）
- 插入示例评价模型和任务

**使用方法**:
```bash
mysql -u root -p userauth < test-data.sql
```

**注意**: 仅用于开发和测试环境，不要在生产环境使用！

### 部署脚本

#### `deploy-backend.sh`
后端服务部署脚本（Linux/Mac）。

**功能**:
- 拉取最新代码
- 构建JAR包
- 停止旧服务
- 启动新服务
- 健康检查

**使用方法**:
```bash
chmod +x deploy-backend.sh
./deploy-backend.sh
```

**环境变量**:
- `BACKEND_PORT`: 服务端口（默认8080）
- `SPRING_PROFILE`: Spring配置文件（默认prod）
- `DB_URL`: 数据库连接URL
- `DB_USERNAME`: 数据库用户名
- `DB_PASSWORD`: 数据库密码

#### `deploy-backend.bat`
后端服务部署脚本（Windows）。

功能与 `deploy-backend.sh` 相同，适用于Windows环境。

**使用方法**:
```cmd
deploy-backend.bat
```

### 维护脚本

#### `backup-db.sh`
数据库备份脚本。

**功能**:
- 备份MySQL数据库
- 压缩备份文件
- 保留最近7天的备份
- 发送备份通知（可选）

**使用方法**:
```bash
chmod +x backup-db.sh
./backup-db.sh
```

**配置**:
编辑脚本中的配置变量：
```bash
DB_NAME="userauth"
DB_USER="root"
DB_PASSWORD="your_password"
BACKUP_DIR="/path/to/backups"
```

**定时备份**（使用cron）:
```bash
# 每天凌晨2点备份
0 2 * * * /path/to/scripts/backup-db.sh
```

#### `restore-db.sh`
数据库恢复脚本。

**功能**:
- 从备份文件恢复数据库
- 验证备份文件完整性
- 创建恢复前快照

**使用方法**:
```bash
./restore-db.sh /path/to/backup.sql.gz
```

### 开发辅助脚本

#### `reset-dev-db.sh`
重置开发数据库。

**功能**:
- 删除所有表
- 重新运行初始化脚本
- 插入测试数据

**使用方法**:
```bash
./reset-dev-db.sh
```

**警告**: 此脚本会删除所有数据，仅用于开发环境！

#### `generate-test-users.sh`
生成测试用户。

**功能**:
- 批量创建测试用户
- 分配不同角色
- 生成随机密码

**使用方法**:
```bash
./generate-test-users.sh 10  # 生成10个测试用户
```

## 脚本开发规范

### 命名规范
- 使用小写字母和连字符
- 使用描述性名称
- 添加适当的文件扩展名（.sh, .sql, .bat）

### 代码规范
- 添加脚本说明注释
- 使用变量存储配置
- 添加错误处理
- 提供使用说明

### 安全规范
- 不要在脚本中硬编码密码
- 使用环境变量或配置文件
- 设置适当的文件权限
- 记录敏感操作日志

## 使用注意事项

1. **权限设置**
   ```bash
   # Linux/Mac
   chmod +x *.sh
   ```

2. **环境变量**
   创建 `.env` 文件存储敏感配置：
   ```bash
   DB_PASSWORD=your_password
   JWT_SECRET=your_secret
   ```

3. **日志记录**
   所有脚本的执行日志保存在 `logs/` 目录

4. **错误处理**
   脚本执行失败时会返回非零退出码

5. **备份策略**
   - 生产环境：每天备份
   - 开发环境：按需备份
   - 保留策略：最近7天

## 故障排查

### 数据库连接失败
- 检查MySQL服务是否运行
- 验证数据库凭证
- 检查防火墙设置

### 脚本执行失败
- 检查文件权限
- 查看错误日志
- 验证环境变量

### 备份恢复失败
- 验证备份文件完整性
- 检查磁盘空间
- 确认数据库版本兼容性

## 相关文档

- [部署指南](../docs/deployment/)
- [数据库设计](../.kiro/specs/user-auth-system/design.md#数据模型)
- [后端README](../backend/README.md)
