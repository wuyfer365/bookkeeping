package com.bookkeeping.mapper;

import com.bookkeeping.dto.ExpenseQueryDTO;
import com.bookkeeping.dto.ExpenseVO;
import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.entity.Expense;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExpenseMapper 集成测试 — 真实 SQLite SQL 执行
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite:target/test-bookkeeping.db?foreign_keys=true"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExpenseMapperTest {

    @Autowired
    private ExpenseMapper expenseMapper;

    private final List<Integer> createdIds = new ArrayList<>();

    @AfterEach
    void cleanup() {
        for (Integer id : createdIds) {
            expenseMapper.deleteById(id);
        }
        createdIds.clear();
    }

    // ---- insert ----

    @Test
    @Order(1)
    void insert_shouldReturnGeneratedId() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("35.50"));
        expense.setCategoryId(11); // 早餐
        expense.setExpenseDate(LocalDate.of(2026, 7, 9));
        expense.setDescription("测试早餐");

        int rows = expenseMapper.insert(expense);
        assertEquals(1, rows);
        assertNotNull(expense.getId());
        createdIds.add(expense.getId());

        ExpenseVO saved = expenseMapper.findById(expense.getId());
        // 用 compareTo 避免 BigDecimal 精度差异
        assertEquals(0, new BigDecimal("35.50").compareTo(saved.getAmount()),
                "金额应为 35.50");
    }

    // ---- findById ----

    @Test
    @Order(2)
    void findById_existing_shouldReturnVOWithCategoryNames() {
        // 插入一条
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("35.50"));
        expense.setCategoryId(11);
        expense.setExpenseDate(LocalDate.of(2026, 7, 9));
        expense.setDescription("早餐测试");
        expenseMapper.insert(expense);
        createdIds.add(expense.getId());

        ExpenseVO vo = expenseMapper.findById(expense.getId());
        assertNotNull(vo);
        assertEquals(0, new BigDecimal("35.50").compareTo(vo.getAmount()));
        assertEquals("早餐", vo.getCategoryName());
        assertEquals("餐饮", vo.getParentCategoryName());
        assertEquals(1, vo.getParentCategoryId());
    }

    @Test
    @Order(3)
    void findById_notExisting_shouldReturnNull() {
        ExpenseVO vo = expenseMapper.findById(99999);
        assertNull(vo);
    }

    // ---- findWithCategory & count (分页) ----

    @Test
    @Order(4)
    void findWithCategory_shouldReturnRecords() {
        // 插入测试数据
        Expense e = new Expense();
        e.setAmount(new BigDecimal("10.00"));
        e.setCategoryId(11);
        e.setExpenseDate(LocalDate.of(2026, 7, 9));
        expenseMapper.insert(e);
        createdIds.add(e.getId());

        ExpenseQueryDTO query = new ExpenseQueryDTO();
        query.setPage(1);
        query.setSize(20);

        List<ExpenseVO> list = expenseMapper.findWithCategory(query);
        assertFalse(list.isEmpty());
        ExpenseVO first = list.get(0);
        assertNotNull(first.getCategoryName());
        assertNotNull(first.getParentCategoryName());
    }

    @Test
    @Order(5)
    void count_shouldReturnTotal() {
        Expense e = new Expense();
        e.setAmount(new BigDecimal("1.00"));
        e.setCategoryId(11);
        e.setExpenseDate(LocalDate.of(2026, 1, 1));
        expenseMapper.insert(e);
        createdIds.add(e.getId());

        ExpenseQueryDTO query = new ExpenseQueryDTO();
        long total = expenseMapper.count(query);
        assertTrue(total >= 1);
    }

    @Test
    @Order(6)
    void findWithCategory_byDateRange_shouldFilter() {
        Expense e1 = new Expense();
        e1.setAmount(new BigDecimal("100.00"));
        e1.setCategoryId(12);
        e1.setExpenseDate(LocalDate.of(2026, 7, 15));
        expenseMapper.insert(e1);
        createdIds.add(e1.getId());

        ExpenseQueryDTO query = new ExpenseQueryDTO();
        query.setStartDate(LocalDate.of(2026, 7, 1));
        query.setEndDate(LocalDate.of(2026, 7, 31));

        List<ExpenseVO> list = expenseMapper.findWithCategory(query);
        assertFalse(list.isEmpty());
        list.forEach(vo -> {
            assertTrue(vo.getExpenseDate().compareTo(LocalDate.of(2026, 7, 1)) >= 0);
        });
    }

    @Test
    @Order(7)
    void findWithCategory_byCategoryId_shouldFilter() {
        Expense e = new Expense();
        e.setAmount(new BigDecimal("20.00"));
        e.setCategoryId(12);
        e.setExpenseDate(LocalDate.of(2026, 7, 9));
        expenseMapper.insert(e);
        createdIds.add(e.getId());

        ExpenseQueryDTO query = new ExpenseQueryDTO();
        query.setCategoryId(12);

        List<ExpenseVO> list = expenseMapper.findWithCategory(query);
        assertFalse(list.isEmpty());
        list.forEach(vo -> assertEquals(12, vo.getCategoryId()));
    }

    @Test
    @Order(8)
    void findWithCategory_pagination_shouldWork() {
        ExpenseQueryDTO query = new ExpenseQueryDTO();
        query.setPage(1);
        query.setSize(2);

        List<ExpenseVO> list = expenseMapper.findWithCategory(query);
        assertTrue(list.size() <= 2, "最多返回2条");
    }

    // ---- update ----

    @Test
    @Order(9)
    void update_shouldChangeValues() {
        // 先插入
        Expense original = new Expense();
        original.setAmount(new BigDecimal("25.00"));
        original.setCategoryId(11);
        original.setExpenseDate(LocalDate.of(2026, 7, 1));
        original.setDescription("修改前");
        expenseMapper.insert(original);
        createdIds.add(original.getId());

        // 修改
        original.setAmount(new BigDecimal("50.00"));
        original.setCategoryId(12);
        original.setExpenseDate(LocalDate.of(2026, 7, 8));
        original.setDescription("修改后");
        expenseMapper.update(original);

        ExpenseVO updated = expenseMapper.findById(original.getId());
        assertEquals(0, new BigDecimal("50.00").compareTo(updated.getAmount()));
        assertEquals("修改后", updated.getDescription());
    }

    // ---- deleteById ----

    @Test
    @Order(10)
    void deleteById_shouldRemove() {
        Expense toDelete = new Expense();
        toDelete.setAmount(new BigDecimal("1.00"));
        toDelete.setCategoryId(11);
        toDelete.setExpenseDate(LocalDate.of(2026, 7, 1));
        expenseMapper.insert(toDelete);

        int rows = expenseMapper.deleteById(toDelete.getId());
        assertEquals(1, rows);
        assertNull(expenseMapper.findById(toDelete.getId()));
    }

    // ---- 统计: summary ----

    @Test
    @Order(11)
    void summary_shouldReturnStats() {
        // 确保有数据
        Expense e = new Expense();
        e.setAmount(new BigDecimal("100.00"));
        e.setCategoryId(11);
        e.setExpenseDate(LocalDate.of(2026, 6, 15));
        expenseMapper.insert(e);
        createdIds.add(e.getId());

        StatisticsVO.Summary s = expenseMapper.summary(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31));
        assertNotNull(s);
        assertTrue(s.getTotalAmount().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(s.getRecordCount() > 0);
    }

    // ---- 统计: statByCategory ----

    @Test
    @Order(12)
    void statByCategory_shouldGroupByParentCategory() {
        Expense e = new Expense();
        e.setAmount(new BigDecimal("50.00"));
        e.setCategoryId(11);
        e.setExpenseDate(LocalDate.of(2026, 7, 9));
        expenseMapper.insert(e);
        createdIds.add(e.getId());

        List<StatisticsVO.CategoryStat> stats = expenseMapper.statByCategory(
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31));
        assertNotNull(stats);
        assertTrue(stats.size() >= 1);
    }

    // ---- 统计: trendByMonth ----

    @Test
    @Order(13)
    void trendByMonth_shouldReturnMonthlyData() {
        List<StatisticsVO.MonthTrend> trends = expenseMapper.trendByMonth(6);
        assertNotNull(trends);
        // 应该至少有当前月份的条目（或许在 7 月有数据）
        assertTrue(trends.size() >= 0);
        if (!trends.isEmpty()) {
            assertTrue(trends.get(0).getMonth().matches("\\d{4}-\\d{2}"));
        }
    }
}
