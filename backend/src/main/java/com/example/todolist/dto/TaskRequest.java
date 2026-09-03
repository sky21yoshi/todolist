package com.example.todolist.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Integer displayOrder = 0;

    private Integer priority = 0;

    private Boolean completed = false;

    private LocalDateTime dueDate;

    private List<Long> categoryIds;

    private List<Long> tagIds;
}
