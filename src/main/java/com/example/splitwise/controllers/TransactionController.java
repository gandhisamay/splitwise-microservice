package com.example.splitwise.controllers;

import com.example.splitwise.exceptions.TransactionNotFoundException;
import com.example.splitwise.models.transaction.SplitTransaction;
import com.example.splitwise.models.transaction.SplitTransactionRequest;
import com.example.splitwise.models.transaction.SplitTransactionResponse;
import com.example.splitwise.models.transaction.SplitTransactionUpdateRequest;
import com.example.splitwise.services.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<SplitTransactionResponse>> getAllTransactions() {
        return transactionService.getAllTransactions();
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<SplitTransactionResponse> getTransactionById(@PathVariable int id) throws TransactionNotFoundException{
        return transactionService.getTransactionById(id);
    }

    @PostMapping("/new")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> createTransaction(@RequestBody SplitTransactionRequest transactionRequest) {
        return transactionService.createTransaction(transactionRequest);
    }

    @PostMapping("/new/many")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createManyTransactions(@RequestBody List<SplitTransactionRequest> transactionRequests) {
        transactionRequests.stream().forEach(transactionService::createTransaction);
        return ResponseEntity.created(URI.create("transactionscreated")).body("Transactions created successfully");
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> updateTransaction(@RequestBody SplitTransactionUpdateRequest transactionUpdateRequest) {
        return transactionService.updateTransaction(transactionUpdateRequest);
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_USER')")
    public ResponseEntity<String> deleteTransaction(@PathVariable int id) {
        return transactionService.deleteTransaction(id);
    }
}
