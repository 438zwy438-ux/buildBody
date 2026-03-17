# 健身房管理系统前端

基于 Vue3 + Element Plus + Vite 构建的健身房管理系统前端应用。

## 技术栈

- Vue 3.4+
- Element Plus 2.5+
- Vue Router 4.2+
- Pinia 2.1+
- Axios 1.6+
- Vite 5.0+

## 功能模块

### 系统管理
- 用户管理
- 订单管理
- 轮播图管理
- 入场管理

### 业务管理
- 会员卡管理
- 课程管理
- 器材管理
- 储物柜管理

## 安装依赖

```bash
cd frontend
npm install
```

## 启动开发服务器

```bash
npm run dev
```

开发服务器将在 http://localhost:3000 启动

## 构建生产版本

```bash
npm run build
```

## 项目结构

```
frontend/
├── src/
│   ├── api/              # API接口
│   ├── assets/           # 静态资源
│   ├── layout/           # 布局组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   │   ├── business/     # 业务管理页面
│   │   └── system/       # 系统管理页面
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html            # HTML模板
├── package.json          # 项目配置
└── vite.config.js        # Vite配置
```

## API接口

所有API请求都会通过代理转发到后端服务器 http://localhost:8080

### 主要接口

- 用户相关: `/api/sysUser/*`
- 订单相关: `/api/sysOrder/*`
- 轮播图相关: `/api/sysBanner/*`
- 入场记录: `/api/tbEntryLog/*`
- 文件上传: `/api/common/upload`

## 注意事项

1. 确保后端服务已启动并运行在 http://localhost:8080
2. 登录后会获取token并存储在localStorage中
3. 所有需要认证的接口都会在请求头中携带token
4. 文件上传功能需要后端MinIO服务支持