@echo off
chcp 65001 >nul
echo ========================================
echo 健身房管理系统 - 自动化测试脚本
echo ========================================
echo.

echo [1/10] 测试服务状态...
curl -s http://localhost:8080/doc.html >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 后端服务正常运行 (http://localhost:8080)
) else (
    echo ❌ 后端服务异常
)

curl -s http://localhost:3000 >nul 2>&1
if %errorlevel% equ 0 (
    echo ✅ 前端服务正常运行 (http://localhost:3000)
) else (
    echo ❌ 前端服务异常
)
echo.

echo [2/10] 测试管理员登录接口...
curl -s -X POST http://localhost:8080/sysUser/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}" > login_result.json 2>&1
type login_result.json
echo.

echo [3/10] 测试未授权访问...
curl -s http://localhost:8080/statistics/dashboard > unauthorized_result.json 2>&1
type unauthorized_result.json
echo.

echo [4/10] 测试会员卡模板接口...
curl -s http://localhost:8080/cardTemplate/list > card_template_result.json 2>&1
type card_template_result.json
echo.

echo [5/10] 测试教练列表接口...
curl -s http://localhost:8080/coach-profile/list > coach_list_result.json 2>&1
type coach_list_result.json
echo.

echo [6/10] 测试器材列表接口...
curl -s http://localhost:8080/equipment/list > equipment_list_result.json 2>&1
type equipment_list_result.json
echo.

echo [7/10] 测试轮播图接口...
curl -s http://localhost:8080/banner/list > banner_list_result.json 2>&1
type banner_list_result.json
echo.

echo [8/10] 测试数据库连接...
mysql -u root -p123456 -e "USE build_body; SHOW TABLES;" > db_tables.txt 2>&1
if %errorlevel% equ 0 (
    echo ✅ 数据库连接正常
    type db_tables.txt
) else (
    echo ❌ 数据库连接异常
)
echo.

echo [9/10] 检查管理员账号...
mysql -u root -p123456 -e "USE build_body; SELECT user_id, username, nickname, status FROM sys_user WHERE username = 'admin';" > admin_info.txt 2>&1
type admin_info.txt
echo.

echo [10/10] 检查用户角色关联...
mysql -u root -p123456 -e "USE build_body; SELECT * FROM sys_user_role;" > user_roles.txt 2>&1
type user_roles.txt
echo.

echo ========================================
echo 测试完成！
echo ========================================
echo.
echo 测试结果文件：
echo - login_result.json (登录结果)
echo - unauthorized_result.json (未授权访问结果)
echo - card_template_result.json (会员卡模板结果)
echo - coach_list_result.json (教练列表结果)
echo - equipment_list_result.json (器材列表结果)
echo - banner_list_result.json (轮播图结果)
echo - db_tables.txt (数据库表列表)
echo - admin_info.txt (管理员信息)
echo - user_roles.txt (用户角色信息)
echo.
pause