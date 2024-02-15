package com.example.splitwise.exceptions;

public class TransactionNotFoundException extends  RuntimeException{
    public TransactionNotFoundException(int id) {
        super("Transaction does not exist with id: " + id);
    }
}
