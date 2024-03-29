package com.example.splitwise.models.transaction;

import lombok.Data;
import java.util.Map;

@Data
public class SplitTransactionRequest {
   private Integer transactionId;
   private  Integer idempotencyKey;
   private  int payerId;
   private  Map<Integer, Double> participantsAmounts;
   private double amount;
   private int groupId;
   private String description;
   private  String category;
}
