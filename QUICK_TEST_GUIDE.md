# 快速测试指南

## 前置条件检查

### 1. 确认服务状态
```bash
# 检查后端服务是否运行
# 访问: http://localhost:8080/doc.html
# 应该能看到Knife4j API文档页面

# 检查前端服务是否运行
# 访问: http://localhost:3000
# 应该能看到前端页面
```

### 2. 确认数据库连接
```sql
-- 连接到MySQL数据库
mysql -u root -p123456

-- 检查数据库是否存在
SHOW DATABASES LIKE 'build_body';

-- 使用数据库
USE build_body;

-- 检查表是否存在
SHOW TABLES;

-- 检查管理员账号
SELECT user_id, username, nickname, status FROM sys_user WHERE username = 'admin';
```

## 快速功能测试

### 测试1: 管理员登录

#### 步骤1: 访问登录页面
```
URL: http://localhost:3000/admin/login
```

#### 步骤2: 输入登录信息
```
用户名: admin
密码: admin123
```

#### 步骤3: 点击登录按钮
- ✅ 成功: 跳转到Dashboard页面
- ❌ 失败: 检查密码是否正确，检查数据库中admin账号是否存在

### 测试2: 查看Dashboard数据

#### 步骤1: 登录后访问Dashboard
```
URL: http://localhost:3000/admin/dashboard
```

#### 步骤2: 检查数据显示
- 会员总数
- 今日入场人数
- 本月收入
- 活跃会员卡数
- 最近入场记录

#### 预期结果
- 所有数据卡片正常显示
- 数据为0或实际数据（取决于数据库是否有数据）

### 测试3: 查看API文档

#### 步骤1: 访问API文档
```
URL: http://localhost:8080/doc.html
```

#### 步骤2: 检查接口列表
- 用户管理接口
- 订单管理接口
- 轮播图管理接口
- 入场记录接口
- 会员卡管理接口
- 会员卡模板接口
- 课程管理接口
- 教练档案接口
- 会员档案接口
- 器材管理接口
- 储物柜管理接口
- 维修记录接口
- 统计数据接口
- AI聊天接口
- 文件上传接口

#### 步骤3: 测试接口调用
选择任意接口，点击"调试"按钮进行测试

### 测试4: 会员注册

#### 步骤1: 访问注册页面
```
URL: http://localhost:3000/user/register
```

#### 步骤2: 填写注册信息
```
用户名: test001
密码: 123456
确认密码: 123456
真实姓名: 测试用户
手机号: 13800138000
会员卡: 选择一个会员卡模板
```

#### 步骤3: 提交注册
- ✅ 成功: 提示注册成功，跳转到登录页面
- ❌ 失败: 检查表单验证，检查后端日志

### 测试5: 会员登录

#### 步骤1: 访问登录页面
```
URL: http://localhost:3000/user/login
```

#### 步骤2: 输入登录信息
```
用户名: test001 (或刚才注册的用户名)
密码: 123456 (或注册时设置的密码)
```

#### 步骤3: 点击登录按钮
- ✅ 成功: 跳转到会员首页
- ❌ 失败: 检查用户名和密码是否正确

### 测试6: 查看会员首页

#### 步骤1: 登录后访问首页
```
URL: http://localhost:3000/user/home
```

#### 步骤2: 检查页面内容
- 轮播图展示
- 器材列表
- 教练列表
- AI助手组件

#### 预期结果
- 页面正常加载
- 各个模块正常显示
- 数据为空或显示实际数据

## 接口测试命令

### 使用curl测试接口

#### 1. 管理员登录
```bash
curl -X POST http://localhost:8080/user/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

#### 2. 获取会员卡模板列表
```bash
curl http://localhost:8080/card-template/list
```

#### 3. 获取Dashboard统计数据
```bash
curl http://localhost:8080/statistics/dashboard
```

#### 4. 获取最近入场记录
```bash
curl http://localhost:8080/statistics/recent-entries
```

#### 5. 获取轮播图列表
```bash
curl http://localhost:8080/banner/list
```

#### 6. 获取教练列表
```bash
curl http://localhost:8080/coach-profile/list
```

#### 7. 获取器材列表
```bash
curl http://localhost:8080/equipment/list
```

## 数据库初始化

### 如果数据库为空，执行初始化脚本

#### 方法1: 使用MySQL命令行
```bash
mysql -u root -p123456 < D:\study\buildbody\sql\init_database.sql
```

#### 方法2: 使用MySQL客户端
1. 打开MySQL客户端（如Navicat、MySQL Workbench等）
2. 连接到MySQL服务器
3. 打开并执行 `D:\study\buildbody\sql\init_database.sql` 文件

#### 方法3: 在MySQL命令行中执行
```sql
-- 连接到MySQL
mysql -u root -p123456

