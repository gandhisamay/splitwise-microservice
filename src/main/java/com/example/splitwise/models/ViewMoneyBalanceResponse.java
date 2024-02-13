package com.example.splitwise.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViewMoneyBalanceResponse {

    private Map<String, Double> moneyBalances;
    private String message;
    private int status;
}
