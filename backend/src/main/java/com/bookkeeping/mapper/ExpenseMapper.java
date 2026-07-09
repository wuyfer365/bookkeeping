package com.bookkeeping.mapper;

import com.bookkeeping.dto.ExpenseQueryDTO;
import com.bookkeeping.dto.ExpenseVO;
import com.bookkeeping.dto.StatisticsVO;
import com.bookkeeping.entity.Expense;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 花销 Mapper
 */
public interface ExpenseMapper {

    /** 分页查询（带分类名称） */
    List<ExpenseVO> findWithCategory(@Param("query") ExpenseQueryDTO query);

    /** 总数 */
    long count(@Param("query") ExpenseQueryDTO query);

    /** 按ID查详情 */
    ExpenseVO findById(@Param("id") Integer id);

    /** 插入 */
    int insert(Expense expense);

    /** 更新 */
    int update(Expense expense);

    /** 删除 */
    int deleteById(@Param("id") Integer id);

    // ---- 统计查询 ----

    /** 汇总 */
    StatisticsVO.Summary summary(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);

    /** 按一级分类统计 */
    List<StatisticsVO.CategoryStat> statByCategory(@Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /** 近N月趋势 */
    List<StatisticsVO.MonthTrend> trendByMonth(@Param("months") Integer months);
}
