package com.example.todolist.service;

import com.example.todolist.dto.CategoryRequest;
import com.example.todolist.dto.CategoryResponse;
import com.example.todolist.entity.Category;
import com.example.todolist.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryService テストスイート")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryRequest categoryRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(1L);
        category.setName("テストカテゴリ");
        category.setDescription("テスト説明");
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRequest = new CategoryRequest();
        categoryRequest.setName("テストカテゴリ");
        categoryRequest.setDescription("テスト説明");
    }

    @Test
    @DisplayName("全カテゴリ取得 - 正常系")
    void testGetAllCategories() {
        // Arrange
        List<Category> categoryList = Arrays.asList(category);
        when(categoryRepository.findAll()).thenReturn(categoryList);

        // Act
        List<CategoryResponse> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("テストカテゴリ", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("IDでカテゴリ取得 - 正常系")
    void testGetCategoryById_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        // Act
        CategoryResponse result = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("テストカテゴリ", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("IDでカテゴリ取得 - 異常系（カテゴリ不在）")
    void testGetCategoryById_NotFound() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.getCategoryById(999L));
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("カテゴリ作成 - 正常系")
    void testCreateCategory() {
        // Arrange
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        // Act
        CategoryResponse result = categoryService.createCategory(categoryRequest);

        // Assert
        assertNotNull(result);
        assertEquals("テストカテゴリ", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("カテゴリ更新 - 正常系")
    void testUpdateCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        categoryRequest.setName("更新されたカテゴリ");

        // Act
        CategoryResponse result = categoryService.updateCategory(1L, categoryRequest);

        // Assert
        assertNotNull(result);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("カテゴリ削除 - 正常系")
    void testDeleteCategory() {
        // Arrange
        when(categoryRepository.existsById(1L)).thenReturn(true);
        doNothing().when(categoryRepository).deleteById(1L);

        // Act
        categoryService.deleteCategory(1L);

        // Assert
        verify(categoryRepository, times(1)).existsById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }
}
