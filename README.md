# 记账APP

个人记账本 — Windows 桌面端记账工具，记录每笔人民币花销，按两级分类管理，支持月度统计与趋势查看。

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3.2.x | RESTful API |
| ORM | MyBatis 3.0.x | XML Mapper |
| 数据库 | SQLite 3.x | 零配置单文件 |
| 前端框架 | Vue 3.4.x | Composition API |
| UI 组件库 | Element Plus 2.4.x | 中文友好 |
| 图表 | ECharts 5.x + vue-echarts 6.x | 饼图/折线图/柱状图 |
| 构建 | Maven + Vite 5.x | 前后端分离 |

## 功能

- **花销管理** — 新增、编辑、删除花销记录（金额、分类、日期、备注）
- **两级分类** — 10 个一级大类 + 42 个二级小类，级联选择
- **仪表盘** — 本月汇总卡片 + 分类占比饼图 + 近 6 月趋势折线图
- **统计页** — 自定义日期范围、分类排行柱状图、月度趋势
- **筛选查询** — 按日期范围、分类筛选，分页浏览

## 花销分类

| 一级大类 | 二级小类 |
|----------|----------|
| 🍽️ 餐饮 | 早餐、午餐、晚餐、零食、饮品、聚餐 |
| 🚗 交通 | 公交/地铁、网约车、加油/充电、停车费、火车/高铁、飞机 |
| 🛒 购物 | 日用品、数码产品、家居用品、图书 |
| 👗 服饰 | 衣服、鞋帽、配饰、美容护肤 |
| 🏠 住房 | 房租/房贷、水电费、物业费、维修、装修 |
| 🎮 娱乐 | 电影、游戏、旅游、运动健身 |
| 💊 医疗 | 门诊、药品、体检、住院 |
| 📚 教育 | 培训课程、书籍资料、考试报名、子女教育 |
| 📡 通讯 | 话费、网费、快递 |
| 📦 其他 | 人情往来、捐赠、杂项 |

## 项目结构

```
记账APP/
├── CLAUDE.md              # 项目协作规范
├── backend/               # Spring Boot 后端
│   ├── pom.xml
│   ├── data/              # SQLite 数据库（自动生成）
│   └── src/main/
│       ├── java/com/bookkeeping/
│       │   ├── controller/
│       │   ├── service/
│       │   ├── mapper/
│       │   ├── entity/
│       │   ├── dto/
│       │   └── common/
│       └── resources/
│           ├── application.yml
│           ├── mapper/
│           └── db/
└── frontend/              # Vue 3 前端
    ├── package.json
    ├── vite.config.ts
    └── src/
        ├── api/           # Axios 封装
        ├── views/         # 页面组件
        ├── components/    # 通用组件
        ├── router/        # 路由
        └── utils/         # 工具函数
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+（项目自带 Maven Wrapper）

### 1. 启动后端

```bash
cd backend
# Windows PowerShell
.\run.ps1

# 或手动
mvnw spring-boot:run
```

后端运行在 http://localhost:8080

### 2. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端运行在 http://localhost:5173（自动代理 `/api` 到后端）

### 3. 测试 API

```bash
# 获取分类树
curl http://localhost:8080/api/categories

# 月度统计
curl "http://localhost:8080/api/statistics/summary?startDate=2026-07-01&endDate=2026-07-31"
```

## API 概览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/categories` | 获取分类树 |
| POST | `/api/categories` | 新建分类 |
| PUT | `/api/categories/{id}` | 修改分类 |
| DELETE | `/api/categories/{id}` | 删除分类 |
| GET | `/api/expenses` | 花销列表（分页+筛选） |
| POST | `/api/expenses` | 新增花销 |
| PUT | `/api/expenses/{id}` | 修改花销 |
| DELETE | `/api/expenses/{id}` | 删除花销 |
| GET | `/api/statistics/summary` | 汇总统计 |
| GET | `/api/statistics/by-category` | 按分类统计 |
| GET | `/api/statistics/trend` | 月度趋势 |

## License

MIT
