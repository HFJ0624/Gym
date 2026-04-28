# Gym-front 用户前台系统

## 1. 项目介绍

`Gym-front` 是体育场馆预约平台的用户前台系统，基于 Vue3 + Vite + Element Plus 开发。

该前台面向普通用户，主要提供场馆浏览、场地预约、我的预约、公告查看、个人中心、商城、购物车、订单、评论收藏、签到以及智能 Agent 问答等功能。

当前前台已从早期 mock/localStorage 数据逐步改为调用后端真实接口，接口统一走：

```text
/front/**
```

后端服务地址默认：

```text
http://localhost:9601
```

开发环境通过 Vite 代理转发：

```text
/api -> http://localhost:9601
```

---

## 2. 技术栈

| 技术 | 说明 |
|---|---|
| Vue3 | 前端核心框架 |
| Vite | 前端构建工具 |
| Element Plus | UI 组件库 |
| Vue Router | 路由管理 |
| Pinia | 状态管理 |
| Axios | HTTP 请求 |
| SCSS/CSS | 页面样式 |
| JavaScript | 主要开发语言 |

---

## 3. 项目目录结构

```text
Gym-front
├── public
├── src
│   ├── api                 # 前台接口封装
│   │   ├── auth.js          # 登录、注册、用户信息
│   │   ├── venues.js        # 场馆列表、收藏、访问量
│   │   ├── court.js         # 场地详情、场地预约
│   │   ├── orders.js        # 我的预约订单、取消预约
│   │   ├── notice.js        # 公告列表、公告详情、公告评论
│   │   ├── profile.js       # 个人信息、余额、充值
│   │   ├── shopping.js      # 商品、购物车、商城订单
│   │   ├── venueComment.js  # 场馆评论
│   │   └── sign.js          # 签到相关
│   ├── assets              # 静态资源
│   ├── components          # 通用组件
│   ├── router              # 路由配置
│   ├── stores              # Pinia 状态管理
│   ├── utils               # 工具类、axios 封装
│   ├── views               # 页面
│   ├── App.vue
│   └── main.js
├── .env.development
├── vite.config.js
├── package.json
└── README.md
```

---

## 4. 当前功能模块

### 4.1 用户认证模块

- 用户登录
- 用户注册
- 验证码获取
- 获取当前登录用户信息
- token 持久化
- 未登录自动跳转登录页

对应接口文件：

```text
src/api/auth.js
```

主要接口：

```text
POST /front/login
GET  /front/generateValidateCode
GET  /front/getUserInfo
POST /front/register
```

---

### 4.2 场馆模块

- 场馆列表
- 热门场馆
- 场馆详情
- 场馆访问量记录
- 场馆收藏
- 取消收藏
- 我的收藏列表

对应接口文件：

```text
src/api/venues.js
```

主要接口：

```text
POST   /front/venues
POST   /front/getVenueByRate
POST   /front/venues/visit/{venueId}
GET    /front/venues/collect/list
POST   /front/venues/collect/{venueId}
DELETE /front/venues/unCollect/{venueId}
```

---

### 4.3 场地与预约模块

- 查询某个场馆下的场地
- 查看场地详情
- 提交场地预约
- 按日期和时段预约

对应接口文件：

```text
src/api/court.js
```

主要接口：

```text
GET  /front/venues/court/{venueId}
GET  /front/venues/details/{courtId}
POST /front/venues/book
```

预约提交数据示例：

```json
{
  "venueId": 1,
  "courtId": 2,
  "bookingDate": "2026-04-28",
  "startTime": "19:00:00",
  "endTime": "20:00:00",
  "remark": "用户预约羽毛球场"
}
```

---

### 4.4 我的预约订单模块

- 查询当前用户的预约订单
- 取消预约订单

对应接口文件：

```text
src/api/orders.js
```

主要接口：

```text
GET  /front/order/my/{pageNum}/{pageSize}
POST /front/order/cancel/{orderId}
```

说明：

前台不再通过 URL 传递 `userId` 查询订单，后端应该根据 token 获取当前登录用户，避免用户通过修改 URL 查询其他用户订单。

---

### 4.5 公告模块

- 公告列表
- 公告详情
- 公告评论

对应接口文件：

```text
src/api/notice.js
```

主要接口：

```text
GET  /front/notice
GET  /front/notice/{noticeId}
POST /front/notice/comment
```

---

### 4.6 个人中心模块

- 修改个人资料
- 查询余额
- 用户充值

对应接口文件：

```text
src/api/profile.js
```

主要接口：

```text
POST /front/updateProfile
GET  /front/getBalance
POST /front/recharge
```

说明：

余额查询不建议由前端传递 `userId`，后端应该根据 token 获取当前登录用户。

---

### 4.7 商城模块

- 商品列表
- 商品详情
- 购物车列表
- 添加购物车
- 修改购物车数量
- 删除购物车商品
- 清空购物车
- 创建商城订单
- 查询商城订单列表

对应接口文件：

```text
src/api/shopping.js
```

主要接口：

```text
GET    /front/goods/list
GET    /front/goods/detail/{id}
GET    /front/cart/list
POST   /front/cart/add
PUT    /front/cart/update
DELETE /front/cart/delete/{id}
POST   /front/cart/clear
POST   /front/shoppingOrder/create
GET    /front/shoppingOrder/list
```

后续可继续补充：

```text
GET  /front/shoppingOrder/detail/{id}
PUT  /front/shoppingOrder/cancel/{id}
POST /front/shoppingOrder/pay/{id}
```

---

### 4.8 场馆评论模块

- 分页查询场馆评论
- 提交场馆评论

对应接口文件：

```text
src/api/venueComment.js
```

主要接口：

```text
POST /front/venues/findByPageComment/{venueId}/{pageNum}/{pageSize}
POST /front/venues/saveVenueComment
```

---

### 4.9 签到模块

- 用户签到
- 查询签到记录
- 查询签到状态

对应接口文件：

```text
src/api/sign.js
```

---

### 4.10 智能 Agent 模块

前台后续可接入智能 Agent，实现：

- 查询场馆
- 查询公告
- 查询我的预约
- 查询我的订单
- 查询可预约时段
- 生成预约草稿
- 用户确认后执行预约
- 查询商品
- 生成商品下单草稿
- 用户确认后执行下单
- 场馆知识库问答

建议接口路径：

```text
POST /front/agent/chat
```

---

## 5. 环境要求

| 环境 | 版本建议 |
|---|---|
| Node.js | 16+ |
| npm | 8+ |
| Vue | 3.x |
| Vite | 4.x 或项目当前版本 |
| 后端服务 | Spring Boot 后端，默认端口 9601 |

---

## 6. 本地启动

### 6.1 安装依赖

```bash
npm install
```

### 6.2 启动开发环境

```bash
npm run dev
```

启动成功后访问：

```text
http://localhost:3002
```

如果你的 Vite 端口不是 3002，以控制台输出为准。

---

## 7. 环境变量配置

开发环境配置文件：

```text
.env.development
```

推荐内容：

```env
VITE_API_BASE_URL=/api
```

---

## 8. Vite 代理配置

在 `vite.config.js` 中配置代理：

```js
server: {
  port: 3002,
  proxy: {
    '/api': {
      target: 'http://localhost:9601',
      changeOrigin: true,
      rewrite: path => path.replace(/^\/api/, '')
    }
  }
}
```

这样前端请求：

```text
/api/front/login
```

会被代理到：

```text
http://localhost:9601/front/login
```