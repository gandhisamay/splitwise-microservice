package com.example.splitwise.services;

import com.example.splitwise.models.SplitMoneyBalance;
import com.example.splitwise.models.ViewMoneyBalanceResponse;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ViewService {

    private final MoneyBalanceRepository moneyBalanceRepository;

    ViewService(MoneyBalanceRepository moneyBalanceRepository) {
        this.moneyBalanceRepository = moneyBalanceRepository;
    }

    public ViewMoneyBalanceResponse getUserActivity(int userId) {
        //respond now
        List<Object[]> balances = moneyBalanceRepository.getUserActivity(userId);
        Map<String, Double> activityMap = balances.stream().collect(Collectors.toMap(row -> (String) row[0], row -> (double) row[1]));

        return ViewMoneyBalanceResponse.builder().moneyBalances(activityMap).message("User balances found").status(200).build();
    }
}
