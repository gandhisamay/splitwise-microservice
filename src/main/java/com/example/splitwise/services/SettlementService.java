package com.example.splitwise.services;

import com.example.splitwise.models.SplitMoneyBalance;
import com.example.splitwise.models.SplitMoneyBalanceId;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SettlementService {

    private final MoneyBalanceRepository moneyBalanceRepository;

    SettlementService(MoneyBalanceRepository moneyBalanceRepository) {
        this.moneyBalanceRepository = moneyBalanceRepository;
    }

    public ResponseEntity<String> settleUsers(int payerId, int receiverId) {
        //now we need to settle them

        SplitMoneyBalanceId id = SplitMoneyBalanceId.builder().payerId(payerId).receiverId(receiverId).build();
        SplitMoneyBalanceId reverse = id.getReverseMapping();

        SplitMoneyBalance balance = moneyBalanceRepository.findById(id).orElse(null);

        if (balance == null) {
            return ResponseEntity.badRequest().body("Invalid payer or receiver");
        }

        if (balance.getAmount() >= 0) {
            return ResponseEntity.badRequest().body("Payer does not owe any money to the receiver");
        }


        SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(reverse).get();

        balance.setAmount(0);
        reverseBalance.setAmount(0);

        moneyBalanceRepository.save(balance);
        moneyBalanceRepository.save(reverseBalance);

        return ResponseEntity.ok().body("Users settled successfully");
    }
}
