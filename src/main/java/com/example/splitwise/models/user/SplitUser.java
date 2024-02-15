package com.example.splitwise.models.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitUser {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private int userId;
    private String email;
    private String name;
    private String password;
    private String roles;
}
