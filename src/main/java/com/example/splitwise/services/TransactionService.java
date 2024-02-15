package com.example.splitwise.services;

import com.example.splitwise.exceptions.TransactionNotFoundException;
import com.example.splitwise.exceptions.UserNotFoundException;
import com.example.splitwise.models.moneyBalance.SplitMoneyBalance;
import com.example.splitwise.models.moneyBalance.SplitMoneyBalanceId;
import com.example.splitwise.models.transaction.SplitTransaction;
import com.example.splitwise.models.transaction.SplitUserAmount;
import com.example.splitwise.models.user.SplitUser;
import com.example.splitwise.models.transaction.SplitTransactionRequest;
import com.example.splitwise.models.transaction.SplitTransactionResponse;
import com.example.splitwise.models.user.SplitUserCompressed;
import com.example.splitwise.repositories.MoneyBalanceRepository;
import com.example.splitwise.repositories.TransactionRepository;
import com.example.splitwise.repositories.UserRepository;
import jakarta.transaction.Transactional;
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

    public ResponseEntity<List<SplitTransactionResponse>> getAllTransactions() {
        List<SplitTransaction> transactions = transactionRepository.findAll();

        List<SplitTransactionResponse> response = transactions.stream().map(SplitTransactionResponse::toDto).collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public ResponseEntity<SplitTransactionResponse> getTransactionById(int id) throws TransactionNotFoundException {
        SplitTransaction transaction = transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
        SplitTransactionResponse successResponse = SplitTransactionResponse.toDto(transaction);
        return ResponseEntity.ok().body(successResponse);
    }

    @Transactional
    public ResponseEntity<String> createTransaction(SplitTransactionRequest transactionRequest) {
        //need to make this design wayyy better
        System.out.println(transactionRequest);
        //check first if the transaction is already completed or not

        boolean transactionExists = transactionRepository.existsByIdempotencyKey(transactionRequest.getIdempotencyKey());

        if (transactionExists)
            return ResponseEntity.ok().body("Transaction created successfully");

        SplitUser payer = userRepository.findById(transactionRequest.getPayerId()).orElseThrow(() -> new UserNotFoundException(transactionRequest.getPayerId()));
        List<SplitUser> participants = userRepository.findAllById(transactionRequest.getParticipantsAmounts().keySet());


        if (participants.size() != transactionRequest.getParticipantsAmounts().size()) {
            return ResponseEntity.badRequest().body("Participants ids are invalid");
        }

        Map<SplitUser, Double> updatedMap = transactionRequest.getParticipantsAmounts().entrySet().parallelStream().collect(HashMap::new, (map, entry) -> map.put(userRepository.findById(entry.getKey()).orElseThrow(() -> new UserNotFoundException(entry.getKey())), entry.getValue()), HashMap::putAll);

        participants.forEach((receiver) -> {

            //find if the entry exists
            SplitMoneyBalanceId direct = SplitMoneyBalanceId.builder().payerId(transactionRequest.getPayerId()).receiverId(receiver.getUserId()).groupId(transactionRequest.getGroupId()).build();
            SplitMoneyBalanceId inverse = direct.getReverseMapping();


            SplitMoneyBalance directBalance = moneyBalanceRepository.findById(direct).orElse(null);
            SplitMoneyBalance reverseBalance = moneyBalanceRepository.findById(inverse).orElse(null);


            double amountOwed = transactionRequest.getParticipantsAmounts().get(receiver.getUserId());

            if (directBalance == null && reverseBalance == null) {

                //we need to create here
                SplitMoneyBalance newBalance = SplitMoneyBalance.builder().amount(amountOwed).payer(payer).moneyBalanceId(new SplitMoneyBalanceId(payer.getUserId(), receiver.getUserId(), transactionRequest.getGroupId())).receiver(receiver).build();
                moneyBalanceRepository.save(newBalance);

            } else if (reverseBalance != null) {
                reverseBalance.setAmount(reverseBalance.getAmount() - amountOwed);
                moneyBalanceRepository.save(reverseBalance);
            } else {
                directBalance.setAmount(directBalance.getAmount() + amountOwed);
                moneyBalanceRepository.save(directBalance);
            }
        });

        SplitTransaction transaction = SplitTransaction.builder().idempotencyKey(transactionRequest.getIdempotencyKey()).participantsAmounts(updatedMap).payer(payer).groupId(transactionRequest.getGroupId()).amount(transactionRequest.getAmount()).description(transactionRequest.getDescription()).category(transactionRequest.getCategory()).build();

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

            SplitTransaction updatedTransaction = SplitTransaction.builder()
                    .idempotencyKey(transaction.getIdempotencyKey())
                    .transactionId(transaction.getTransactionId())
                    .amount(transaction.getAmount())
                    .participantsAmounts(transaction.getParticipantsAmounts()).groupId(transaction.getGroupId())
                    .build();

            transactionRepository.save(updatedTransaction);
            return new ResponseEntity<>("success", HttpStatus.OK);
        }

        return new ResponseEntity<>("failed to update transaction", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
