package com.example.splitwise.services;


import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.*;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import com.example.splitwise.repositories.TransactionRepository;
import com.example.splitwise.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    private final MoneyBalanceRepository moneyBalanceRepository;

    TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, MoneyBalanceRepository moneyBalanceRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.moneyBalanceRepository = moneyBalanceRepository;
    }

    public ResponseEntity<List<SplitTransaction>> getAllTransactions() {
        List<SplitTransaction> transactions = transactionRepository.findAll();
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    public ResponseEntity<SplitTransactionResponse> getTransactionById(int id) {
        Optional<SplitTransaction> transaction = transactionRepository.findById(id);

        if (transaction.isPresent()) {

            SplitTransactionResponse successResponse = SplitTransactionResponse.builder().transaction(transaction.get()).message("success").build();
            return new ResponseEntity<>(successResponse, HttpStatus.OK);
        }

        SplitTransactionResponse failureResponse = SplitTransactionResponse.builder().message("no transaction with this id exists").build();

        return new ResponseEntity<>(failureResponse, HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<String> createTransaction(SplitTransactionRequest transactionRequest) {

        SplitUser payer = userRepository.findById(transactionRequest.getPayerId()).orElseThrow(() -> new UserNotFoundException(10));
        List<SplitUser> participants = userRepository.findAllById(transactionRequest.getParticipantsAmounts().keySet());


        if (participants.size() != transactionRequest.getParticipantsAmounts().size()) {
            return ResponseEntity.badRequest().body("Participants ids are invalid");
        }

        Map<SplitUser, Double> updatedMap = transactionRequest.getParticipantsAmounts().entrySet().stream().collect(HashMap::new, (map, entry) -> map.put(userRepository.findById(entry.getKey()).orElseThrow(() -> new UserNotFoundException(entry.getKey())), entry.getValue()), HashMap::putAll);


        participants.forEach((receiver) -> {
            //find if the entry exists
            SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transactionRequest.getPayerId()).receiverId(receiver.getUserId()).build();
            SplitMoneyBalanceId inverse = SplitMoneyBalanceId.builder().payerId(receiver.getUserId()).payerId(transactionRequest.getPayerId()).build();


            SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
            SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);


            double amountOwed = transactionRequest.getParticipantsAmounts().get(receiver.getUserId());

            if (directBalance == null && reverseBalance == null) {

                //we need to create here
                SplitMoneyBalance newBalance = SplitMoneyBalance.builder().amount(amountOwed).payer(payer).moneyBalanceId(new SplitMoneyBalanceId(payer.getUserId(), receiver.getUserId())).receiver(receiver).build();
                moneyBalanceRepository.save(newBalance);

            } else if (reverseBalance != null) {
                reverseBalance.setAmount(reverseBalance.getAmount() - amountOwed);
                moneyBalanceRepository.save(reverseBalance);
            } else {
                directBalance.setAmount(directBalance.getAmount() + amountOwed);
                moneyBalanceRepository.save(directBalance);
            }
        });

        SplitTransaction transaction = SplitTransaction.builder().participantsAmounts(updatedMap).payer(payer).groupId(transactionRequest.getGroupId()).amount(transactionRequest.getAmount()).description(transactionRequest.getDescription()).build();

        transactionRepository.save(transaction);

        return ResponseEntity.ok().body("Transaction created successfully");

    }


    public ResponseEntity<String> deleteTransaction(int id) {
        boolean transactionExists = transactionRepository.existsById(id);
        if (transactionExists) {
            transactionRepository.deleteById(id);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }

        return new ResponseEntity<>("transaction does not exist", HttpStatus.BAD_REQUEST);
    }

    //do the transaction part here later.
    public ResponseEntity<String> updateTransaction(SplitTransaction transaction) {

        boolean transactionExists = transactionRepository.existsById(transaction.getTransactionId());
        if (transactionExists) {

            SplitTransaction updatedTransaction = SplitTransaction.builder().transactionId(transaction.getTransactionId()).amount(transaction.getAmount()).participantsAmounts(transaction.getParticipantsAmounts()).groupId(transaction.getGroupId()).build();

            transactionRepository.save(updatedTransaction);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }

        return new ResponseEntity<>("failed to update transaction", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}