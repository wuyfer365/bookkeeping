package com.bookkeeping.controller;

import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    // ---- GET /api/statistics/summary ----

    @Test
    void summary_shouldReturnStats() throws Exception {
        StatisticsVO.Summary summary = new StatisticsVO.Summary();
        summary.setTotalAmount(new BigDecimal("5000.00"));
        summary.setRecordCount(30);
        summary.setAvgPerDay(new BigDecimal("166.67"));

        when(statisticsService.getSummary(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(summary);

        mockMvc.perform(get("/api/statistics/summary")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalAmount").value(5000.00))
                .andExpect(jsonPath("$.data.recordCount").value(30))
                .andExpect(jsonPath("$.data.avgPerDay").value(166.67));
    }

    // ---- GET /api/statistics/by-category ----

    @Test
    void byCategory_shouldReturnCategoryStats() throws Exception {
        StatisticsVO.CategoryStat stat = new StatisticsVO.CategoryStat();
        stat.setCategoryId(1);
        stat.setCategoryName("餐饮");
        stat.setTotalAmount(new BigDecimal("2000.00"));
        stat.setCount(15);

        when(statisticsService.getByCategory(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(stat));

        mockMvc.perform(get("/api/statistics/by-category")
                        .param("startDate", "2026-07-01")
                        .param("endDate", "2026-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].categoryName").value("餐饮"))
                .andExpect(jsonPath("$.data[0].count").value(15));
    }

    // ---- GET /api/statistics/trend ----

    @Test
    void trend_shouldReturnMonthlyTrends() throws Exception {
        StatisticsVO.MonthTrend t1 = new StatisticsVO.MonthTrend();
        t1.setMonth("2026-07");
        t1.setTotalAmount(new BigDecimal("1500.00"));

        when(statisticsService.getTrend(6)).thenReturn(List.of(t1));

        mockMvc.perform(get("/api/statistics/trend")
                        .param("months", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].month").value("2026-07"))
                .andExpect(jsonPath("$.data[0].totalAmount").value(1500.00));
    }

    @Test
    void trend_defaultMonths_shouldWork() throws Exception {
        when(statisticsService.getTrend(6)).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/trend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void trend_customMonths_shouldPassParameter() throws Exception {
        when(statisticsService.getTrend(12)).thenReturn(List.of());

        mockMvc.perform(get("/api/statistics/trend")
                        .param("months", "12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
