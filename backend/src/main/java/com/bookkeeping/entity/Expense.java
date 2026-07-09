package com.bookkeeping.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 花销记录实体
 */
@Data
public class Expense {
    private Integer id;
    private BigDecimal amount;      // 金额（元）
    private Integer categoryId;     // 二级分类ID
    private LocalDate expenseDate;  // 消费日期
    private String description;     // 备注
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
