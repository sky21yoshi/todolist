package com.example.todolist.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.todolist.dto.CategoryRequest;
import com.example.todolist.dto.CategoryResponse;
import com.example.todolist.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CategoryController.class)
@DisplayName("CategoryController テストスイート")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        categoryResponse = new CategoryResponse();
        categoryResponse.setId(1L);
        categoryResponse.setName("テストカテゴリ");
        categoryResponse.setDescription("テスト説明");
        categoryResponse.setCreatedAt(LocalDateTime.now());
        categoryResponse.setUpdatedAt(LocalDateTime.now());

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("テストカテゴリ");
        categoryRequest.setDescription("テスト説明");
    }

    @Test
    @DisplayName("GET /api/v1/categories - 全カテゴリ取得")
    void testGetAllCategories() throws Exception {
        // Arrange
        List<CategoryResponse> categoryList = Arrays.asList(categoryResponse);
        when(categoryService.getAllCategories()).thenReturn(categoryList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("テストカテゴリ"));

        verify(categoryService, times(1)).getAllCategories();
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} - 特定カテゴリ取得")
    void testGetCategoryById() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(1L)).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("テストカテゴリ"));

        verify(categoryService, times(1)).getCategoryById(1L);
    }

    @Test
    @DisplayName("POST /api/v1/categories - カテゴリ作成")
    void testCreateCategory() throws Exception {
        // Arrange
        when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("テストカテゴリ"));

        verify(categoryService, times(1)).createCategory(any(CategoryRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/categories/{id} - カテゴリ更新")
    void testUpdateCategory() throws Exception {
        // Arrange
        when(categoryService.updateCategory(eq(1L), any(CategoryRequest.class))).thenReturn(categoryResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(categoryService, times(1)).updateCategory(eq(1L), any(CategoryRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/categories/{id} - カテゴリ削除")
    void testDeleteCategory() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(1L);
    }
}
