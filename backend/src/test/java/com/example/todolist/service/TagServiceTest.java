package com.example.todolist.service;

import com.example.todolist.dto.TagRequest;
import com.example.todolist.dto.TagResponse;
import com.example.todolist.entity.Tag;
import com.example.todolist.repository.TagRepository;
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
@DisplayName("TagService テストスイート")
class TagServiceTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagService tagService;

    private Tag tag;
    private TagRequest tagRequest;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("テストタグ");
        tag.setDescription("テスト説明");
        tag.setCreatedAt(LocalDateTime.now());
        tag.setUpdatedAt(LocalDateTime.now());

        tagRequest = new TagRequest();
        tagRequest.setName("テストタグ");
        tagRequest.setDescription("テスト説明");
    }

    @Test
    @DisplayName("全タグ取得 - 正常系")
    void testGetAllTags() {
        // Arrange
        List<Tag> tagList = Arrays.asList(tag);
        when(tagRepository.findAll()).thenReturn(tagList);

        // Act
        List<TagResponse> result = tagService.getAllTags();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("テストタグ", result.get(0).getName());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("IDでタグ取得 - 正常系")
    void testGetTagById_Success() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));

        // Act
        TagResponse result = tagService.getTagById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("テストタグ", result.getName());
        verify(tagRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("IDでタグ取得 - 異常系（タグ不在）")
    void testGetTagById_NotFound() {
        // Arrange
        when(tagRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> tagService.getTagById(999L));
        verify(tagRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("タグ作成 - 正常系")
    void testCreateTag() {
        // Arrange
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        // Act
        TagResponse result = tagService.createTag(tagRequest);

        // Assert
        assertNotNull(result);
        assertEquals("テストタグ", result.getName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("タグ更新 - 正常系")
    void testUpdateTag() {
        // Arrange
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        tagRequest.setName("更新されたタグ");

        // Act
        TagResponse result = tagService.updateTag(1L, tagRequest);

        // Assert
        assertNotNull(result);
        verify(tagRepository, times(1)).findById(1L);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    @DisplayName("タグ削除 - 正常系")
    void testDeleteTag() {
        // Arrange
        when(tagRepository.existsById(1L)).thenReturn(true);
        doNothing().when(tagRepository).deleteById(1L);

        // Act
        tagService.deleteTag(1L);

        // Assert
        verify(tagRepository, times(1)).existsById(1L);
        verify(tagRepository, times(1)).deleteById(1L);
    }
}
