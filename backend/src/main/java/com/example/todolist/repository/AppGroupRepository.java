package com.example.todolist.repository;

import com.example.todolist.entity.AppGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppGroupRepository extends JpaRepository<AppGroup, Long> {
}
