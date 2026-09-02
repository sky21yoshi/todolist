package com.example.todolist.service;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.entity.Task;
import com.example.todolist.repository.TaskRepository;
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
@DisplayName("TaskService テストスイート")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("テストタスク");
        task.setDescription("テスト説明");
        task.setOrder(0);
        task.setPriority(1);
        task.setCompleted(false);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("テストタスク");
        taskRequest.setDescription("テスト説明");
        taskRequest.setOrder(0);
        taskRequest.setPriority(1);
        taskRequest.setCompleted(false);
    }

    @Test
    @DisplayName("全タスク取得 - 正常系")
    void testGetAllTasks() {
        // Arrange
        List<Task> taskList = Arrays.asList(task);
        when(taskRepository.findAll()).thenReturn(taskList);

        // Act
        List<TaskResponse> result = taskService.getAllTasks();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("テストタスク", result.get(0).getTitle());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("IDでタスク取得 - 正常系")
    void testGetTaskById_Success() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        // Act
        TaskResponse result = taskService.getTaskById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("テストタスク", result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("IDでタスク取得 - 異常系（タスク不在）")
    void testGetTaskById_NotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(999L));
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("完了状態でフィルター - 正常系")
    void testGetTasksByCompleted() {
        // Arrange
        task.setCompleted(true);
        List<Task> taskList = Arrays.asList(task);
        when(taskRepository.findAllByCompleted(true)).thenReturn(taskList);

        // Act
        List<TaskResponse> result = taskService.getTasksByCompleted(true);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getCompleted());
        verify(taskRepository, times(1)).findAllByCompleted(true);
    }

    @Test
    @DisplayName("タスク作成 - 正常系")
    void testCreateTask() {
        // Arrange
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskResponse result = taskService.createTask(taskRequest);

        // Assert
        assertNotNull(result);
        assertEquals("テストタスク", result.getTitle());
        assertEquals(0, result.getOrder());
        assertEquals(1, result.getPriority());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("タスク更新 - 正常系")
    void testUpdateTask() {
        // Arrange
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        taskRequest.setTitle("更新されたタスク");
        taskRequest.setCompleted(true);

        // Act
        TaskResponse result = taskService.updateTask(1L, taskRequest);

        // Assert
        assertNotNull(result);
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("タスク更新 - 異常系（タスク不在）")
    void testUpdateTask_NotFound() {
        // Arrange
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(999L, taskRequest));
        verify(taskRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("タスク削除 - 正常系")
    void testDeleteTask() {
        // Arrange
        when(taskRepository.existsById(1L)).thenReturn(true);
        doNothing().when(taskRepository).deleteById(1L);

        // Act
        taskService.deleteTask(1L);

        // Assert
        verify(taskRepository, times(1)).existsById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("タスク削除 - 異常系（タスク不在）")
    void testDeleteTask_NotFound() {
        // Arrange
        when(taskRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> taskService.deleteTask(999L));
        verify(taskRepository, times(1)).existsById(999L);
    }
}
