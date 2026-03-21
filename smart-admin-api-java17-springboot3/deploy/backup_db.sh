#!/bin/bash
# /home/app/deploy/backup_db.sh

# 1. 配置
BACKUP_DIR="/home/app/backups/postgres"
mkdir -p $BACKUP_DIR
DATE=$(date +%Y%m%d_%H%M%S)
FILENAME="smart_admin_$DATE.sql.gz"
# 保留最近 7 天的备份
KEEP_DAYS=7

# 2. 执行备份 (使用 docker exec 调用容器内的 pg_dump)
echo "开始备份数据库..."
docker exec smart-admin-postgres pg_dump -U postgres smart_admin_v3 | gzip > "$BACKUP_DIR/$FILENAME"

# 3. 检查备份是否成功
if [ -f "$BACKUP_DIR/$FILENAME" ]; then
    echo "✅ 备份成功: $BACKUP_DIR/$FILENAME Size: $(du -h "$BACKUP_DIR/$FILENAME" | cut -f1)"
else
    echo "❌ 备份失败！"
    exit 1
fi

# 4. 清理旧备份
echo "清理 $KEEP_DAYS 天前的旧备份..."
find $BACKUP_DIR -name "smart_admin_*.sql.gz" -mtime +$KEEP_DAYS -exec rm {} \;

echo "备份任务完成。"