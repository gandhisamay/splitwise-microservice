package com.example.splitwise.services;

import com.example.splitwise.models.moneyBalance.ViewMoneyBalanceResponse;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import org.springframework.http.HttpStatus;
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

    public List<ViewMoneyBalanceResponse> getUserActivity(int userId) {
        //respond now
        //select mb.payer.userId, mb.payer.name, mb.receiver.userId, mb.receiver.name, mb.amount from SplitMoneyBalance mb where mb.moneyBalanceId.payerId = ?1 or mb.moneyBalanceId.receiverId = ?1
        List<Object[]> balances = moneyBalanceRepository.getUserActivity(userId);


        Map<String, Double> activityMap = balances.parallelStream().filter(row -> (Integer) row[0] == userId).collect(Collectors.toMap(row -> (String) row[3], row -> (double) row[4]));
        balances.parallelStream().filter(row -> (Integer) row[2] == userId).forEach(row -> activityMap.put((String) row[1], -(double) row[4]));

        List<ViewMoneyBalanceResponse> response = new java.util.ArrayList<>(balances.parallelStream().filter(row -> (Integer) row[0] == userId).map(row -> ViewMoneyBalanceResponse.builder().name((String) row[3]).amount((double) row[5]).groupId((int) row[4]).build()).toList());
        balances.parallelStream().filter(row -> (Integer) row[2] == userId).forEach(row -> response.add((ViewMoneyBalanceResponse.builder().amount((double) row[5]).name((String) row[1]).groupId((int) row[4]).build())));

        return response;
    }
}
