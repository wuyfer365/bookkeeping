package com.bookkeeping.service.impl;

import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.mapper.ExpenseMapper;
import com.bookkeeping.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final ExpenseMapper expenseMapper;

    public StatisticsServiceImpl(ExpenseMapper expenseMapper) {
        this.expenseMapper = expenseMapper;
    }

    @Override
    public StatisticsVO.Summary getSummary(LocalDate startDate, LocalDate endDate) {
        return expenseMapper.summary(startDate, endDate);
    }

    @Override
    public List<StatisticsVO.CategoryStat> getByCategory(LocalDate startDate, LocalDate endDate) {
        return expenseMapper.statByCategory(startDate, endDate);
    }

    @Override
    public List<StatisticsVO.MonthTrend> getTrend(Integer months) {
        return expenseMapper.trendByMonth(months);
    }
}
