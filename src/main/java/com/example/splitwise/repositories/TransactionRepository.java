package com.example.splitwise.repositories;

import com.example.splitwise.models.transaction.SplitTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<SplitTransaction, Integer> {

    boolean existsByIdempotencyKey(int idempotencyKey);
}
