# 健身房管理系统 - 项目说明

## 项目概述
这是一个基于 Spring Boot + Vue 3 的前后端分离健身房管理系统，支持管理员和会员两种角色的使用。

## 技术栈

### 后端
- Spring Boot 3.1.4
- MyBatis Plus 3.5.3.1
- MySQL 8.0
- Knife4j 4.3.0 (API文档)
- MinIO 8.5.7 (对象存储)
- Hutool 5.8.18 (工具库)
- JWT (身份认证)

### 前端
- Vue 3.4.0
- Vite 5.0.0
- Element Plus 2.5.0 (UI组件库)
- Vue Router 4.2.0 (路由)
- Pinia 2.1.0 (状态管理)
- Axios 1.6.0 (HTTP请求)

## 项目结构

```
buildbody/
├── frontend/                 # 前端项目
│   ├── src/
│   │   ├── api/            # API接口
│   │   ├── assets/         # 静态资源
│   │   ├── components/     # 公共组件
│   │   ├── layout/         # 布局组件
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia状态管理
│   │   ├── utils/          # 工具函数
│   │   └── views/          # 页面组件
│   │       ├── business/    # 业务管理页面
│   │       ├── system/      # 系统管理页面
│   │       └── user/        # 用户端页面
│   ├── index.html
│   ├── package.json
│   └── vite.config.js
├── src/main/
│   ├── java/com/cdp/zwy/buildbody/
│   │   ├── common/         # 公共模块
│   │   │   ├── annotation/  # 注解
│   │   │   ├── config/      # 配置类
│   │   │   ├── exception/   # 异常处理
│   │   │   ├── interceptor/ # 拦截器
│   │   │   ├── result/      # 统一返回结果
│   │   │   └── utils/       # 工具类
│   │   └── module/
│   │       ├── business/     # 业务模块
│   │       └── system/       # 系统模块
│   └── resources/
│       ├── mapper/          # MyBatis映射文件
│       └── application.yml  # 配置文件
├── sql/                    # 数据库脚本
│   ├── init_database.sql   # 初始化数据库
│   └── add_is_locker_field.sql
└── pom.xml                # Maven配置
```

## 数据库表结构

### 系统管理
- `sys_user` - 系统用户表
- `sys_user_role` - 用户角色关联表
- `sys_order` - 系统订单表
- `sys_banner` - 首页轮播图表
- `img_relation` - 图片关系表
- `tb_entry_log` - 进出场记录表

### 业务管理
- `tb_card_template` - 会员卡模板表
- `tb_member_card` - 会员持卡表
- `tb_course` - 课程信息表
- `tb_course_booking` - 课程预约记录表
- `tb_coach_profile` - 教练档案表
- `tb_member_profile` - 会员档案表
- `tb_equipment` - 健身器材表
- `tb_locker` - 更衣室储物柜表
- `tb_fix_log` - 维修记录表

## 功能模块

### 管理员端
1. **系统管理**
   - 用户管理
   - 订单管理
   - 轮播图管理
   - 入场管理

2. **业务管理**
   - 会员卡管理
   - 会员卡模板
   - 课程管理
   - 教练档案
   - 会员档案
   - 器材管理
   - 储物柜管理
   - 维修记录

### 会员端
1. **首页**
   - 轮播图展示
   - 健身器材展示
   - 专业教练展示
   - 特色功能介绍

2. **个人中心**
   - 个人信息管理
   - 会员档案
   - 修改密码

3. **会员服务**
   - 我的会员卡
   - 课程预约
   - 入场记录
   - 我的订单

4. **其他功能**
   - 教练列表
   - 教练详情
   - 器材详情
   - AI助手

## 快速开始

### 前置条件
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 数据库初始化
```bash
# 创建数据库并导入初始数据
mysql -u root -p < sql/init_database.sql
```

### 后端启动
```bash
cd D:\study\buildbody
mvn spring-boot:run
```
后端服务将运行在 `http://localhost:8080`

API文档地址: `http://localhost:8080/doc.html`

### 前端启动
```bash
cd D:\study\buildbody\frontend
npm install
npm run dev
```
前端服务将运行在 `http://localhost:3000`

## 默认账号

### 管理员
- 用户名: `admin`
- 密码: `admin123`
- 角色: `admin`

### 会员
- 需要通过注册页面注册
- 注册时会自动创建会员账号和档案

## 配置说明

### 后端配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/build_body
    username: root
    password: 123456  # 修改为你的数据库密码

minio:
  endpoint: http://localhost:9000
  access-key: minioadmin
  secret-key: minioadmin
  bucket-name: buildbody

ai:
  api-key: "your-api-key"
  api-url: "https://api.deepseek.com/chat/completions"
  model: "deepseek-chat"
```

### 前端配置 (vite.config.js)
```javascript
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 待完成功能

### 后端
- [ ] 完善所有业务接口的CRUD操作
- [ ] 添加数据验证
- [ ] 完善异常处理
- [ ] 添加单元测试
- [ ] 优化SQL查询性能

### 前端
- [ ] 完善所有管理页面的增删改查功能
- [ ] 添加表单验证
- [ ] 完善用户个人中心功能
- [ ] 添加数据可视化图表
- [ ] 优化页面交互体验

### 其他
- [ ] 集成MinIO文件上传功能
- [ ] 集成支付功能
- [ ] 添加消息通知功能
- [ ] 完善AI助手功能

## 开发规范

### 代码风格
- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue 3官方风格指南
- 使用ESLint进行代码检查

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 重构
- test: 测试相关
- chore: 构建/工具链相关

## 联系方式
如有问题，请联系项目维护者。

## 许可证
MIT License