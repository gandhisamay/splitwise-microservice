package com.example.splitwise.repositories;

import com.example.splitwise.models.SplitMoneyBalance;
import com.example.splitwise.models.SplitMoneyBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoneyBalanceRepository extends JpaRepository<SplitMoneyBalance, SplitMoneyBalanceId> {


    @Query("select mb.payer.userId, mb.payer.name, mb.receiver.userId, mb.receiver.name, mb.amount from SplitMoneyBalance mb where mb.moneyBalanceId.payerId = ?1 or mb.moneyBalanceId.receiverId = ?1")
    List<Object[]> getUserActivity(int userId);
}
