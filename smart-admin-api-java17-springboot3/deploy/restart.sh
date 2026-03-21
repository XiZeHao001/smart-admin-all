#!/bin/bash
# deploy/restart.sh - 重启脚本
# 路径固定为 /home/app

set -e

APP_DIR="/home/app"
SCRIPT_DIR="$APP_DIR/deploy"

echo "停止服务..."
"$SCRIPT_DIR/stop.sh"

sleep 5

echo "启动服务..."
"$SCRIPT_DIR/start.sh"

