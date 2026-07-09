package com.bookkeeping.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 新增/编辑花销请求 DTO
 */
public class ExpenseCreateDTO {

    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    @DecimalMax(value = "99999999.99", message = "金额超出上限")
    private BigDecimal amount;

    @NotNull(message = "分类不能为空")
    private Integer categoryId;

    @NotNull(message = "消费日期不能为空")
    @PastOrPresent(message = "消费日期不能是未来日期")
    private LocalDate expenseDate;

    @Size(max = 500, message = "备注不能超过500字")
    private String description;

    // Getters & Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public LocalDate getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDate expenseDate) { this.expenseDate = expenseDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
