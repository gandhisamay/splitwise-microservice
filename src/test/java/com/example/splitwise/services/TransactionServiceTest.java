package com.example.splitwise.services;

import com.example.splitwise.models.transaction.SplitTransaction;
import com.example.splitwise.models.SplitUser;
import com.example.splitwise.repositories.TransactionRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

//write the unit tests here.
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private PasswordEncoder passwordEncoder;

    private SplitTransaction transaction;
    private SplitUser payer;
    private Map<SplitUser, Double> participantsAmounts;

    @BeforeEach
    void setup() {
        payer = SplitUser.builder()
                .userId(1)
                .email("gandhisamay200@gmail.com")
                .name("Samay Gandhi")
                .password(passwordEncoder.encode("test"))
                .roles("ROLE_ADMIN,ROLE_USER")
                .build();


        transaction = SplitTransaction.builder()
                .idempotencyKey(112)
                .amount(28)
                .description("Tea")
                .category("Food & Dining")
                .groupId(3)
                .payer(payer)

                .build();
    }

    @Test
    void getAllTransactions() {
    }

    @Test
    void getTransactionById() {
    }

    @Test
    void createTransaction() {
    }

    @Test
    void deleteTransaction() {
    }

    @Test
    void updateTransaction() {
    }
}