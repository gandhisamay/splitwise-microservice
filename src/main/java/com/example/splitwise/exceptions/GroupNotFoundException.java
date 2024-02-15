package com.example.splitwise.exceptions;

public class GroupNotFoundException extends RuntimeException {

    public GroupNotFoundException(int id) {
        super("Group not found with id: " + id);
    }
}
