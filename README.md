# 健身房管理系统 - 项目说明

## 项目概述
这是一个基于 Spring Boot + Vue 3 的前后端分离健身房管理系统，支持管理员和会员两种角色的使用。

## 技术栈

### 后端技术栈

#### 核心框架
- Spring Boot 3.1.4 - 主框架
- Java 17 - 编程语言

#### 数据库相关
- MySQL - 关系型数据库
- MyBatis Plus 3.5.3.1 - ORM框架，增强MyBatis功能
- MyBatis Spring Boot Starter 3.0.2 - MyBatis集成

#### Web开发
- Spring Boot Web - Web应用开发
- Spring Validation - 参数校验

#### 工具库
- Hutool 5.8.18 - Java工具类库
- Lombok - 简化Java代码
- FastJSON2 2.0.43 - JSON处理

#### API文档
- Knife4j 4.3.0 - Swagger增强版API文档

#### 文件存储
- MinIO 8.5.7 - 对象存储服务

#### 网络通信
- OkHttp 4.12.0 - HTTP客户端

#### 开发工具
- Spring DevTools - 热部署

### 前端技术栈

#### 核心框架
- Vue 3.4.0 - 渐进式JavaScript框架
- Vite 5.0.0 - 下一代前端构建工具

#### UI组件
- Element Plus 2.5.0 - Vue 3 UI组件库
- @element-plus/icons-vue 2.3.0 - Element Plus图标库

#### 路由和状态管理
- Vue Router 4.2.0 - 官方路由管理器
- Pinia 2.1.0 - Vue 3状态管理库

#### 网络请求
- Axios 1.6.0 - HTTP客户端

#### 功能组件
- Marked 17.0.6 - Markdown解析和渲染
- Vue3-cropper 0.4.0 - 图片裁剪组件

### AI技术栈

#### 大语言模型
- DeepSeek API - 大语言模型服务
- deepseek-chat - 模型名称

#### 实时通信
- SSE (Server-Sent Events) - 服务器推送事件，实现流式响应

### 基础设施

#### 数据库
- MySQL 8.0 - 主数据库

#### 对象存储
- MinIO - 文件和图片存储

### 开发环境
- 端口配置: 后端8080，前端通过Vite代理

### 技术架构特点

#### 前后端分离
- 前端Vue 3 + Vite
- 后端Spring Boot RESTful API
- 通过Axios进行HTTP通信

#### 现代化开发
- Java 17最新特性
- Vue 3 Composition API
- Vite快速构建
- 热部署提升开发效率

#### AI集成
- DeepSeek大语言模型
- Function Calling智能对话
- RAG静态检索
- SSE流式响应

#### 文件管理
- MinIO对象存储
- 图片裁剪上传
- Markdown富文本支持

#### API文档
- Knife4j自动生成API文档
- Swagger UI可视化测试

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

### 用户角色权限

#### 访客（未登录用户）
- 查看健身房相关信息
- 查看器械信息
- 查看在售会员卡信息
- 查看在职教练信息
- 查看在售私教课信息
- 使用AI客服

#### 会员
**基础功能**
- 登录用户端，退出登录
- 查看个人信息，修改个人信息，修改密码
- 查看健身房相关信息
- 查看器械信息
- 查看在售会员卡信息
- 查看在职教练信息
- 查看在售私教课信息
- 使用AI客服

**业务功能**
- 购买私教课
- 查看我的私教课（会员可能曾经是VIP）
- 查看我的预约记录（会员可能曾经是VIP）
- 查看我的会员卡
- 查看我的入场记录
- 出场
- 查看我的订单
- 订单退款
- 借用储物柜（查看所有没有被占用的储物柜，占用储物柜，临时打开储物柜，退换储物柜）

#### VIP会员
**基础功能**
- 登录用户端，退出登录
- 查看个人信息，修改个人信息，修改密码
- 查看健身房相关信息
- 查看器械信息
- 查看在售会员卡信息
- 查看在职教练信息
- 查看在售私教课信息
- 使用AI客服

