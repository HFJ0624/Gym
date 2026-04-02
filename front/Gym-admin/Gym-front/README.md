# gym-front（体育场馆预约系统 - 前台）

## 本地启动

在 `d:\workspace\Gym-admin\Gym-front` 目录执行：

```bash
npm i
npm run dev
```

默认端口 `3002`。由于本项目按“同域子路径部署”配置了 `base=/front/`，本地开发请访问：

- `http://localhost:3002/front/`

## 目录说明

- `src/router/index.js`
  - 使用 `history` 路由，base 为 `/front`
  - 除 `/login` 外，其余页面都需要前台 token（未登录会跳转登录页）
- `src/utils/frontRequest.js`
  - 前台专用 axios 实例
  - 自动携带请求头 `token`（与你后端一致）
- `src/api/auth.js`
  - 登录与验证码接口封装
  - 你后端返回 `Result<LoginVo>`，token 在 `data.token`
- `src/api/venues.js`、`src/api/orders.js`
  - 目前是 mock（死数据 + localStorage）
  - 对接后端时，把 mock 替换为 `/front/**` 的真实接口即可

## 生产部署（Nginx 未配置前先跳过）

本项目按 `/front/` 子路径部署设计，需要 Nginx 做 history 回退重写（否则刷新会 404）：

```nginx
location /front/ {
  alias /var/www/gym-front/;
  try_files $uri $uri/ /front/index.html;
}
```

