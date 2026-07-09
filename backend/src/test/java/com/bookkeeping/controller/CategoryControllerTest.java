package com.bookkeeping.controller;

import com.bookkeeping.common.Result;
import com.bookkeeping.dto.CategoryTreeDTO;
import com.bookkeeping.entity.Category;
import com.bookkeeping.exception.BusinessException;
import com.bookkeeping.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    // ---- GET /api/categories ----

    @Test
    void getTree_shouldReturnTree() throws Exception {
        CategoryTreeDTO dto = new CategoryTreeDTO();
        dto.setId(1);
        dto.setName("餐饮");
        dto.setLevel(1);
        dto.setChildren(List.of());

        when(categoryService.getTree()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("餐饮"));
    }

    // ---- GET /api/categories/{id} ----

    @Test
    void getById_existing_shouldReturnCategory() throws Exception {
        Category cat = new Category();
        cat.setId(1);
        cat.setName("餐饮");
        cat.setLevel(1);

        when(categoryService.getById(1)).thenReturn(cat);

        mockMvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("餐饮"));
    }

    @Test
    void getById_notFound_shouldReturnError() throws Exception {
        when(categoryService.getById(999))
                .thenThrow(new BusinessException(404, "分类不存在"));

        mockMvc.perform(get("/api/categories/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("分类不存在"));
    }

    // ---- POST /api/categories ----

    @Test
    void create_validCategory_shouldReturnCreated() throws Exception {
        Category input = new Category();
        input.setName("新分类");
        input.setSortOrder(1);

        Category saved = new Category();
        saved.setId(100);
        saved.setName("新分类");
        saved.setLevel(1);

        when(categoryService.create(any())).thenReturn(saved);

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("新分类"))
                .andExpect(jsonPath("$.data.id").value(100));
    }

    // ---- PUT /api/categories/{id} ----

    @Test
    void update_valid_shouldReturnUpdated() throws Exception {
        Category input = new Category();
        input.setName("改名后");
        input.setSortOrder(99);

        Category updated = new Category();
        updated.setId(1);
        updated.setName("改名后");
        updated.setSortOrder(99);

        when(categoryService.update(eq(1), any())).thenReturn(updated);

        mockMvc.perform(put("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("改名后"));
    }

    // ---- DELETE /api/categories/{id} ----

    @Test
    void delete_existing_shouldReturnSuccess() throws Exception {
        doNothing().when(categoryService).delete(1);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void delete_notFound_shouldReturnError() throws Exception {
        doThrow(new BusinessException(404, "分类不存在"))
                .when(categoryService).delete(999);

        mockMvc.perform(delete("/api/categories/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }
}
