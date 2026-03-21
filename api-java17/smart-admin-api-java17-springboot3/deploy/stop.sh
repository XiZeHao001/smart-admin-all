#!/bin/bash
# deploy/stop.sh - 停止脚本
# 路径固定为 /home/app

set -e

APP_DIR="/home/app"
cd "$APP_DIR"

echo "=========================================="
echo "停止 Smart Admin 应用"
echo "=========================================="

docker-compose down

echo "=========================================="
echo "服务已停止"
echo "=========================================="

