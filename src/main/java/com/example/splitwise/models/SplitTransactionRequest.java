package com.example.splitwise.models;

import lombok.Data;
import java.util.Map;

@Data
public class SplitTransactionRequest {
   private  int payerId;
   private  Map<Integer, Double> participantsAmounts;
   private double amount;
   private int groupId;
   private String description;
}
