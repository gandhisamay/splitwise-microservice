package com.example.splitwise.services;

import com.example.splitwise.exceptions.TransactionDoesNotExistBetweenUsersException;
import com.example.splitwise.models.SplitMoneyBalance;
import com.example.splitwise.models.SplitMoneyBalanceId;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import jakarta.transaction.Transactional;
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

    @Transactional
    public ResponseEntity<String> settleUsers(int payerId, int receiverId) throws TransactionDoesNotExistBetweenUsersException {
        //now we need to settle them

        SplitMoneyBalanceId id = SplitMoneyBalanceId.builder().payerId(payerId).receiverId(receiverId).build();
        SplitMoneyBalanceId reverse = id.getReverseMapping();

        //now we have it ready ..just check for existence

        SplitMoneyBalance balance = moneyBalanceRepository.findById(id).orElse(null);

        if (balance != null) {
            //directly check it
            if (balance.getAmount() < 0)
                return ResponseEntity.badRequest().body("Payer does not owe any money to the receiver");

            balance.setAmount(0);
            moneyBalanceRepository.save(balance);
        }
        else{
            balance = moneyBalanceRepository.findById(reverse).orElseThrow(() -> new TransactionDoesNotExistBetweenUsersException(payerId, receiverId));
        }


        return ResponseEntity.ok().body("Users settled successfully");
    }
}
