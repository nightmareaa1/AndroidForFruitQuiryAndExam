#!/bin/bash

# 删除 fruits 表的 en_name 列的脚本

echo "正在连接 MySQL 并删除 en_name 列..."

# 方法1: 使用 Docker 执行 SQL
docker exec -i userauth-mysql-dev mysql -u userauth -p'SecureDbPass456$%^' -D userauth_dev <<EOF
ALTER TABLE fruits DROP COLUMN IF EXISTS en_name;
SELECT 'en_name column removed successfully' as result;
EOF

if [ $? -eq 0 ]; then
    echo "✅ 成功删除 en_name 列"
else
    echo "❌ 执行失败，尝试其他方法..."
    echo ""
    echo "请手动执行以下步骤："
    echo "1. docker exec -it userauth-mysql-dev bash"
    echo "2. mysql -u userauth -p"
    echo "3. 输入密码: SecureDbPass456\$%^"
    echo "4. USE userauth_dev;"
    echo "5. ALTER TABLE fruits DROP COLUMN IF EXISTS en_name;"
fi
