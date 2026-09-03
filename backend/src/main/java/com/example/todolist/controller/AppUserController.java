package com.example.todolist.controller;

import com.example.todolist.dto.UserRequest;
import com.example.todolist.dto.UserResponse;
import com.example.todolist.service.AppUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppUserController {

    private final AppUserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> addUserToGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        userService.addUserToGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/groups/{groupId}")
    public ResponseEntity<Void> removeUserFromGroup(
            @PathVariable Long userId,
            @PathVariable Long groupId) {
        userService.removeUserFromGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/tasks/{taskId}")
    public ResponseEntity<Void> assignTaskToUser(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        userService.assignTaskToUser(userId, taskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{userId}/tasks/{taskId}")
    public ResponseEntity<Void> unassignTaskFromUser(
            @PathVariable Long userId,
            @PathVariable Long taskId) {
        userService.unassignTaskFromUser(userId, taskId);
        return ResponseEntity.noContent().build();
    }
}
