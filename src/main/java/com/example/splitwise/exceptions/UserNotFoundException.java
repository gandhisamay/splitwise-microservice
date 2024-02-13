package com.example.splitwise.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(int id) {
        super("User not found with ID: " + id);
    }
}
