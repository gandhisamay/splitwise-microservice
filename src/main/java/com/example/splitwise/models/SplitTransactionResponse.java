package com.example.splitwise.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SplitTransactionResponse {
    private SplitTransaction transaction;
    private String message;
}
