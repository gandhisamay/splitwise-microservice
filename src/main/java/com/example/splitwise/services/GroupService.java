package com.example.splitwise.services;

import com.example.splitwise.exceptions.GroupNotFoundException;
import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.group.SplitGroup;
import com.example.splitwise.models.group.SplitGroupRequest;
import com.example.splitwise.models.group.SplitGroupResponse;
import com.example.splitwise.models.user.SplitUser;
import com.example.splitwise.repositories.GroupRepository;
import com.example.splitwise.repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    GroupService(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<List<SplitGroupResponse>> getAllGroups() {
        List<SplitGroup> groups = groupRepository.findAll();
        List<SplitGroupResponse> groupResponse = groups.stream().map(SplitGroupResponse::toDto).toList();
        return ResponseEntity.ok().body(groupResponse);
    }

    public ResponseEntity<SplitGroupResponse> getGroupById(int groupId) throws GroupNotFoundException {
        SplitGroup group = groupRepository.findById(groupId).orElseThrow(() -> new GroupNotFoundException(groupId));
        SplitGroupResponse response = SplitGroupResponse.toDto(group);
        return ResponseEntity.ok().body(response);
    }

    private SplitGroup groupRequestParser(SplitGroupRequest splitGroupRequest) throws UserNotFoundException {
        List<SplitUser> groupMembers = userRepository.findAllById(splitGroupRequest.getGroupMembers());

        if (groupMembers.size() != splitGroupRequest.getGroupMembers().size()) {
            throw new UserNotFoundException();
        }

        return SplitGroup.builder().groupMembers(groupMembers).name(splitGroupRequest.getName()).build();
    }

    public ResponseEntity<String> createGroup(SplitGroupRequest splitGroupRequest) throws UserNotFoundException {
        SplitGroup group = groupRequestParser(splitGroupRequest);
        groupRepository.save(group);
        return ResponseEntity.created(URI.create("groupcreated")).body("Group created successfully");
    }

    public ResponseEntity<String> updateGroup(SplitGroupRequest splitGroupRequest) throws UserNotFoundException {
        SplitGroup updatedGroup = groupRequestParser(splitGroupRequest);
        updatedGroup.setGroupId(splitGroupRequest.getId());
        groupRepository.save(updatedGroup);
        return ResponseEntity.created(URI.create("groupcreated")).body("Group created successfully");
    }

    public ResponseEntity<String> deleteGroup(int groupId) {

        boolean groupExists = groupRepository.existsById(groupId);

        if (!groupExists)
            throw new GroupNotFoundException(groupId);

        groupRepository.deleteById(groupId);

        return ResponseEntity.ok().body("Group deleted successfully");
    }
}
