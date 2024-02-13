package com.example.splitwise.controllers;

import com.example.splitwise.models.SplitTransaction;
import com.example.splitwise.models.SplitTransactionRequest;
import com.example.splitwise.models.SplitTransactionResponse;
import com.example.splitwise.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SplitTransaction>> getAllTransactions() {
        return transactionService.getAllTransactions();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<SplitTransactionResponse> getTransactionById(@PathVariable int id) {
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createTransaction(@RequestBody SplitTransactionRequest transactionRequest) {
        return transactionService.createTransaction(transactionRequest);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> updateTransaction(@RequestBody SplitTransaction transaction) {
        return transactionService.updateTransaction(transaction);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteTransaction(@PathVariable  int id) {
        return transactionService.deleteTransaction(id);
    }
}
