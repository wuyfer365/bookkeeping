package com.bookkeeping.service;

import com.bookkeeping.common.PageResult;
import com.bookkeeping.dto.*;
import com.bookkeeping.entity.Category;
import com.bookkeeping.entity.Expense;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.mapper.CategoryMapper;
import com.bookkeeping.mapper.ExpenseMapper;
import com.bookkeeping.service.impl.ExpenseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private CategoryMapper categoryMapper;

    private ExpenseServiceImpl expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseServiceImpl(expenseMapper, categoryMapper);
    }

    // ---- list ----

    @Test
    void list_shouldReturnPageResult() {
        ExpenseQueryDTO query = new ExpenseQueryDTO();
        query.setPage(1);
        query.setSize(20);

        ExpenseVO vo = buildVO(1, "50.00", "早餐", "餐饮", LocalDate.of(2026, 7, 9));
        when(expenseMapper.findWithCategory(query)).thenReturn(List.of(vo));
        when(expenseMapper.count(query)).thenReturn(1L);

        PageResult<ExpenseVO> result = expenseService.list(query);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getPage());
        assertEquals(20, result.getPageSize());
    }

    // ---- getById ----

    @Test
    void getById_existing_shouldReturnVO() {
        ExpenseVO vo = buildVO(1, "100.00", "午餐", "餐饮", LocalDate.of(2026, 7, 8));
        when(expenseMapper.findById(1)).thenReturn(vo);

        ExpenseVO result = expenseService.getById(1);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
    }

    @Test
    void getById_notExisting_shouldThrowBusinessException() {
        when(expenseMapper.findById(999)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseService.getById(999));
        assertEquals(404, ex.getCode());
        assertTrue(ex.getMessage().contains("不存在"));
    }

    // ---- create ----

    @Test
    void create_validInput_shouldSucceed() {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal("25.50"));
        dto.setCategoryId(11); // 早餐 (二级)
        dto.setExpenseDate(LocalDate.of(2026, 7, 9));
        dto.setDescription("豆浆油条");

        Category cat = new Category();
        cat.setId(11);
        cat.setName("早餐");
        cat.setLevel(2);

        when(categoryMapper.findById(11)).thenReturn(cat);
        when(expenseMapper.insert(any(Expense.class))).thenAnswer(inv -> {
            Expense e = inv.getArgument(0);
            e.setId(100);
            return 1;
        });
        when(expenseMapper.findById(100)).thenReturn(
                buildVO(100, "25.50", "早餐", "餐饮", LocalDate.of(2026, 7, 9)));

        ExpenseVO result = expenseService.create(dto);
        assertNotNull(result);
        assertEquals(new BigDecimal("25.50"), result.getAmount());
        assertEquals("早餐", result.getCategoryName());
    }

    @Test
    void create_categoryNotExists_shouldThrow() {
        ExpenseCreateDTO dto = buildDTO("10.00", 999, LocalDate.now());
        when(categoryMapper.findById(999)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseService.create(dto));
        assertEquals("分类不存在", ex.getMessage());
    }

    @Test
    void create_categoryNotLevel2_shouldThrow() {
        ExpenseCreateDTO dto = buildDTO("10.00", 1, LocalDate.now()); // 1=餐饮（一级）

        Category parent = new Category();
        parent.setId(1);
        parent.setName("餐饮");
        parent.setLevel(1); // 一级，不能选

        when(categoryMapper.findById(1)).thenReturn(parent);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> expenseService.create(dto));
        assertEquals("请选择二级分类（小类）", ex.getMessage());
    }

    // ---- update ----

    @Test
    void update_validInput_shouldSucceed() {
        ExpenseCreateDTO dto = buildDTO("88.88", 12, LocalDate.of(2026, 7, 8));
        ExpenseVO existingVO = buildVO(1, "50.00", "早餐", "餐饮", LocalDate.of(2026, 7, 9));

        Category cat = new Category();
        cat.setId(12);
        cat.setName("午餐");
        cat.setLevel(2);

        when(expenseMapper.findById(1)).thenReturn(existingVO);
        when(categoryMapper.findById(12)).thenReturn(cat);
        when(expenseMapper.update(any(Expense.class))).thenReturn(1);
        when(expenseMapper.findById(1)).thenReturn(
                buildVO(1, "88.88", "午餐", "餐饮", LocalDate.of(2026, 7, 8)));

        ExpenseVO result = expenseService.update(1, dto);
        assertEquals(new BigDecimal("88.88"), result.getAmount());
    }

    @Test
    void update_recordNotExists_shouldThrow() {
        ExpenseCreateDTO dto = buildDTO("50.00", 11, LocalDate.now());
        when(expenseMapper.findById(999)).thenReturn(null);

        assertThrows(BusinessException.class, () -> expenseService.update(999, dto));
    }

    // ---- delete ----

    @Test
    void delete_existing_shouldSucceed() {
        ExpenseVO vo = buildVO(1, "50.00", "早餐", "餐饮", LocalDate.now());
        when(expenseMapper.findById(1)).thenReturn(vo);
        when(expenseMapper.deleteById(1)).thenReturn(1);

        assertDoesNotThrow(() -> expenseService.delete(1));
        verify(expenseMapper).deleteById(1);
    }

    @Test
    void delete_notExisting_shouldThrow() {
        when(expenseMapper.findById(999)).thenReturn(null);
        assertThrows(BusinessException.class, () -> expenseService.delete(999));
        verify(expenseMapper, never()).deleteById(any());
    }

    // ---- 工具方法 ----

    private ExpenseCreateDTO buildDTO(String amount, Integer categoryId, LocalDate date) {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal(amount));
        dto.setCategoryId(categoryId);
        dto.setExpenseDate(date);
        return dto;
    }

    private ExpenseVO buildVO(Integer id, String amount, String catName, String parentName, LocalDate date) {
        ExpenseVO vo = new ExpenseVO();
        vo.setId(id);
        vo.setAmount(new BigDecimal(amount));
        vo.setCategoryId(11);
        vo.setCategoryName(catName);
        vo.setParentCategoryId(1);
        vo.setParentCategoryName(parentName);
        vo.setExpenseDate(date);
        return vo;
    }
}
