package com.bookkeeping.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 分类实体 — 单表自引用实现两级分类
 * parent_id = NULL → 一级大类
 * parent_id != NULL → 二级小类
 */
@Data
public class Category {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer level;        // 1=一级, 2=二级
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
