## 一、项目背景

后台的Gym-admin系统一个Vue3 Element Admin的脚手架，脚手架链接:https://github.com/youlaitech/vue3-element-admin

**Vue3 Element Admin** 是一个免费开源的中后台模版。基于`vue3`+`ElementPlus`+`Vite`开发，是一个开箱即用的中后台系统前端解决方案，它可以帮助你快速搭建企业级中后台产品原型。

## 二、技术背景

前端技术:Vue3+Element Plus+Vite+Vue Router

后端技术:Java+SprinBoot+Mybatis+Mysql+redis+minio+docker+短信验证服务(现在需要公司认证)

## 三、环境需求

node版本为16.19.0
<img width="409" height="156" alt="image" src="https://github.com/user-attachments/assets/e37cb8c6-e48c-41ae-99dd-749154e7f42c" />


SpringBoot为2.6.13版本以及java17、redis7、mysql8、maven版本为3.8.3

## 四、环境部署

### 4.1 Vue3 Element Admin部署

```shell
# Vue3-Element-Admin 要求 Node.js 版本 >= 12 ，推荐Node.js  16.x版本

# 使用git克隆项目 或者 直接下载项目
git clone https://github.com/youlaitech/vue3-element-admin.git

# 进入项目目录
cd vue3-element-admin

# 安装依赖
npm install

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
npm install --registry=https://registry.npm.taobao.org

# 启动服务
npm start
```

![image-20260326144347597](C:\Users\86138\AppData\Roaming\Typora\typora-user-images\image-20260326144347597.png)

---



<img src="C:\Users\86138\AppData\Roaming\Typora\typora-user-images\image-20260326144251775.png" alt="image-20260326144251775" style="zoom:150%;" />



-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

<img src="C:\Users\86138\AppData\Roaming\Typora\typora-user-images\image-20260326144422422.png" alt="image-20260326144422422" style="zoom:150%;" />



---



### 4.2 项目目录介绍

```java
mock					// 用于测试，模拟后端接口地址
public					// 存储公共的静态资源：图片
src						// 源代码目录，非常重要
    | api				// 提供用于请求后端接口的js文件
    | assets			// 存储静态资源：图片、css
    | components		// 存储公共组件,可重用的一些组件
    | directive			// 存储自定义的一些指令
    | hooks				// 存储自定义的钩子函数
    | i18n				// 存储用于国际化的js文件
    | layout			// 存储首页布局组件
    | pinia				// 用于进行全局状态管理
    | router			// 存储用于进行路由的js文件
    | utils				// 存储工具类的js文件
    | views				// 和路由绑定的组件
    | App.vue			// 根组件
    | default-settings.js // 默认设置的js文件
    | error-log.js		// 错误日志js文件
    | global-components.js // 全局组件的js文件
    | main.js			// 入口js文件(非常重要)
    | permission.js		// 权限相关的js文件(路由前置守卫、路由后置守卫)
vite.config.js			// vite的配置文件，可以在该配置文件中配置前端工程的端口号
```



![image-20260326144038932](C:\Users\86138\AppData\Roaming\Typora\typora-user-images\image-20260326144038932.png)

---



![image-20260326144903142](C:\Users\86138\AppData\Roaming\Typora\typora-user-images\image-20260326144903142.png)
