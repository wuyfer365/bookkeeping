package com.bookkeeping.dto;

import java.math.BigDecimal;

/**
 * 统计相关 VO
 */
public class StatisticsVO {

    /** 月度汇总 — 按一级分类 */
    public static class CategoryStat {
        private String categoryName;
        private Integer categoryId;
        private BigDecimal totalAmount;
        private Integer count;

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public Integer getCategoryId() { return categoryId; }
        public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }

    /** 趋势 — 每月总支出 */
    public static class MonthTrend {
        private String month;           // 格式: YYYY-MM
        private BigDecimal totalAmount;

        public String getMonth() { return month; }
        public void setMonth(String month) { this.month = month; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    }

    /** 汇总统计 */
    public static class Summary {
        private BigDecimal totalAmount;
        private Integer recordCount;
        private BigDecimal avgPerDay;

        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        public Integer getRecordCount() { return recordCount; }
        public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }
        public BigDecimal getAvgPerDay() { return avgPerDay; }
        public void setAvgPerDay(BigDecimal avgPerDay) { this.avgPerDay = avgPerDay; }
    }
}
