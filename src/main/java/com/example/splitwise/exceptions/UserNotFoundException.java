package com.example.splitwise.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(int id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException() {
        super("Error finding requested user not found");
    }
}
