@echo off
chcp 65001 >nul
echo 测试登录接口...
echo.
curl -s -X POST http://localhost:8080/sysUser/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"123456\"}"
echo.
pause