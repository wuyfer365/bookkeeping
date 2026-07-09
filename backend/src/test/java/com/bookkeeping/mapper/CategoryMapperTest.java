package com.bookkeeping.mapper;

import com.bookkeeping.entity.Category;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CategoryMapper 集成测试 — 使用 SQLite 真实 SQL 执行
 * 每个测试回滚，互不干扰
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite:target/test-bookkeeping.db?foreign_keys=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoryMapperTest {

    @Autowired
    private CategoryMapper categoryMapper;

    // 记录本测试插入的 ID，用于 @AfterEach 清理
    private final List<Integer> createdIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        // 删除本测试插入的数据，保持测试隔离
        for (Integer id : createdIds) {
            categoryMapper.deleteById(id);
        }
        createdIds.clear();
    }

    // ---- findAll ----

    @Test
    @Order(1)
    void findAll_shouldReturnAllCategories() {
        List<Category> all = categoryMapper.findAll();
        assertTrue(all.size() >= 53, "至少包含预设的 53 个分类");
        assertEquals(1, all.get(0).getLevel(), "第一条应该是一级分类");
    }

    // ---- findById ----

    @Test
    @Order(2)
    void findById_existing_shouldReturnCategory() {
        Category c = categoryMapper.findById(1);
        assertNotNull(c);
        assertEquals("餐饮", c.getName());
        assertEquals(1, c.getLevel());
    }

    @Test
    @Order(3)
    void findById_notExisting_shouldReturnNull() {
        Category c = categoryMapper.findById(99999);
        assertNull(c);
    }

    // ---- findByParentId ----

    @Test
    @Order(4)
    void findByParentId_shouldReturnChildren() {
        List<Category> children = categoryMapper.findByParentId(1);
        assertEquals(6, children.size(), "餐饮应有6个子分类");
        children.forEach(c -> {
            assertEquals(2, c.getLevel());
            assertEquals(1, c.getParentId());
        });
    }

    @Test
    @Order(5)
    void findByParentId_noChildren_shouldReturnEmpty() {
        List<Category> children = categoryMapper.findByParentId(11);
        assertTrue(children.isEmpty());
    }

    // ---- insert ----

    @Test
    @Order(6)
    void insert_shouldReturnGeneratedId() {
        Category cat = new Category();
        cat.setName("测试一级");
        cat.setLevel(1);
        cat.setSortOrder(99);
        cat.setParentId(null);

        int rows = categoryMapper.insert(cat);
        assertEquals(1, rows);
        assertNotNull(cat.getId());
        createdIds.add(cat.getId());

        Category saved = categoryMapper.findById(cat.getId());
        assertNotNull(saved);
        assertEquals("测试一级", saved.getName());
    }

    @Test
    @Order(7)
    void insert_subCategory_shouldWork() {
        Category sub = new Category();
        sub.setName("测试二级");
        sub.setLevel(2);
        sub.setParentId(1);
        sub.setSortOrder(50);

        categoryMapper.insert(sub);
        assertNotNull(sub.getId());
        createdIds.add(sub.getId());

        Category saved = categoryMapper.findById(sub.getId());
        assertEquals(1, saved.getParentId());
        assertEquals(2, saved.getLevel());
    }

    // ---- update ----

    @Test
    @Order(8)
    void update_shouldChangeNameAndSortOrder() {
        Category cat = new Category();
        cat.setName("旧名称");
        cat.setLevel(1);
        cat.setSortOrder(1);
        categoryMapper.insert(cat);
        createdIds.add(cat.getId());

        cat.setName("新名称");
        cat.setSortOrder(100);
        categoryMapper.update(cat);

        Category updated = categoryMapper.findById(cat.getId());
        assertEquals("新名称", updated.getName());
        assertEquals(100, updated.getSortOrder());
    }

    // ---- deleteById ----

    @Test
    @Order(9)
    void deleteById_shouldRemoveCategory() {
        Category cat = new Category();
        cat.setName("待删除");
        cat.setLevel(1);
        categoryMapper.insert(cat);
        // 不放到 createdIds，因为会手动删除

        int rows = categoryMapper.deleteById(cat.getId());
        assertEquals(1, rows);
        assertNull(categoryMapper.findById(cat.getId()));
    }

    // ---- countByParentId ----

    @Test
    @Order(10)
    void countByParentId_shouldReturnChildCount() {
        int count = categoryMapper.countByParentId(1);
        assertEquals(6, count, "餐饮 (id=1) 有 6 个种子子分类");
    }

    @Test
    @Order(11)
    void countByParentId_noChildren_shouldReturnZero() {
        int count = categoryMapper.countByParentId(11);
        assertEquals(0, count);
    }

    // ---- countExpensesByCategoryId ----

    @Test
    @Order(12)
    void countExpensesByCategoryId_noExpenses_shouldReturnZero() {
        int count = categoryMapper.countExpensesByCategoryId(11);
        assertEquals(0, count);
    }
}
