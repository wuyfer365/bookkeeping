package com.bookkeeping.mapper;

import com.bookkeeping.entity.Category;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 分类 Mapper
 */
public interface CategoryMapper {

    /** 查询所有分类（按排序） */
    List<Category> findAll();

    /** 按ID查询 */
    Category findById(@Param("id") Integer id);

    /** 按父级ID查询子分类 */
    List<Category> findByParentId(@Param("parentId") Integer parentId);

    /** 插入 */
    int insert(Category category);

    /** 更新 */
    int update(Category category);

    /** 删除 */
    int deleteById(@Param("id") Integer id);

    /** 检查是否存在子分类 */
    int countByParentId(@Param("parentId") Integer parentId);

    /** 检查分类下是否有花销记录 */
    int countExpensesByCategoryId(@Param("categoryId") Integer categoryId);
}
