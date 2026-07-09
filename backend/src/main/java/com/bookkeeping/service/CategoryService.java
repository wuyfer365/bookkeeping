package com.bookkeeping.service;

import com.bookkeeping.dto.CategoryTreeDTO;
import com.bookkeeping.entity.Category;
import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService {

    /** 查询全部分类树 */
    List<CategoryTreeDTO> getTree();

    /** 查询单个分类 */
    Category getById(Integer id);

    /** 新建分类 */
    Category create(Category category);

    /** 更新分类 */
    Category update(Integer id, Category category);

    /** 删除分类 */
    void delete(Integer id);
}
