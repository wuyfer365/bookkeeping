package com.bookkeeping.service;

import com.bookkeeping.dto.CategoryTreeDTO;
import com.bookkeeping.entity.Category;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.mapper.CategoryMapper;
import com.bookkeeping.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryMapper);
    }

    // ---- getTree ----

    @Test
    void getTree_shouldBuildTreeCorrectly() {
        // 准备数据：2个一级，3个二级
        Category parent1 = buildCategory(1, "餐饮", null, 1);
        Category parent2 = buildCategory(2, "交通", null, 1);
        Category child1 = buildCategory(11, "早餐", 1, 2);
        Category child2 = buildCategory(12, "午餐", 1, 2);
        Category child3 = buildCategory(17, "公交/地铁", 2, 2);

        when(categoryMapper.findAll())
                .thenReturn(List.of(parent1, parent2, child1, child2, child3));

        List<CategoryTreeDTO> tree = categoryService.getTree();

        assertEquals(2, tree.size());
        // 餐饮有2个子分类
        assertEquals(2, tree.get(0).getChildren().size());
        assertEquals("早餐", tree.get(0).getChildren().get(0).getName());
        // 交通有1个子分类
        assertEquals(1, tree.get(1).getChildren().size());
        assertEquals("公交/地铁", tree.get(1).getChildren().get(0).getName());
    }

    @Test
    void getTree_noCategories_shouldReturnEmptyList() {
        when(categoryMapper.findAll()).thenReturn(List.of());
        List<CategoryTreeDTO> tree = categoryService.getTree();
        assertTrue(tree.isEmpty());
    }

    // ---- getById ----

    @Test
    void getById_existing_shouldReturnCategory() {
        Category cat = buildCategory(1, "餐饮", null, 1);
        when(categoryMapper.findById(1)).thenReturn(cat);

        Category result = categoryService.getById(1);
        assertEquals("餐饮", result.getName());
    }

    @Test
    void getById_notExisting_shouldThrowBusinessException() {
        when(categoryMapper.findById(999)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.getById(999));
        assertEquals(404, ex.getCode());
        assertEquals("分类不存在", ex.getMessage());
    }

    // ---- create ----

    @Test
    void create_topLevel_shouldSucceed() {
        Category input = new Category();
        input.setName("新一级");
        input.setSortOrder(1);

        when(categoryMapper.insert(any())).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(100);
            return 1;
        });

        Category result = categoryService.create(input);
        assertEquals(1, result.getLevel());
        assertEquals("新一级", result.getName());
        assertNotNull(result.getId());
    }

    @Test
    void create_subCategory_validParent_shouldSucceed() {
        Category parent = buildCategory(1, "餐饮", null, 1);
        Category input = new Category();
        input.setName("新子类");
        input.setParentId(1);

        when(categoryMapper.findById(1)).thenReturn(parent);
        when(categoryMapper.insert(any())).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(200);
            return 1;
        });

        Category result = categoryService.create(input);
        assertEquals(2, result.getLevel());
        assertEquals(1, result.getParentId());
    }

    @Test
    void create_subCategory_invalidParent_shouldThrow() {
        Category input = new Category();
        input.setName("新子类");
        input.setParentId(1);

        when(categoryMapper.findById(1)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.create(input));
        assertEquals("父分类不存在或不是一级分类", ex.getMessage());
    }

    @Test
    void create_subCategory_parentIsNotTopLevel_shouldThrow() {
        // 父分类级别是2（非一级）
        Category notParent = buildCategory(11, "早餐", 1, 2);
        Category input = new Category();
        input.setName("三级？");
        input.setParentId(11);

        when(categoryMapper.findById(11)).thenReturn(notParent);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> categoryService.create(input));
        assertEquals("父分类不存在或不是一级分类", ex.getMessage());
    }

    // ---- update ----

    @Test
    void update_shouldChangeNameAndSortOrder() {
        Category existing = buildCategory(1, "餐饮", null, 1);
        Category input = new Category();
        input.setName("美食餐饮");
        input.setSortOrder(99);

        when(categoryMapper.findById(1)).thenReturn(existing);
        when(categoryMapper.update(any())).thenReturn(1);

        Category result = categoryService.update(1, input);
        assertEquals("美食餐饮", result.getName());
        assertEquals(99, result.getSortOrder());
        verify(categoryMapper).update(existing);
    }

    // ---- delete ----

    @Test
    void delete_existing_shouldSucceed() {
        Category existing = buildCategory(11, "早餐", 1, 2);
        when(categoryMapper.findById(11)).thenReturn(existing);
        when(categoryMapper.deleteById(11)).thenReturn(1);

        assertDoesNotThrow(() -> categoryService.delete(11));
        verify(categoryMapper).deleteById(11);
    }

    @Test
    void delete_notExisting_shouldThrow() {
        when(categoryMapper.findById(999)).thenReturn(null);

        assertThrows(BusinessException.class, () -> categoryService.delete(999));
        verify(categoryMapper, never()).deleteById(any());
    }

    // ---- 工具方法 ----

    private Category buildCategory(Integer id, String name, Integer parentId, Integer level) {
        Category cat = new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setParentId(parentId);
        cat.setLevel(level);
        cat.setSortOrder(0);
        return cat;
    }
}
