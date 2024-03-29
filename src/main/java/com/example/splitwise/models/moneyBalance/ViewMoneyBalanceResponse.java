package com.example.splitwise.models.moneyBalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewMoneyBalanceResponse {

    private String name;
    private double amount;
    private int groupId;
}
