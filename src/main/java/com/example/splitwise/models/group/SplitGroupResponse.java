package com.example.splitwise.models.group;

import com.example.splitwise.models.user.SplitUserCompressed;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class SplitGroupResponse {
    private int groupId;
    private String name;
    private List<SplitUserCompressed> groupMembers;


    public static SplitGroupResponse toDto(SplitGroup group) {
        List<SplitUserCompressed> groupMembers = group.getGroupMembers().parallelStream().map(member -> SplitUserCompressed.builder().userId(member.getUserId()).email(member.getEmail()).name(member.getName()).build()).toList();
        return SplitGroupResponse.builder().groupId(group.getGroupId()).name(group.getName()).groupMembers(groupMembers).build();
    }
}
