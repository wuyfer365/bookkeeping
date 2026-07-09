package com.bookkeeping.dto;

import java.util.List;

/**
 * 分类树节点 — 一级含 children 二级数组
 */
public class CategoryTreeDTO {
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer level;
    private Integer sortOrder;
    private List<CategoryTreeDTO> children;  // 仅一级节点有值

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public List<CategoryTreeDTO> getChildren() { return children; }
    public void setChildren(List<CategoryTreeDTO> children) { this.children = children; }
}
