package com.bookkeeping.controller;

import com.bookkeeping.common.PageResult;
import com.bookkeeping.dto.ExpenseCreateDTO;
import com.bookkeeping.dto.ExpenseQueryDTO;
import com.bookkeeping.dto.ExpenseVO;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.service.ExpenseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ---- GET /api/expenses ----

    @Test
    void list_shouldReturnPageResult() throws Exception {
        ExpenseVO vo = buildVO(1, "35.50", "早餐", "餐饮");
        PageResult<ExpenseVO> page = new PageResult<>(List.of(vo), 1, 1, 20);

        when(expenseService.list(any())).thenReturn(page);

        mockMvc.perform(get("/api/expenses")
                        .param("page", "1")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records[0].categoryName").value("早餐"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    // ---- GET /api/expenses/{id} ----

    @Test
    void getById_existing_shouldReturnExpense() throws Exception {
        ExpenseVO vo = buildVO(1, "100.00", "午餐", "餐饮");
        when(expenseService.getById(1)).thenReturn(vo);

        mockMvc.perform(get("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(100.00));
    }

    @Test
    void getById_notFound_shouldReturnError() throws Exception {
        when(expenseService.getById(999))
                .thenThrow(new BusinessException(404, "花销记录不存在"));

        mockMvc.perform(get("/api/expenses/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ---- POST /api/expenses ----

    @Test
    void create_valid_shouldReturnCreated() throws Exception {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal("25.50"));
        dto.setCategoryId(11);
        dto.setExpenseDate(LocalDate.of(2026, 7, 9));
        dto.setDescription("测试");

        ExpenseVO saved = buildVO(100, "25.50", "早餐", "餐饮");
        when(expenseService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    @Test
    void create_missingAmount_shouldReturnValidationError() throws Exception {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(null);
        dto.setCategoryId(11);
        dto.setExpenseDate(LocalDate.of(2026, 7, 9));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void create_negativeAmount_shouldReturnValidationError() throws Exception {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal("-1.00"));
        dto.setCategoryId(11);
        dto.setExpenseDate(LocalDate.of(2026, 7, 9));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void create_futureDate_shouldReturnValidationError() throws Exception {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal("50.00"));
        dto.setCategoryId(11);
        dto.setExpenseDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    // ---- PUT /api/expenses/{id} ----

    @Test
    void update_valid_shouldReturnUpdated() throws Exception {
        ExpenseCreateDTO dto = new ExpenseCreateDTO();
        dto.setAmount(new BigDecimal("88.00"));
        dto.setCategoryId(12);
        dto.setExpenseDate(LocalDate.of(2026, 7, 8));

        ExpenseVO updated = buildVO(1, "88.00", "午餐", "餐饮");
        when(expenseService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/expenses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.amount").value(88.00));
    }

    // ---- DELETE /api/expenses/{id} ----

    @Test
    void delete_existing_shouldReturnSuccess() throws Exception {
        doNothing().when(expenseService).delete(1);

        mockMvc.perform(delete("/api/expenses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ---- 工具方法 ----

    private ExpenseVO buildVO(Integer id, String amount, String catName, String parentName) {
        ExpenseVO vo = new ExpenseVO();
        vo.setId(id);
        vo.setAmount(new BigDecimal(amount));
        vo.setCategoryId(11);
        vo.setCategoryName(catName);
        vo.setParentCategoryId(1);
        vo.setParentCategoryName(parentName);
        vo.setExpenseDate(LocalDate.of(2026, 7, 9));
        return vo;
    }
}
