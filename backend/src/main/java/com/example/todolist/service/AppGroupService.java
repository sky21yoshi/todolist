package com.example.todolist.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.todolist.dto.GroupRequest;
import com.example.todolist.dto.GroupResponse;
import com.example.todolist.entity.AppGroup;
import com.example.todolist.repository.AppGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppGroupService {

    private final AppGroupRepository groupRepository;

    @Transactional(readOnly = true)
    public List<GroupResponse> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        return convertToResponse(findGroup(id));
    }

    @Transactional
    public GroupResponse createGroup(GroupRequest request) {
        AppGroup group = new AppGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return convertToResponse(groupRepository.save(group));
    }

    @Transactional
    public GroupResponse updateGroup(Long id, GroupRequest request) {
        AppGroup group = findGroup(id);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        return convertToResponse(groupRepository.save(group));
    }

    @Transactional
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new IllegalArgumentException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }

    private AppGroup findGroup(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + id));
    }

    private GroupResponse convertToResponse(AppGroup group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setCreatedAt(group.getCreatedAt());
        response.setUpdatedAt(group.getUpdatedAt());
        return response;
    }
}
