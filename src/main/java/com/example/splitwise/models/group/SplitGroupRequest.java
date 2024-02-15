package com.example.splitwise.models.group;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SplitGroupRequest {
   private Integer id;
   private String name;
   private List<Integer> groupMembers;
}
