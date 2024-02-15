package com.example.splitwise.controllers;

import com.example.splitwise.models.group.SplitGroup;
import com.example.splitwise.models.group.SplitGroupRequest;
import com.example.splitwise.models.group.SplitGroupResponse;
import com.example.splitwise.services.GroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SplitGroupResponse>> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<SplitGroupResponse> getGroupById(@PathVariable int groupId) {
        return groupService.getGroupById(groupId);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createNewGroup(@RequestBody SplitGroupRequest splitGroupRequest) {
        return groupService.createGroup(splitGroupRequest);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> updateGroup(@RequestBody SplitGroupRequest splitGroupRequest) {
        return groupService.updateGroup(splitGroupRequest);
    }

    @DeleteMapping("/delete/{groupId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteGroup(@PathVariable int groupId) {
        return groupService.deleteGroup(groupId);
    }
}
