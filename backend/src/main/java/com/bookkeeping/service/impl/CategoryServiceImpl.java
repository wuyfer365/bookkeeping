package com.bookkeeping.service.impl;

import com.bookkeeping.dto.CategoryTreeDTO;
import com.bookkeeping.entity.Category;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.mapper.CategoryMapper;
import com.bookkeeping.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现
 * 负责两级分类（一级大类 → 二级小类）的增删改查及树形结构组装。
 * 删除一级分类时依赖 SQLite 外键 CASCADE 自动级联删除子分类和关联花销。
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    /**
     * 获取分类树（两级结构）
     * 先从数据库查出全部分类，按 level 拆分为一级和二级，
     * 再将二级分类按 parentId 挂载到对应一级节点下。
     *
     * @return 一级分类列表，每个节点包含其下所有二级子分类
     */
    @Override
    public List<CategoryTreeDTO> getTree() {
        List<Category> all = categoryMapper.findAll();

        // 一级大类：parent_id IS NULL, level = 1
        List<Category> parents = all.stream()
                .filter(c -> c.getLevel() == 1)
                .toList();

        // 二级小类：parent_id 指向一级分类ID, level = 2
        Map<Integer, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getLevel() == 2)
                .collect(Collectors.groupingBy(Category::getParentId));

        // 组装树：遍历一级分类，填充其 children 列表
        List<CategoryTreeDTO> tree = new ArrayList<>();
        for (Category p : parents) {
            CategoryTreeDTO dto = toDTO(p);
            List<Category> children = childrenMap.getOrDefault(p.getId(), List.of());
            dto.setChildren(children.stream().map(this::toDTO).collect(Collectors.toList()));
            tree.add(dto);
        }
        return tree;
    }

    /**
     * 按 ID 查询单个分类
     *
     * @param id 分类ID
     * @return 分类实体
     * @throws BusinessException(404) 分类不存在时抛出
     */
    @Override
    public Category getById(Integer id) {
        Category c = categoryMapper.findById(id);
        if (c == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return c;
    }

    /**
     * 新建分类
     * 如果指定了 parentId 且指向一级分类，则创建二级子类；
     * 如果 parentId 为空，则创建一级大类。
     * 不允许把二级分类（如"早餐"）作为父级再建子类。
     *
     * @param category 分类实体（name 必填，parentId 可为空）
     * @return 创建后的分类（含自增ID）
     * @throws BusinessException 父分类不存在或不是一级分类
     */
    @Override
    public Category create(Category category) {
        // 二级分类：必须确保 parentId 指向合法的一级大类
        if (category.getParentId() != null) {
            Category parent = categoryMapper.findById(category.getParentId());
            if (parent == null || parent.getLevel() != 1) {
                throw new BusinessException("父分类不存在或不是一级分类");
            }
            category.setLevel(2);
        } else {
            category.setLevel(1);
        }
        categoryMapper.insert(category);
        return category;
    }

    /**
     * 更新分类
     * 仅允许修改名称和排序，不可改变父级关系（parentId/level 不可变）。
     *
     * @param id 目标分类ID
     * @param category 新值（name、sortOrder）
     * @return 更新后的分类
     * @throws BusinessException(404) 分类不存在
     */
    @Override
    public Category update(Integer id, Category category) {
        Category existing = getById(id);
        existing.setName(category.getName());
        existing.setSortOrder(category.getSortOrder());
        categoryMapper.update(existing);
        return existing;
    }

    /**
     * 删除分类及其关联数据
     * 删除一级分类 → SQLite 外键 CASCADE 自动级联删除所有子分类和这些分类下的花销记录。
     * 删除二级分类 → CASCADE 自动删除该分类下的花销记录。
     *
     * @param id 要删除的分类ID
     * @throws BusinessException(404) 分类不存在
     */
    @Override
    @Transactional
    public void delete(Integer id) {
        getById(id); // 确保存在，不存在抛异常
        categoryMapper.deleteById(id);
        // 外键 CASCADE: 子分类和关联花销由 SQLite 自动删除，无需手动清理
    }

    // ---- 内部工具 ----

    /**
     * Entity → DTO 转换
     * 将 Category 实体映射为 CategoryTreeDTO，不复制 children（由调用方 getTree 组装）。
     */
    private CategoryTreeDTO toDTO(Category c) {
        CategoryTreeDTO dto = new CategoryTreeDTO();
        dto.setId(c.getId());
        dto.setName(c.getName());
        dto.setParentId(c.getParentId());
        dto.setLevel(c.getLevel());
        dto.setSortOrder(c.getSortOrder());
        return dto;
    }
}
