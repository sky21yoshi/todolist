package com.example.todolist.service;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.entity.Task;
import com.example.todolist.entity.TaskEx;
import com.example.todolist.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    @Transactional(readOnly = true)
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        return convertToResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByCompleted(Boolean completed) {
        return taskRepository.findAllByCompleted(completed).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        task.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        task.setCompleted(request.getCompleted() != null ? request.getCompleted() : false);

        Task savedTask = taskRepository.save(task);

        // Create TaskEx when extended properties are provided
        if (request.getDueDate() != null) {
            TaskEx taskEx = new TaskEx();
            taskEx.setTask(savedTask);
            taskEx.setDueDate(request.getDueDate());
        }

        return convertToResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : task.getDisplayOrder());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setCompleted(request.getCompleted() != null ? request.getCompleted() : task.getCompleted());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setDisplayOrder(task.getDisplayOrder());
        response.setPriority(task.getPriority());
        response.setCompleted(task.getCompleted());
        response.setCategoryIds(task.getCategories().stream().map(category -> category.getId()).collect(Collectors.toSet()));
        response.setTagIds(task.getTags().stream().map(tag -> tag.getId()).collect(Collectors.toSet()));
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
