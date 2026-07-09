package com.bookkeeping.service;

import com.bookkeeping.dto.StatisticsVO;
import java.time.LocalDate;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /** 汇总 */
    StatisticsVO.Summary getSummary(LocalDate startDate, LocalDate endDate);

    /** 按一级分类统计 */
    List<StatisticsVO.CategoryStat> getByCategory(LocalDate startDate, LocalDate endDate);

    /** 近N月趋势 */
    List<StatisticsVO.MonthTrend> getTrend(Integer months);
}
