package com.example.splitwise.models.group;

import com.example.splitwise.models.user.SplitUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SplitGroup {
    @Id
    @GeneratedValue
    @Column(name = "group_id")
    private int groupId;
    private String name;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
   // @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private List<SplitUser> groupMembers;
}
