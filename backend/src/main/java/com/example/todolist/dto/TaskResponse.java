package com.example.todolist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private Integer order;

    private Integer priority;

    private Boolean completed;

    private LocalDateTime dueDate;

    private Long categoryId;

    private String categoryName;

    private Long tagId;

    private String tagName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
