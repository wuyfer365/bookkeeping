-- 记账APP 初始分类数据
-- 每次启动时执行 INSERT OR IGNORE（已有数据则跳过）

-- ========== 一级分类 (parent_id = NULL, level = 1) ==========
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (1,  '餐饮', NULL, 1, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (2,  '交通', NULL, 1, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (3,  '购物', NULL, 1, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (4,  '服饰', NULL, 1, 4);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (5,  '住房', NULL, 1, 5);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (6,  '娱乐', NULL, 1, 6);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (7,  '医疗', NULL, 1, 7);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (8,  '教育', NULL, 1, 8);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (9,  '通讯', NULL, 1, 9);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (10, '其他', NULL, 1, 10);

-- ========== 二级分类 (parent_id = 一级ID, level = 2) ==========
-- 1. 餐饮 (parent_id = 1)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (11, '早餐',     1, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (12, '午餐',     1, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (13, '晚餐',     1, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (14, '零食',     1, 2, 4);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (15, '饮品',     1, 2, 5);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (16, '聚餐',     1, 2, 6);

-- 2. 交通 (parent_id = 2)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (17, '公交/地铁', 2, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (18, '网约车',    2, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (19, '加油/充电', 2, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (20, '停车费',    2, 2, 4);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (21, '火车/高铁', 2, 2, 5);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (22, '飞机',      2, 2, 6);

-- 3. 购物 (parent_id = 3)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (23, '日用品',    3, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (24, '数码产品',  3, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (25, '家居用品',  3, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (26, '图书',      3, 2, 4);

-- 4. 服饰 (parent_id = 4)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (27, '衣服',      4, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (28, '鞋帽',      4, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (29, '配饰',      4, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (30, '美容护肤',  4, 2, 4);

-- 5. 住房 (parent_id = 5)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (31, '房租/房贷', 5, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (32, '水电费',    5, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (33, '物业费',    5, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (34, '维修',      5, 2, 4);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (35, '装修',      5, 2, 5);

-- 6. 娱乐 (parent_id = 6)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (36, '电影',      6, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (37, '游戏',      6, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (38, '旅游',      6, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (39, '运动健身',  6, 2, 4);

-- 7. 医疗 (parent_id = 7)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (40, '门诊',      7, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (41, '药品',      7, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (42, '体检',      7, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (43, '住院',      7, 2, 4);

-- 8. 教育 (parent_id = 8)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (44, '培训课程',  8, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (45, '书籍资料',  8, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (46, '考试报名',  8, 2, 3);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (47, '子女教育',  8, 2, 4);

-- 9. 通讯 (parent_id = 9)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (48, '话费',      9, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (49, '网费',      9, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (50, '快递',      9, 2, 3);

-- 10. 其他 (parent_id = 10)
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (51, '人情往来', 10, 2, 1);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (52, '捐赠',     10, 2, 2);
INSERT OR IGNORE INTO categories (id, name, parent_id, level, sort_order) VALUES (53, '杂项',     10, 2, 3);
