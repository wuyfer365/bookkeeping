package com.bookkeeping.controller;

import com.bookkeeping.common.Result;
import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.service.StatisticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计 Controller
 */
@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /** 汇总 */
    @GetMapping("/summary")
    public Result<StatisticsVO.Summary> summary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getSummary(startDate, endDate));
    }

    /** 按分类统计 */
    @GetMapping("/by-category")
    public Result<List<StatisticsVO.CategoryStat>> byCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return Result.success(statisticsService.getByCategory(startDate, endDate));
    }

    /** 趋势 */
    @GetMapping("/trend")
    public Result<List<StatisticsVO.MonthTrend>> trend(
            @RequestParam(defaultValue = "6") Integer months) {
        return Result.success(statisticsService.getTrend(months));
    }
}
