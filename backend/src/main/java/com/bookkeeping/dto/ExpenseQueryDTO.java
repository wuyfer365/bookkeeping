package com.bookkeeping.dto;

import java.time.LocalDate;

/**
 * 花销列表查询参数
 */
public class ExpenseQueryDTO {
    private Integer page = 1;
    private Integer size = 20;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer categoryId;

    // Getters & Setters
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    /** 计算 MyBatis OFFSET */
    public int getOffset() {
        return (page - 1) * size;
    }
}
