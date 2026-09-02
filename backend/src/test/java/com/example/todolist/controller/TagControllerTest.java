package com.example.todolist.controller;

import com.example.todolist.dto.TagRequest;
import com.example.todolist.dto.TagResponse;
import com.example.todolist.service.TagService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagController.class)
@DisplayName("TagController テストスイート")
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @Autowired
    private ObjectMapper objectMapper;

    private TagResponse tagResponse;
    private TagRequest tagRequest;

    @BeforeEach
    void setUp() {
        tagResponse = new TagResponse();
        tagResponse.setId(1L);
        tagResponse.setName("テストタグ");
        tagResponse.setDescription("テスト説明");
        tagResponse.setCreatedAt(LocalDateTime.now());
        tagResponse.setUpdatedAt(LocalDateTime.now());

        tagRequest = new TagRequest();
        tagRequest.setName("テストタグ");
        tagRequest.setDescription("テスト説明");
    }

    @Test
    @DisplayName("GET /api/v1/tags - 全タグ取得")
    void testGetAllTags() throws Exception {
        // Arrange
        List<TagResponse> tagList = Arrays.asList(tagResponse);
        when(tagService.getAllTags()).thenReturn(tagList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("テストタグ"));

        verify(tagService, times(1)).getAllTags();
    }

    @Test
    @DisplayName("GET /api/v1/tags/{id} - 特定タグ取得")
    void testGetTagById() throws Exception {
        // Arrange
        when(tagService.getTagById(1L)).thenReturn(tagResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("テストタグ"));

        verify(tagService, times(1)).getTagById(1L);
    }

    @Test
    @DisplayName("POST /api/v1/tags - タグ作成")
    void testCreateTag() throws Exception {
        // Arrange
        when(tagService.createTag(any(TagRequest.class))).thenReturn(tagResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("テストタグ"));

        verify(tagService, times(1)).createTag(any(TagRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/tags/{id} - タグ更新")
    void testUpdateTag() throws Exception {
        // Arrange
        when(tagService.updateTag(eq(1L), any(TagRequest.class))).thenReturn(tagResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/tags/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tagRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(tagService, times(1)).updateTag(eq(1L), any(TagRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tags/{id} - タグ削除")
    void testDeleteTag() throws Exception {
        // Arrange
        doNothing().when(tagService).deleteTag(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tags/1"))
                .andExpect(status().isNoContent());

        verify(tagService, times(1)).deleteTag(1L);
    }
}