-- 执行脚本
source D:\study\buildbody\sql\init_database.sql;

-- 检查表是否创建成功
USE build_body;
SHOW TABLES;
```

## 常见问题排查

### 问题1: 后端服务无法启动

#### 检查端口占用
```bash
# Windows
netstat -ano | findstr :8080

# 如果端口被占用，可以：
# 1. 关闭占用端口的进程
# 2. 修改application.yml中的端口号
```

#### 检查数据库连接
```yaml
# 检查 application.yml 中的数据库配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/build_body?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456  # 确认密码是否正确
```

### 问题2: 前端服务无法启动

#### 检查Node.js版本
```bash
node --version
# 建议使用 Node.js 16.x 或更高版本
```

#### 检查依赖安装
```bash
cd D:\study\buildbody\frontend
npm install
```

#### 检查端口占用
```bash
# Windows
netstat -ano | findstr :3000
```

### 问题3: 登录失败

#### 检查管理员账号
```sql
USE build_body;
SELECT user_id, username, nickname, status FROM sys_user WHERE username = 'admin';
```

#### 重置管理员密码
```sql
-- 如果密码忘记，可以重置为 admin123
UPDATE sys_user 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi' 
WHERE username = 'admin';
-- 注意: 这是 'admin123' 的BCrypt加密值
```

### 问题4: 数据库连接失败

#### 检查MySQL服务状态
```bash
# Windows
net start MySQL80

# 或者检查MySQL服务是否正在运行
sc query MySQL80
```

#### 检查数据库是否存在
```sql
SHOW DATABASES LIKE 'build_body';
```

#### 创建数据库（如果不存在）
```sql
CREATE DATABASE IF NOT EXISTS build_body 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

## 性能测试

### 简单性能测试

#### 测试接口响应时间
```bash
# 使用time命令测试
time curl http://localhost:8080/statistics/dashboard

# 预期响应时间: < 1秒
```

#### 测试并发请求
```bash
# 使用Apache Bench进行简单的并发测试
ab -n 100 -c 10 http://localhost:8080/statistics/dashboard

# 参数说明:
# -n 100: 总共100个请求
# -c 10: 并发10个请求
```

## 测试结果记录

### 测试记录模板

| 测试项 | 测试时间 | 测试结果 | 备注 |
|--------|----------|----------|------|
| 后端服务启动 | | | |
| 前端服务启动 | | | |
| 管理员登录 | | | |
| Dashboard显示 | | | |
| 会员注册 | | | |
| 会员登录 | | | |
| 会员首页显示 | | | |
| API文档访问 | | | |

### 测试通过标准
- ✅ 所有服务正常启动
- ✅ 管理员可以正常登录
- ✅ Dashboard数据正常显示
- ✅ 会员可以正常注册和登录
- ✅ 页面加载时间 < 3秒
- ✅ 接口响应时间 < 1秒

## 下一步

### 测试通过后
1. **完善管理端页面** - 开发各个管理模块的增删改查功能
2. **完善会员端页面** - 开发会员功能页面
3. **添加测试数据** - 在数据库中添加测试数据
4. **进行功能测试** - 测试各个业务功能
5. **性能优化** - 优化页面性能和数据库查询

### 测试未通过
1. **查看错误日志** - 检查后端和前端日志
2. **排查问题原因** - 根据错误信息定位问题
3. **修复问题** - 修复发现的问题
4. **重新测试** - 修复后重新进行测试

## 联系方式
如有问题或建议，请联系项目维护者。

## 更新日志
- 2026-03-23: 创建快速测试指南