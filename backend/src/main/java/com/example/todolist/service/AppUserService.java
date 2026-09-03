package com.example.todolist.service;

import com.example.todolist.dto.UserRequest;
import com.example.todolist.dto.UserResponse;
import com.example.todolist.entity.AppUser;
import com.example.todolist.entity.AppGroup;
import com.example.todolist.entity.Task;
import com.example.todolist.repository.AppGroupRepository;
import com.example.todolist.repository.AppUserRepository;
import com.example.todolist.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository userRepository;
    private final AppGroupRepository groupRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return convertToResponse(findUser(id));
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        AppUser user = new AppUser();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setExpiresAt(request.getExpiresAt());
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        AppUser user = findUser(id);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setExpiresAt(request.getExpiresAt());
        return convertToResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void addUserToGroup(Long userId, Long groupId) {
        AppUser user = findUser(userId);
        AppGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));
        user.getGroups().add(group);
        userRepository.save(user);
    }

    @Transactional
    public void removeUserFromGroup(Long userId, Long groupId) {
        AppUser user = findUser(userId);
        user.getGroups().removeIf(group -> group.getId().equals(groupId));
        userRepository.save(user);
    }

    @Transactional
    public void assignTaskToUser(Long userId, Long taskId) {
        AppUser user = findUser(userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + taskId));
        user.getTasks().add(task);
        userRepository.save(user);
    }

    @Transactional
    public void unassignTaskFromUser(Long userId, Long taskId) {
        AppUser user = findUser(userId);
        user.getTasks().removeIf(task -> task.getId().equals(taskId));
        userRepository.save(user);
    }

    private AppUser findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    private UserResponse convertToResponse(AppUser user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setExpiresAt(user.getExpiresAt());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
