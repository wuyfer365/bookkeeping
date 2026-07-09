package com.bookkeeping.controller;

import com.bookkeeping.common.Result;
import com.bookkeeping.dto.CategoryTreeDTO;
import com.bookkeeping.entity.Category;
import com.bookkeeping.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理 Controller
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /** 获取分类树 */
    @GetMapping
    public Result<List<CategoryTreeDTO>> getTree() {
        return Result.success(categoryService.getTree());
    }

    /** 获取单个分类 */
    @GetMapping("/{id}")
    public Result<Category> getById(@PathVariable Integer id) {
        return Result.success(categoryService.getById(id));
    }

    /** 新建分类 */
    @PostMapping
    public Result<Category> create(@Valid @RequestBody Category category) {
        return Result.success(categoryService.create(category));
    }

    /** 更新分类 */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Integer id, @Valid @RequestBody Category category) {
        return Result.success(categoryService.update(id, category));
    }

    /** 删除分类 */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return Result.success();
    }
}
