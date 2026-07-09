package com.bookkeeping.service;

import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.mapper.ExpenseMapper;
import com.bookkeeping.service.impl.StatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private ExpenseMapper expenseMapper;

    private StatisticsServiceImpl statisticsService;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsServiceImpl(expenseMapper);
    }

    // ---- getSummary ----

    @Test
    void getSummary_shouldReturnSummary() {
        StatisticsVO.Summary expected = new StatisticsVO.Summary();
        expected.setTotalAmount(new BigDecimal("1500.00"));
        expected.setRecordCount(10);
        expected.setAvgPerDay(new BigDecimal("150.00"));

        LocalDate start = LocalDate.of(2026, 7, 1);
        LocalDate end = LocalDate.of(2026, 7, 31);
        when(expenseMapper.summary(start, end)).thenReturn(expected);

        StatisticsVO.Summary result = statisticsService.getSummary(start, end);
        assertEquals(new BigDecimal("1500.00"), result.getTotalAmount());
        assertEquals(10, result.getRecordCount());
        assertEquals(new BigDecimal("150.00"), result.getAvgPerDay());
    }

    @Test
    void getSummary_noData_shouldReturnEmptySummary() {
        StatisticsVO.Summary expected = new StatisticsVO.Summary();
        expected.setTotalAmount(BigDecimal.ZERO);
        expected.setRecordCount(0);
        expected.setAvgPerDay(BigDecimal.ZERO);

        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 1, 31);
        when(expenseMapper.summary(start, end)).thenReturn(expected);

        StatisticsVO.Summary result = statisticsService.getSummary(start, end);
        assertEquals(BigDecimal.ZERO, result.getTotalAmount());
        assertEquals(0, result.getRecordCount());
    }

    // ---- getByCategory ----

    @Test
    void getByCategory_shouldReturnCategoryStats() {
        StatisticsVO.CategoryStat stat1 = new StatisticsVO.CategoryStat();
        stat1.setCategoryId(1);
        stat1.setCategoryName("餐饮");
        stat1.setTotalAmount(new BigDecimal("500.00"));
        stat1.setCount(5);

        StatisticsVO.CategoryStat stat2 = new StatisticsVO.CategoryStat();
        stat2.setCategoryId(2);
        stat2.setCategoryName("交通");
        stat2.setTotalAmount(new BigDecimal("200.00"));
        stat2.setCount(3);

        List<StatisticsVO.CategoryStat> expected = List.of(stat1, stat2);
        LocalDate start = LocalDate.of(2026, 7, 1);
        LocalDate end = LocalDate.of(2026, 7, 31);
        when(expenseMapper.statByCategory(start, end)).thenReturn(expected);

        List<StatisticsVO.CategoryStat> result = statisticsService.getByCategory(start, end);
        assertEquals(2, result.size());
        assertEquals("餐饮", result.get(0).getCategoryName());
        assertEquals(new BigDecimal("500.00"), result.get(0).getTotalAmount());
    }

    // ---- getTrend ----

    @Test
    void getTrend_shouldReturnMonthlyTrends() {
        StatisticsVO.MonthTrend t1 = new StatisticsVO.MonthTrend();
        t1.setMonth("2026-02");
        t1.setTotalAmount(new BigDecimal("3000.00"));

        StatisticsVO.MonthTrend t2 = new StatisticsVO.MonthTrend();
        t2.setMonth("2026-07");
        t2.setTotalAmount(new BigDecimal("1500.00"));

        List<StatisticsVO.MonthTrend> expected = List.of(t1, t2);
        when(expenseMapper.trendByMonth(6)).thenReturn(expected);

        List<StatisticsVO.MonthTrend> result = statisticsService.getTrend(6);
        assertEquals(2, result.size());
        assertEquals("2026-02", result.get(0).getMonth());
        assertEquals(new BigDecimal("3000.00"), result.get(0).getTotalAmount());
    }

    @Test
    void getTrend_noData_shouldReturnEmpty() {
        when(expenseMapper.trendByMonth(3)).thenReturn(List.of());

        List<StatisticsVO.MonthTrend> result = statisticsService.getTrend(3);
        assertTrue(result.isEmpty());
    }
}
