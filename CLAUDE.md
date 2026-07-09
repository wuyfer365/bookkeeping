# 记账APP — 项目协作规范

> **核心规则**：开发中遇到非微小技术决策，必须列出 ≥2 个方案并解释优劣，由用户拍板，决策结果记录回本文档的「决策日志」章节。

---

## 1. 产品概述

| 项目 | 说明 |
|------|------|
| **名称** | 记账APP |
| **定位** | Windows 桌面端个人记账工具 |
| **核心功能** | 记录每笔人民币花销，按两级分类管理，支持月度统计与趋势查看 |
| **用户** | 个人用户，本地使用 |
| **创建日期** | 2026-07-09 |

---

## 2. 技术栈

| 层级 | 选型 | 版本 | 说明 |
|------|------|------|------|
| 后端框架 | Spring Boot | 3.2.x | 生态成熟 |
| ORM | MyBatis (XML mapper) | 3.0.x | SQL 灵活可控 |
| 数据库 | SQLite | 3.x | 零配置单文件，适合本地桌面应用 |
| JDBC 驱动 | org.xerial:sqlite-jdbc | 3.44.x | SQLite 官方 JDBC |
| 前端框架 | Vue 3 | 3.4.x | Composition API + `<script setup>` |
| UI 组件库 | Element Plus | 2.4.x | Vue 3 版 Element UI，中文友好 |
| 图表库 | ECharts + vue-echarts | 5.x / 6.x | 饼图/折线图/柱状图 |
| 构建工具(前端) | Vite | 5.x | Vue 3 官方推荐 |
| 构建工具(后端) | Maven | 3.8+ | |
| 语言(后端) | Java 17 | LTS | Spring Boot 3.x 最低要求 |
| 语言(前端) | JavaScript (ES Modules) | | |
| 运行平台 | Windows | | |

---

## 3. 架构

**前后端分离**：
- 开发时：后端 `localhost:8080`，前端 `localhost:5173`，Vite 代理 `/api` → `:8080`
- 生产（后续）：前端打包静态文件，由 Spring Boot 或 Nginx 统一 serve

**分层架构（后端）**：
```
Controller → Service → Mapper → SQLite
     ↑ DTO/VO  ←  Entity  ←  XML
```

---

## 4. 花销分类体系（2级）

单表 `category` + `parent_id` 自引用实现。花销记录只能引用二级分类（叶子节点）。

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

---

## 5. 数据库设计

**文件位置**：`backend/data/bookkeeping.db`（首次启动自动创建）

### 5.1 categories 分类表

```sql
CREATE TABLE IF NOT EXISTS categories (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(50)  NOT NULL,
    parent_id   INTEGER      DEFAULT NULL,          -- NULL=一级, 非NULL=二级
    level       TINYINT      NOT NULL CHECK(level IN (1, 2)),
    sort_order  INTEGER      DEFAULT 0,
    created_at  DATETIME     DEFAULT (datetime('now', 'localtime')),
    updated_at  DATETIME     DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
);
CREATE INDEX idx_categories_parent ON categories(parent_id);
```

### 5.2 expenses 花销表

```sql
CREATE TABLE IF NOT EXISTS expenses (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    amount       NUMERIC(10,2) NOT NULL CHECK(amount > 0),
    category_id  INTEGER       NOT NULL,            -- 必须引用二级分类
    expense_date DATE          NOT NULL,
    description  VARCHAR(500),
    created_at   DATETIME      DEFAULT (datetime('now', 'localtime')),
    updated_at   DATETIME      DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
CREATE INDEX idx_expenses_date     ON expenses(expense_date);
CREATE INDEX idx_expenses_category ON expenses(category_id);
```

### 5.3 SQLite 注意事项

- **外键**：JDBC URL 需加 `?foreign_keys=true`，否则 CASCADE 不生效
- **默认时间**：用 `datetime('now','localtime')`，**不是** MySQL 的 `NOW()`
- **无 DECIMAL**：用 `NUMERIC(10,2)`（SQLite 的 NUMERIC affinity），Java 侧用 `BigDecimal`
- **无 BOOLEAN**：如需要，用 INTEGER 0/1
- **并发**：文件级锁，单人桌面应用无影响

---

## 6. API 设计

- 统一前缀：`/api`
- 统一响应：`{ "code": 200, "message": "success", "data": {...} }`
- 分页响应：`{ "code": 200, "message": "success", "data": { "records": [...], "total": 100, "page": 1, "pageSize": 20 } }`

### 6.1 分类 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/categories` | 获取分类树（一级含 children 二级数组） |
| POST | `/api/categories` | 新建分类 `{ "name", "parentId?", "level", "sortOrder" }` |
| PUT | `/api/categories/{id}` | 修改分类名称 |
| DELETE | `/api/categories/{id}` | 删除分类（级联删除子分类和关联花销） |

### 6.2 花销 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/expenses` | 分页列表 `?page=1&size=20&startDate=&endDate=&categoryId=` |
| GET | `/api/expenses/{id}` | 单条详情 |
| POST | `/api/expenses` | 新增 `{ "amount", "categoryId", "expenseDate", "description" }` |
| PUT | `/api/expenses/{id}` | 修改 |
| DELETE | `/api/expenses/{id}` | 删除 |

