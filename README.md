# Gym 体育场馆预约平台

## 1. 项目介绍

Gym 是一个基于 Spring Boot 3 + Vue3 + Agent的体育场馆预约平台，包含用户前台、后台管理系统和智能 Agent 服务。

项目核心业务包括：场馆浏览、场地预约、订单管理、评论收藏、公告管理、后台运营管理、邮箱通知、智能问答与 Agent 工具调用。

本项目的目标不是简单实现 CRUD，而是围绕真实预约业务构建完整后端主链，并进一步接入 Java + AI 应用能力，例如 Agent 工具调用、RAG 知识库问答、智能预约草稿生成等。

## 2. 项目亮点

- 前后端分离架构，包含用户前台和后台管理端
- 基于 Spring Boot 3 + MyBatis 构建后端业务系统
- 支持场馆、场地、预约、订单、公告、评论、收藏等核心业务
- 接入 Redis，用于缓存、验证码、登录状态等场景
- 接入 MinIO，用于图片和文件存储
- 接入邮箱通知能力
- 引入 LangChain4j，构建体育场馆智能 Agent
- 规划使用 pgvector 构建 RAG 知识库
- 规划二维码核销、预约状态机、支付流水、退款流水、后台统计看板

## 3. 技术栈

### 后端

- Java 17
- Spring Boot 3.0.5
- Spring MVC
- MyBatis
- MySQL 8
- Redis 7
- MinIO
- WebSocket
- Mail
- EasyExcel
- LangChain4j
- pgvector
- ZXing
- Maven 多模块

### 前端

- Vue3
- Element Plus
- Vite
- Vue Router
- Axios
- Pinia / Store

### 工程化

- Maven
- Docker
- Git
- GitHub

## 4. 系统角色

### 普通用户

- 注册登录
- 浏览场馆
- 查看公告
- 预约场地
- 查看我的预约
- 查看我的订单
- 评论和收藏
- 使用 Agent 查询场馆与预约信息

### 管理员

- 用户管理
- 场馆管理
- 场地管理
- 预约管理
- 订单管理
- 公告管理
- 评论管理
- 运营数据统计

### 智能 Agent

- 查询场馆
- 查询公告
- 查询可预约时段
- 查询我的预约
- 查询我的订单
- 生成预约草稿
- 用户确认后执行预约
- 后续接入 RAG 知识库问答

## 5. 功能模块

| 模块 | 说明 |
|---|---|
| 用户模块 | 登录、注册、个人信息 |
| 场馆模块 | 场馆列表、详情、状态管理 |
| 场地模块 | 场地管理、价格、容量、状态 |
| 预约模块 | 按日期和时段预约、取消、改签 |
| 订单模块 | 预约订单、商城订单、订单状态流转 |
| 支付模块 | 模拟支付、支付流水、退款流水 |
| 公告模块 | 公告发布、公告展示、公告分类 |
| 评论收藏 | 用户评论、收藏场馆 |
| 通知模块 | 预约通知、取消通知、退款通知 |
| Agent 模块 | 智能查询、预约草稿、工具调用 |
| RAG 模块 | 场馆知识库问答，规划中 |

## 6. 项目结构

```text
Gym
├── Gym-parent                  # 后端父工程
│   ├── Gym-admin               # 后端启动模块与业务接口
│   ├── Gym-Model               # 实体类、DTO、VO
│   └── Gym-common              # 公共模块
│       ├── common-log          # 日志相关
│       ├── common-service      # 通用服务
│       └── common-util         # 工具类
├── front
│   └── Gym-admin
│       ├── Gym-admin           # 后台管理端
│       └── Gym-front           # 用户前台端
├── db
│   └── gym.sql                 # 数据库初始化脚本
├── README.md
└── .gitignore

```

## 7.部署项目
环境需求:JDK 17、Maven 3.8+、MySQL 8、Redis 7、Node.js 16+<br>
1.先执行初始化数据库<br>
初始化数据库 先执行：db/gym.sql

2.修改配置文件
修改Gym-parent/Gym-admin/src/main/resources/application.yml改成自己的配置环境<br>
并新建一个本地的配置文件存放重要的配置数据Gym-parent/Gym-admin/src/main/resources/application-local.yml<br>
<img width="532" height="402" alt="image" src="https://github.com/user-attachments/assets/783054f6-85ae-4ee0-a27a-14f09c46cd34" />

3.启动后端
Gym-parent/Gym-admin/src/main/java/com/sau/gym/admin/AdminApplication.java<br>
后端端口在9601

4.启动前端
```shell
后台管理端
cd front/Gym-admin/Gym-admin
npm install
npm run dev
后台管理端口在3001

用户前台端
cd front/Gym-admin/Gym-front
npm install
npm run dev
用户前台端口在3002
```

## 8.总结
本项目的起源是想着学习java后端方向以及前端，项目背景是本人除了编写代码的其他爱好就是健身及各种运动才有感而生想到的项目背景，我是在学完了各种技术以及做了四个项目前提下，自己想着动手搭建从0到1的一个项目，期间会搭配着各种AI以及大佬的博客解决项目中遇到的各种难题。我觉得该项目可以够一个小白跟着视频学完，想完全自学的一个项目，你看完我的或者别人的，感觉都会有点灵感，虽然这只是一个SpringBoot+Vue的一个前后端分离项目，放在现在的AI大背景下可能不是很够看的一个项目，但我觉得要学就要大胆去做，而不是畏手畏脚不敢去做，如果有更好的意见，恳请各位指点。回想起来，自己也是刚开始从java的输入System.out.println();都会打错，到现在也慢慢进步，也能慢慢给一些同学建议。当你没那么在意未来，当下最重要，也许新学的技术会更新迭代的很快，也许有一天AI会代替程序员，也许现在都充斥着入行Java就是1949年入国军，但是我觉得不要被这些给吓到了而去焦虑未来，既然选择了这条路，不清楚是不是最好的，那就坚信自己的选择，相信自己的选择是最正确的选择。最后，接受各个大佬的指点以及互相讨论学习也可，谢谢大家。
持续更新ing...
