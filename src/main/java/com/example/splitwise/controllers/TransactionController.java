package com.example.splitwise.controllers;

import com.example.splitwise.models.SplitTransaction;
import com.example.splitwise.models.SplitTransactionRequest;
import com.example.splitwise.models.SplitTransactionResponse;
import com.example.splitwise.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SplitTransaction>> getAllTransactions() {
        return transactionService.getAllTransactions();
    }


    @GetMapping("/{id}")
    public ResponseEntity<SplitTransactionResponse> getTransactionById(@PathVariable int id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/new")
    public ResponseEntity<String> createTransaction(@RequestBody SplitTransactionRequest transactionRequest) {
        return transactionService.createTransaction(transactionRequest);
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTransaction(@RequestBody SplitTransaction transaction) {
        return transactionService.updateTransaction(transaction);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable  int id) {
        return transactionService.deleteTransaction(id);
    }
}