### 6.3 统计 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/statistics/summary` | 汇总 `?startDate=&endDate=` → `{ totalAmount, recordCount, avgPerDay }` |
| GET | `/api/statistics/by-category` | 按一级分类统计 `?startDate=&endDate=` |
| GET | `/api/statistics/trend` | 趋势 `?months=6` → 每月总支出 |

### 6.4 业务规则

1. 花销 `category_id` 必须引用**二级分类**（parent_id IS NOT NULL）
2. `amount` > 0 且 ≤ 99999999.99
3. `expense_date` 不能是未来日期
4. 删除一级分类 → 级联删除子分类 + 关联花销
5. 删除二级分类 → 级联删除关联花销

---

## 7. 项目目录结构

```
记账APP/
├── CLAUDE.md                         ← 本文档（项目唯一权威信息来源）
├── backend/                          ← SpringBoot 后端
│   ├── pom.xml
│   ├── data/                         ← SQLite 数据库文件目录（自动生成）
│   └── src/main/
│       ├── java/com/bookkeeping/
│       │   ├── BookkeepingApplication.java
│       │   ├── controller/
│       │   │   ├── CategoryController.java
│       │   │   ├── ExpenseController.java
│       │   │   └── StatisticsController.java
│       │   ├── service/
│       │   │   ├── CategoryService.java
│       │   │   ├── ExpenseService.java
│       │   │   ├── StatisticsService.java
│       │   │   └── impl/
│       │   ├── mapper/
│       │   │   ├── CategoryMapper.java
│       │   │   └── ExpenseMapper.java
│       │   ├── entity/
│       │   │   ├── Category.java
│       │   │   └── Expense.java
│       │   ├── dto/
│       │   │   ├── CategoryTreeDTO.java
│       │   │   ├── ExpenseCreateDTO.java
│       │   │   ├── ExpenseQueryDTO.java
│       │   │   ├── ExpenseVO.java
│       │   │   └── StatisticsVO.java
│       │   └── common/
│       │       ├── Result.java
│       │       └── PageResult.java
│       └── resources/
│           ├── application.yml
│           └── db/
│               ├── schema.sql
│               └── data.sql
└── frontend/                         ← Vue 3 前端
    ├── package.json
    ├── vite.config.ts
    ├── index.html
    └── src/
        ├── main.ts
        ├── App.vue
        ├── router/index.ts
        ├── api/
        │   ├── request.ts
        │   ├── category.ts
        │   ├── expense.ts
        │   └── statistics.ts
        ├── views/
        │   ├── Dashboard.vue
        │   ├── ExpenseList.vue
        │   ├── ExpenseForm.vue
        │   └── Statistics.vue
        ├── components/
        │   ├── AppLayout.vue
        │   ├── ExpenseTable.vue
        │   ├── CategoryCascader.vue
        │   └── StatChart.vue
        └── utils/
            ├── format.ts
            └── request.ts
```

---

## 8. 开发命令

### 后端启动（Windows）
```bash
cd backend
mvnw spring-boot:run
# → http://localhost:8080
```

### 前端启动（Windows）
```bash
cd frontend
npm install        # 仅首次
npm run dev
# → http://localhost:5173（自动代理 /api → :8080）
```

### 查看数据库
```bash
sqlite3 backend/data/bookkeeping.db ".tables"
sqlite3 backend/data/bookkeeping.db "SELECT * FROM categories;"
```

---

## 9. 开发约定

1. **前端**：Composition API + `<script setup>`。UI 全部用 Element Plus。图表用 vue-echarts。
2. **后端**：Controller → Service → Mapper 三层。DTO 入参、VO 出参。`@Validated` 校验入参。日期统一 `LocalDate`。
3. **金额显示**：始终带 `¥` 前缀和 2 位小数，使用 `formatRMB()` 工具函数。
4. **API 调用**：组件中只能通过 `api/` 模块调用，**禁止**直接写 axios。
5. **错误处理**：后端返回中文错误信息，前端拦截器自动 `ElMessage.error`。
6. **加载状态**：所有数据加载组件显示 `<el-skeleton>` 直到数据到达。

---

## 10. 决策日志

> 每当做出非微小技术决策时，记录到这里。格式：日期、决策内容、备选方案、选择理由。

### 2026-07-09 — 初始技术选型

| 决策 | 方案A（采用） | 方案B（未采用） | 理由 |
|------|-------------|----------------|------|
| 前端框架 | **Vue 3** | Vue 2 | 2026 年主流，Composition API，TypeScript 友好 |
| UI 库 | **Element Plus** | Naive UI / Ant Design Vue | Vue 3 版 Element UI，中文文档完善，组件最丰富 |
| 部署方式 | **前后端分离** | 整合到一个 JAR | 开发灵活，热更新快，可独立调试 |
| 分类存储 | **单表 + parent_id** | 两张独立表 | 结构更简洁，树查询方便 |
| MyBatis SQL | **XML mapper** | 注解 | 统计聚合 SQL 复杂，XML 更清晰 |
| 路由模式 | **Hash 模式** | History 模式 | 无需服务端配置，刷新不 404 |
| 统计计算 | **SQL 实时聚合** | 物化汇总表 | 个人小数据量，代码更简单 |

---

*本文档是项目的唯一权威信息来源。当有疑问时，优先查阅此处。*
