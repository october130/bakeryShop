# 🍰 烘焙蛋糕定制平台

一个基于 Spring Boot 4 + Vue 3 的全栈烘焙电商平台，支持蛋糕在线定制、智能 AI 推荐、优惠券秒杀、订单全生命周期管理等功能。

## 技术栈

### 后端
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.0 | 核心框架 |
| Java | 21 | LTS 版本 |
| Spring AI | 2.0.0 | AI 智能推荐（RAG + Function Calling） |
| MyBatis-Plus | 3.5.15 | ORM 框架（boot4-starter） |
| Redis | - | 缓存 + 购物车存储 |
| Redisson | - | 分布式锁（优惠券防超卖） |
| RabbitMQ | - | 消息队列（订单超时自动取消） |
| WebSocket | - | 实时通知推送 |
| JWT | - | 无状态认证 |
| SpringDoc | - | API 文档自动生成 |

### 前端
| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |
| Pinia | 状态管理 |
| Vue Router | 路由管理 |
| Axios | HTTP 请求 |
| SCSS | 样式预处理 |

## 项目模块

```
com.cake.platform
├── common/          # 公共模块：配置、异常、工具类、拦截器、AOP
├── user/            # 用户模块：注册、登录、JWT 认证
├── bakeryShop/      # 烘焙店模块：店铺信息、分类、LBS 距离排序
├── cake/            # 蛋糕模块：蛋糕 CRUD、定制字段
├── shoppingcar/     # 购物车模块：Redis 实现，支持批量操作
├── coupon/          # 优惠券模块：Redisson 分布式锁防超卖
├── order/           # 订单模块：完整状态机、RabbitMQ 延迟队列
├── ai/              # AI 模块：Spring AI 智能推荐（RAG + Function Calling）
└── review/          # 评价模块：用户评价、商家回复、评分缓存
```

## 核心功能

### 用户端
- **注册登录**：手机号 + 密码，JWT 无状态认证
- **蛋糕浏览**：按分类筛选、LBS 就近店铺排序
- **在线定制**：选择尺寸、口味、祝福语等定制选项
- **购物车**：Redis 高性能存储，支持增删改查、批量结算
- **订单管理**：完整订单状态流转（待支付→已支付→制作中→配送中→已完成→已取消）
- **优惠券**：限时秒杀，Redisson 分布式锁保证不超卖
- **评价系统**：对已完成订单打分评价，查看商家回复
- **AI 助手**：智能蛋糕推荐，支持自然语言对话

### 商家端
- **订单处理**：接单、发货、完成操作
- **评价管理**：回复用户评价、隐藏不当评价

### 技术亮点
- **AI 智能推荐**：Spring AI 2.0 + RAG 检索增强 + Function Calling 动态查库 + Structured Output 结构化返回
- **RabbitMQ 延迟队列**：订单 15 分钟未支付自动取消，替代定时任务轮询
- **Redisson 分布式锁**：Lua 脚本保证原子性，优惠券秒杀不超卖
- **Redis 购物车**：Hash 结构存储，支持批量操作，性能优异
- **完整订单状态机**：6 种状态流转，有延迟队列兜底
- **LBS 距离排序**：Haversine 公式计算用户与店铺距离

## 项目结构

```
bakeryShop/
├── src/main/java/com/cake/platform/
│   ├── BakeryShopApplication.java    # 启动类
│   ├── common/                        # 公共模块
│   │   ├── config/                    # 配置类（Redis、Redisson、RabbitMQ、MVC）
│   │   ├── exception/                 # 全局异常处理
│   │   ├── interceptor/               # JWT 认证拦截器
│   │   ├── result/                    # 统一响应封装
│   │   └── utils/                     # 工具类（JWT、用户ID）
│   ├── user/                          # 用户模块
│   ├── bakeryShop/                    # 烘焙店模块
│   ├── cake/                          # 蛋糕模块
│   ├── shoppingcar/                   # 购物车模块
│   ├── coupon/                        # 优惠券模块
│   ├── order/                         # 订单模块
│   │   └── mq/                        # RabbitMQ 消费者（延迟队列）
│   ├── ai/                            # AI 推荐模块
│   │   ├── service/                   # AI 服务
│   │   ├── tool/                      # Function Calling 工具
│   │   └── chatMemory/               # 聊天记忆配置
│   └── review/                        # 评价模块
├── src/main/resources/
│   ├── application.yml                # 应用配置
│   └── knowledge/                     # AI 知识库（RAG）
└── frontend/                          # Vue 3 前端项目
    ├── src/
    │   ├── api/                       # API 接口封装
    │   ├── views/                     # 页面组件
    │   ├── components/                # 公共组件
    │   ├── stores/                    # Pinia 状态管理
    │   └── router/                    # 路由配置
    └── package.json
```

## 快速开始

### 环境要求
- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.0+
- RabbitMQ 3.12+
- Node.js 18+

### 后端启动

1. 克隆项目
```bash
git clone https://github.com/october130/bakeryShop.git
cd bakeryShop
```

2. 初始化数据库
```sql
-- 创建数据库
CREATE DATABASE bakery_shop DEFAULT CHARACTER SET utf8mb4;
-- 导入 SQL 文件（如有）
```

3. 修改配置
编辑 `src/main/resources/application.yml`，配置数据库、Redis、RabbitMQ 连接信息。

4. 启动项目
```bash
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8082`

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端开发服务器运行在 `http://localhost:5173`

## API 文档

项目集成了 SpringDoc，启动后访问：
- Swagger UI：`http://localhost:8082/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8082/v3/api-docs`

详细接口文档见 [API.md](./API.md)

## 开发计划

- [x] 基础设施搭建（配置、异常处理、认证）
- [x] 用户模块
- [x] 烘焙店模块
- [x] 蛋糕模块
- [x] 购物车模块
- [x] 优惠券模块
- [x] 订单模块 + RabbitMQ 延迟队列
- [x] 商家端接口
- [x] AI 智能推荐模块
- [x] 评价模块
- [x] Vue 前端
- [ ] 微服务架构升级（进行中）

## 许可证

MIT License
