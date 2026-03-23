@echo off
chcp 65001 >nul
echo ========================================
echo 修复 tb_entry_log 表结构
echo ========================================
echo.

echo 正在执行数据库修复脚本...
echo.

mysql -u root -p123456 < D:\study\buildbody\sql\fix_entry_log_simple.sql

if %errorlevel% equ 0 (
    echo.
    echo ✅ 数据库表结构修复成功！
) else (
    echo.
    echo ❌ 数据库表结构修复失败，请检查错误信息
)

echo.
echo ========================================
echo 修复完成
echo ========================================
echo.
pause