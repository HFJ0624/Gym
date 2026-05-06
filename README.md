# Gym 体育场馆预约平台

## 1. 项目介绍

Gym 是一个基于 Spring Boot 3 + Vue3 + LangChain4j Agent + RAG 的体育场馆预约平台，包含用户前台、后台管理系统和智能 Agent 服务。

项目核心业务包括：场馆浏览、场地预约、预约时段可视化、订单管理、评论收藏、公告管理、后台运营管理、邮箱通知、智能问答、RAG 知识库问答与 Agent 工具调用。

本项围绕真实体育场馆预约业务构建完整的后端主链路，并进一步接入 Java + AI 应用能力，例如 Agent 工具调用、RAG 知识库问答、智能预约草稿生成、前端确认按钮化、Agent 工具日志追踪、RAG 命中来源展示等。

目前项目已经从普通的体育场馆预约系统，进一步升级为一个带有智能 Agent 和 RAG 知识库能力的预约平台。用户既可以通过传统页面完成预约，也可以通过自然语言让 Agent 辅助查询场馆、生成预约草稿、确认预约、查询我的预约以及回答平台规则类问题。

## 2. 项目亮点

- 前后端分离架构，包含用户前台和后台管理端
- 基于 Spring Boot 3 + MyBatis 构建后端业务系统
- 支持场馆、场地、预约、订单、公告、评论、收藏等核心业务
- 支持按日期和时间段预约场地
- 支持同一场地同一时段预约冲突校验
- 支持预约时段可视化展示，用户可以直观看到可预约时段和已占用时段
- 接入 Redis，用于缓存、验证码、登录状态等场景
- 接入 MinIO，用于图片和文件存储
- 接入邮箱通知能力
- 引入 LangChain4j，构建体育场馆智能 Agent
- Agent 支持自然语言查询场馆、公告、我的预约等业务信息
- Agent 支持生成预约草稿，用户确认后再执行真实预约
- 前端支持预约草稿卡片化展示和确认按钮化
- 支持我的预约查询直达路由，提高常用业务查询的准确性和响应速度
- 接入 PostgreSQL + pgvector，构建 RAG 知识库问答能力
- RAG 支持命中来源展示，包括标题、标签、知识范围、相似度和命中文本片段
- RAG 支持单条知识增量索引，方便后台日常维护知识库
- 后台支持 Agent 工具日志查看，方便追踪 Agent 调用了什么工具、参数是什么、结果是什么
- 后续可继续扩展预约取消 / 退款 Agent 流程、多轮上下文增强、场馆别名解析、Agent 配置管理页等功能

## 3. 技术栈

### 后端

- Java 17
- Spring Boot 3.0.5
- Spring MVC
- MyBatis
- MySQL 8
- Redis 7
- PostgreSQL
- pgvector
- MinIO
- WebSocket
- Mail
- EasyExcel
- LangChain4j
- ZXing
- Maven 多模块

### 前端

- Vue3
- Element Plus
- Vite
- Vue Router
- Axios
- Pinia / Store
- ECharts

### AI 与 RAG

- LangChain4j Agent
- Tool Calling 工具调用
- OpenAI Compatible API
- Embedding 文本向量化
- PostgreSQL + pgvector 向量检索
- RAG 检索增强生成
- Agent 工具调用日志追踪
- RAG 命中来源展示
- 单条知识增量索引

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
- 查看场地详情
- 选择预约日期和时间段
- 查看预约时段状态
- 预约场地
- 查看我的预约
- 查看我的订单
- 评论和收藏
- 使用 Agent 查询场馆与预约信息
- 使用 Agent 生成预约草稿
- 点击确认按钮完成预约
- 使用 RAG 问答查询平台规则、预约流程、场馆公告等信息

### 管理员

- 用户管理
- 场馆管理
- 场地管理
- 预约管理
- 订单管理
- 公告管理
- 评论管理
- RAG 知识库管理
- 单条知识增量索引
- Agent 工具日志管理
- 运营数据统计

### 智能 Agent

- 查询场馆
- 查询公告
- 查询可预约时段
- 查询我的预约
- 查询我的订单
- 生成预约草稿
- 返回预约草稿卡片
- 用户确认后执行预约
- 调用 RAG 知识库回答规则类问题
- 返回 RAG 命中来源
- 记录工具调用日志，方便后台追踪和调试

## 5. 功能模块

| 模块 | 说明 |
|---|---|
| 用户模块 | 登录、注册、个人信息 |
| 场馆模块 | 场馆列表、详情、状态管理、图片展示 |
| 场地模块 | 场地管理、场地类型、价格、容量、状态 |
| 预约模块 | 按日期和时段预约、预约冲突校验、我的预约查询 |
| 订单模块 | 预约订单、商城订单、订单状态流转 |
| 支付模块 | 模拟支付、支付流水、退款流水 |
| 公告模块 | 公告发布、公告展示、公告分类 |
| 评论收藏 | 用户评论、收藏场馆 |
| 通知模块 | 预约通知、取消通知、退款通知、邮箱通知 |
| Agent 模块 | 智能查询、预约草稿、确认预约、工具调用 |
| RAG 模块 | 知识库问答、向量检索、来源展示、单条知识增量索引 |
| 工具日志模块 | Agent 工具调用记录、traceId 追踪、调用参数和结果查看 |
| 可视化模块 | 预约时段可视化、后台统计图表 |

## 6. 项目结构

```text
Gym
├── Gym-parent                  # 后端父工程
│   ├── Gym-admin               # 后端启动模块与业务接口
│   │   ├── src/main/java/com/sau/gym/admin
│   │   │   ├── agent           # Agent 相关代码
│   │   │   ├── controller      # 接口层
│   │   │   ├── mapper          # MyBatis Mapper
│   │   │   ├── rag             # RAG 知识库模块
│   │   │   ├── service         # 业务服务
│   │   │   └── AdminApplication.java
│   │   └── src/main/resources  # 配置文件
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
