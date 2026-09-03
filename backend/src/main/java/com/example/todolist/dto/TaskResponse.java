package com.example.todolist.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private Integer displayOrder;

    private Integer priority;

    private Boolean completed;

    private LocalDateTime dueDate;

    private Set<Long> categoryIds;

    private Set<Long> tagIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
