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
import static org.mockito.Mockito.never;
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

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TaskController.class)
@DisplayName("TaskController テストスイート")
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskResponse taskResponse;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        taskResponse = new TaskResponse();
        taskResponse.setId(1L);
        taskResponse.setTitle("テストタスク");
        taskResponse.setDescription("テスト説明");
        taskResponse.setOrder(0);
        taskResponse.setPriority(1);
        taskResponse.setCompleted(false);
        taskResponse.setCreatedAt(LocalDateTime.now());
        taskResponse.setUpdatedAt(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("テストタスク");
        taskRequest.setDescription("テスト説明");
        taskRequest.setOrder(0);
        taskRequest.setPriority(1);
        taskRequest.setCompleted(false);
    }

    @Test
    @DisplayName("GET /api/v1/tasks - 全タスク取得")
    void testGetAllTasks() throws Exception {
        // Arrange
        List<TaskResponse> taskList = Arrays.asList(taskResponse);
        when(taskService.getAllTasks()).thenReturn(taskList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("テストタスク"));

        verify(taskService, times(1)).getAllTasks();
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} - 特定タスク取得")
    void testGetTaskById() throws Exception {
        // Arrange
        when(taskService.getTaskById(1L)).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("テストタスク"));

        verify(taskService, times(1)).getTaskById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/tasks/filter/completed - 完了状態でフィルター")
    void testGetCompletedTasks() throws Exception {
        // Arrange
        taskResponse.setCompleted(true);
        List<TaskResponse> taskList = Arrays.asList(taskResponse);
        when(taskService.getTasksByCompleted(true)).thenReturn(taskList);

        // Act & Assert
        mockMvc.perform(get("/api/v1/tasks/filter/completed")
                .param("completed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(true));

        verify(taskService, times(1)).getTasksByCompleted(true);
    }

    @Test
    @DisplayName("POST /api/v1/tasks - タスク作成")
    void testCreateTask() throws Exception {
        // Arrange
        when(taskService.createTask(any(TaskRequest.class))).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("テストタスク"));

        verify(taskService, times(1)).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("POST /api/v1/tasks - タイトル未入力で作成失敗")
    void testCreateTask_MissingTitle() throws Exception {
        // Arrange
        taskRequest.setTitle("");

        // Act & Assert
        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isBadRequest());

        verify(taskService, never()).createTask(any(TaskRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} - タスク更新")
    void testUpdateTask() throws Exception {
        // Arrange
        when(taskService.updateTask(eq(1L), any(TaskRequest.class))).thenReturn(taskResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(taskService, times(1)).updateTask(eq(1L), any(TaskRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} - タスク削除")
    void testDeleteTask() throws Exception {
        // Arrange
        doNothing().when(taskService).deleteTask(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteTask(1L);
    }
}
