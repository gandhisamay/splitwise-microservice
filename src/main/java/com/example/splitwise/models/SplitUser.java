package com.example.splitwise.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class SplitUser{
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private int userId;
    private String email;
    private String name;
}
