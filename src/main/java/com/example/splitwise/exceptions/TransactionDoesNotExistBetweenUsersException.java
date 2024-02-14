package com.example.splitwise.exceptions;

public class TransactionDoesNotExistBetweenUsersException extends RuntimeException {
    public TransactionDoesNotExistBetweenUsersException(int payerId, int receiverId) {
        super("There has does not exist any transaction between payer: " + payerId + "and receiver: " + receiverId);
    }
}
