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

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryTreeDTO> getTree() {
        List<Category> all = categoryMapper.findAll();

        // 分离一级和二级
        List<Category> parents = all.stream()
                .filter(c -> c.getLevel() == 1)
                .toList();

        Map<Integer, List<Category>> childrenMap = all.stream()
                .filter(c -> c.getLevel() == 2)
                .collect(Collectors.groupingBy(Category::getParentId));

        // 组装树
        List<CategoryTreeDTO> tree = new ArrayList<>();
        for (Category p : parents) {
            CategoryTreeDTO dto = toDTO(p);
            List<Category> children = childrenMap.getOrDefault(p.getId(), List.of());
            dto.setChildren(children.stream().map(this::toDTO).collect(Collectors.toList()));
            tree.add(dto);
        }
        return tree;
    }

    @Override
    public Category getById(Integer id) {
        Category c = categoryMapper.findById(id);
        if (c == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return c;
    }

    @Override
    public Category create(Category category) {
        // 如果是二级分类，检查父级是否存在且是一级
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

    @Override
    public Category update(Integer id, Category category) {
        Category existing = getById(id);
        existing.setName(category.getName());
        existing.setSortOrder(category.getSortOrder());
        categoryMapper.update(existing);
        return existing;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        getById(id); // 确保存在
        categoryMapper.deleteById(id);
        // SQLite CASCADE 自动删除子分类和关联花销
    }

    // ---- 内部工具 ----

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
