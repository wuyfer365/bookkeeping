-- 记账APP 数据库建表脚本 (SQLite)
-- 每次启动时执行 CREATE TABLE IF NOT EXISTS

-- 分类表（自引用实现两级分类）
CREATE TABLE IF NOT EXISTS categories (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR(50)  NOT NULL,
    parent_id   INTEGER      DEFAULT NULL,
    level       TINYINT      NOT NULL CHECK(level IN (1, 2)),
    sort_order  INTEGER      DEFAULT 0,
    created_at  DATETIME     DEFAULT (datetime('now', 'localtime')),
    updated_at  DATETIME     DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id);

-- 花销记录表
CREATE TABLE IF NOT EXISTS expenses (
    id           INTEGER PRIMARY KEY AUTOINCREMENT,
    amount       NUMERIC(10,2) NOT NULL CHECK(amount > 0),
    category_id  INTEGER       NOT NULL,
    expense_date DATE          NOT NULL,
    description  VARCHAR(500),
    created_at   DATETIME      DEFAULT (datetime('now', 'localtime')),
    updated_at   DATETIME      DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE INDEX IF NOT EXISTS idx_expenses_date     ON expenses(expense_date);
CREATE INDEX IF NOT EXISTS idx_expenses_category ON expenses(category_id);
