package com.example.splitwise.models.transaction;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
    private int transactionId;*/

    private int userId;
    private String name;
    private String email;
    private double amount;
}
