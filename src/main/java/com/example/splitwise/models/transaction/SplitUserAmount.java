package com.example.splitwise.models.transaction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
/*@NoArgsConstructor
@AllArgsConstructor
@Entity*/
public class SplitUserAmount {
/*    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;*/

    private int userId;
    private String name;
    private String email;
    private double amount;
}