**业务功能**
- 购买私教课
- 查看我的私教课
- 预约教练上课
- 查看我的预约记录
- 查看我的会员卡
- 查看我的入场记录
- 出场
- 查看我的订单
- 订单退款
- 借用储物柜（查看所有没有被占用的储物柜，占用储物柜，临时打开储物柜，退换储物柜）

#### 教练
**基础功能**
- 登录用户端，退出登录
- 查看个人信息，修改个人信息，修改密码
- 查看健身房相关信息
- 查看器械信息
- 查看在售会员卡信息
- 查看在职教练信息
- 查看在售私教课信息

**业务功能**
- 核销课程
- 查看我的课程

#### 管理员
**用户管理**
- 查看，修改用户信息
- 注册会员账号
- 注册教练账号

**档案管理**
- 查看，修改会员档案信息
- 查看，修改教练信息

**订单管理**
- 查看，删除订单信息

**轮播图管理**
- 查看，新增，修改，删除轮播图信息

**入场管理**
- 查看入场信息
- 会员入场
- 会员出场

**会员卡管理**
- 新增，修改，删除会员卡信息
- 查看使用中的会员卡信息

**课程管理**
- 查看，新增，删除课程信息
- 核销课程

**器材管理**
- 查看，修改，删除器材信息

**储物柜管理**
- 查找，新增，修改储物柜
- 临时打开储物柜

**其他功能**
- 使用AI客服

### 核心业务流程

#### 用户角色转换机制

**成为会员**
- 方式：管理员注册并办卡
- 流程：生成账号 → 绑定该账号角色 → 绑定会员档案 → 生成会员卡的订单

**会员成为VIP**
- 方式：会员购买私教课
- 流程：生成私教课订单 → 该账号添加VIP角色

**VIP降级为会员**
- 条件：所购买的所有已付款私教课订单剩余次数为零
- 自动执行：系统自动检测并降级

**成为教练**
- 方式：管理员注册教练
- 流程：生成账号 → 绑定该账号角色 → 绑定教练档案

#### 会员卡消费流程
1. 会员或VIP到前台（管理员）报出手机号
2. 管理员输入手机号查询会员信息
3. 核实人脸信息
4. 入场放行
5. 如果是次卡则剩余次数减一
6. 会员卡到期则不放行

#### 储物柜使用流程
1. 用户进入更衣室
2. 录入当前账号的手机号
3. 验证手机号与账号是否一致
4. 核验通过后，查找所有未被占用的储物柜
5. 供用户选择对应柜子
6. 用户选择后占用储物柜
7. 占用后，用户可以临时打开和还柜

#### 退款业务规则

**私教课退款**
- 退款金额 = 总金额 ÷ 总次数 × 剩余次数 × 80%

**会员卡退款**
- 次卡：退款金额 = 总金额 ÷ 总次数 × 剩余次数 × 80%
- 时间卡：退款金额 = 总金额 ÷ 总天数 × 剩余天数 × 80%

#### VIP私教课管理
**购买即VIP**
- 购买私教课后自动成为VIP

**次数管理**
- 每次预约扣除次数
- 核销后不恢复

**自动降级**
- 次数用完后自动降级为普通会员

**灵活退款**
- 支持按剩余次数退款
- 退款比例为80%

**预约管理**
- 支持预约、核销、取消等完整流程

#### 教练核销课程流程
1. 检查剩余次数
2. 次数用完则降级为普通会员
3. 完成核销操作

### AI客服功能
**技术实现**
- 使用流式对话（RAG静态检索）
- 支持SSE实时响应
- Markdown富文本渲染
- 图片自动提取和显示

**功能特点**
- 智能问答系统
- 实时对话交互
- 丰富的文本格式支持
- 图片内容识别和展示

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
- 密码: `admin`
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


## 联系方式
如有问题，请联系项目维护者。

## 许可证
MIT License