package com.example.splitwise.models.transaction;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SplitTransactionUpdateRequest {
    private SplitTransactionRequest transactionRequest;
    private List<String> updates;
}
