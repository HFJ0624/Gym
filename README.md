## 一、项目背景

该项目是一个高校及各大体育馆的体育场馆预约平台,其功能主要是用户可以登录预约场地、发布评论以及后台管理系统等等，是一个的SpringBoot+Vue的项目。

后台的Gym-admin系统一个Vue3 Element Admin的脚手架，脚手架链接:https://github.com/youlaitech/vue3-element-admin


## 二、技术背景

前端技术:Vue3+Element Plus+Vite+Vue Router

后端技术:Java+SprinBoot+Mybatis+Mysql+redis+minio+docker+短信验证服务(现在需要公司认证)+邮箱推送功能

## 三、环境需求及部署

node版本为16.19.0

SpringBoot为2.6.13版本以及java17、redis7、mysql8、maven版本为3.8.3

```shell
# Vue3-Element-Admin 要求 Node.js 版本 >= 12 ，推荐Node.js  16.x版本
git clone 该项目

# 建议不要直接使用 cnpm 安装依赖，会有各种诡异的 bug。可以通过如下操作解决 npm 下载速度慢的问题
# 前台系统 启动在localhost:3002/
npm install --registry=https://registry.npm.taobao.org
npm run dev

# 管理员系统 启动在localhost:3001/
npm install --registry=https://registry.npm.taobao.org
npm start

# 后端点击AdminApplication启动类启动 启动在localhost:9601/
```

## 四、后端框架结构
<img width="1331" height="747" alt="image" src="https://github.com/user-attachments/assets/e27a1cdc-7ff5-4514-99b0-27efb3ca3d68" />

## 五、开发心得

本项目的起源是想着学习java后端方向以及前端，项目背景是本人除了编写代码的其他爱好就是健身及各种运动才有感而生想到的项目背景，我是在学完了各种技术以及做了四个项目前提下，自己想着动手搭建从0到1的一个项目，期间会搭配着各种AI以及大佬的博客解决项目中遇到的各种难题。我觉得该项目可以够一个小白跟着视频学完，想完全自学的一个项目，你看完我的或者别人的，感觉都会有点灵感，虽然这只是一个SpringBoot+Vue的一个前后端分离项目，放在现在的AI大背景下可能不是很够看的一个项目，但我觉得要学就要大胆去做，而不是畏手畏脚不敢去做，如果有更好的意见，恳请各位指点。回想起来，自己也是刚开始从java的输入System.out.println();都会打错，到现在也慢慢进步，也能慢慢给一些同学建议。当你没那么在意未来，当下最重要，也许新学的技术会更新迭代的很快，也许有一天AI会代替程序员，也许现在都充斥着入行Java就是1949年入国军，但是我觉得不要被这些给吓到了而去焦虑未来，既然选择了这条路，不清楚是不是最好的，那就坚信自己的选择，相信自己的选择是最正确的选择。最后，接受各个大佬的指点以及互相讨论学习也可，谢谢大家。
持续更新ing...
