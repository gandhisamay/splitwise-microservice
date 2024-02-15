package com.example.splitwise.controllers;

import com.example.splitwise.models.group.SplitGroup;
import com.example.splitwise.models.group.SplitGroupRequest;
import com.example.splitwise.models.group.SplitGroupResponse;
import com.example.splitwise.services.GroupService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<SplitGroupResponse>> getAllGroups() {
        return groupService.getAllGroups();
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<SplitGroupResponse> getGroupById(@PathVariable int groupId) {
        return groupService.getGroupById(groupId);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createNewGroup(SplitGroupRequest splitGroupRequest) {
        return groupService.createGroup(splitGroupRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateGroup(SplitGroupRequest splitGroupRequest) {
        return groupService.updateGroup(splitGroupRequest);
    }

    @DeleteMapping("/delete/{groupId}")
    public ResponseEntity<String> deleteGroup(@PathVariable int groupId) {
        return groupService.deleteGroup(groupId);
    }
}
